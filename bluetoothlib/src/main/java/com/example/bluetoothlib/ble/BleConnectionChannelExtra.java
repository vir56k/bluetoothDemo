package com.example.bluetoothlib.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.bluetoothlib.contract.BLueConnectionState;
import com.example.bluetoothlib.contract.ConnectionCallback;
import com.example.bluetoothlib.util.LogHelper;
import com.example.bluetoothlib.util.OutputStringUtil;
import com.example.bluetoothlib.util.SafeHandler;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * 扩展出 send and receive 方法
 * Created by zhangyunfei on 16/10/3.
 */
public class BleConnectionChannelExtra {
    private static final boolean DEBUG = false;
    private static final Object LOCK = new Object();
    private BleConnectionChannel blueToothConnectionBLE;
    private static final String TAG = "BleExtra";
    private static final int CACHE_CURSOR_START = -1;//缓存的游标,从0的前一个位置开始
    private int cacheCursor = CACHE_CURSOR_START;
    private byte[] response;
    private byte[] cache;//接收数据的缓存
    private byte mLimit;
    private Buffer bufferPool;

    public BleConnectionChannelExtra(Context context, BluetoothAdapter bluetoothAdapter, ConnectionCallback callback1) {
        if (context == null)
            throw new NullPointerException();
        if (bluetoothAdapter == null)
            throw new NullPointerException();

        ConnectionCallback bluetoothConnectionCallback = new MyBluetoothConnectionCallback(callback1);
        this.blueToothConnectionBLE = new BleConnectionChannel(context, bluetoothAdapter,
                bluetoothConnectionCallback);
        cache = new byte[1024];
    }


    public void start() {
        blueToothConnectionBLE.start();
    }

    public int getState() {
        return blueToothConnectionBLE.getState();
    }

    public void stop() {
        clearLock();
        blueToothConnectionBLE.close();
    }

    public void write(byte[] send) throws IOException {
        if (blueToothConnectionBLE == null)
            throw new IOException("未建立蓝牙连接");
        blueToothConnectionBLE.write(send);
    }

    public synchronized byte[] sendAndReceive(byte[] cmd, byte limit, int timeout) throws IOException, TimeoutException {
        Log.e(TAG, "## invoke sendAndReceive ============ threadid = " + Thread.currentThread().getName());
        if (timeout <= 500) {
            throw new InvalidParameterException("timeout 不能小于500");
        }
        if (getState() != BLueConnectionState.CONNECTION_STATE_CONNECTED) {
            throw new IOException("未建立蓝牙连接");
        }
        Calendar start = Calendar.getInstance();
        synchronized (LOCK) {
            mLimit = limit;
            response = null;

            write(cmd);
            try {
                LOCK.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            printf("##      response = " + OutputStringUtil.transferForPrint(response));
            if (response == null || response.length == 0) {
                Calendar end = Calendar.getInstance();
                long millis = (end.getTimeInMillis() - start.getTimeInMillis());
                LogHelper.e(TAG, String.format("### 指令执行超时cm=%s ,耗时%s。当前超时设定为%s,", OutputStringUtil.transferForPrint(cmd), millis, timeout));
                throw new TimeoutException("指令执行超时" + new String(cmd));
            } else {
                Calendar end = Calendar.getInstance();
                long millis = (end.getTimeInMillis() - start.getTimeInMillis());
                printLogSendAndReceive(cmd, response, millis);
                return response;
            }
        }
    }

    public void connect(String deviceAddress, boolean autoConnect) throws Exception {
        blueToothConnectionBLE.connect(deviceAddress, autoConnect);
    }

    public void ensureDiscoverable(Context activity) {
        blueToothConnectionBLE.ensureDiscoverable(activity);
    }

    public void clearLock() {
        synchronized (LOCK) {
            LOCK.notifyAll();
        }
    }

    public void disconnect() {
        blueToothConnectionBLE.disconnect();
        blueToothConnectionBLE.close();
    }

    public void close() {
        if (bufferPool != null) {
            bufferPool.release();
        }
        stop();
    }

    private Buffer getBufferPool() {
        if (bufferPool == null) {
            bufferPool = new Buffer();
        }
        return bufferPool;
    }



    private class MyBluetoothConnectionCallback implements ConnectionCallback {
        ConnectionCallback inner;

        public MyBluetoothConnectionCallback(ConnectionCallback inner) {
            this.inner = inner;
        }

        @Override
        public void onConnected(String deviceName) {
            printf("## [raise onConnected] 连接成功");
            if (inner != null) inner.onConnected(deviceName);
        }

        @Override
        public void onConnectionFailed(String error) {
            printf("## [raise onConnectionFailed] 连接失败");
            if (inner != null) inner.onConnectionFailed(error);
        }

        @Override
        public void onConnectionLost() {
            printf("## [raise onConnectionLost] 连接断开");
            if (inner != null) inner.onConnectionLost();
        }

        @Override
        public void onReadMessage(byte[] buffer) {
            Log.e(TAG, "## ============ threadid = " + Thread.currentThread().getName());
            printf(String.format("## [raise onReadMessage] %s, len=%s", OutputStringUtil.transferForPrint(buffer), (buffer == null ? 0 : buffer.length)));
            getBufferPool().offer(buffer);
        }

        @Override
        public void onWriteMessage(byte[] buffer) {
            printf(String.format("## [raise onWriteMessage] %s, len=%s", OutputStringUtil.transferForPrint(buffer), (buffer == null ? 0 : buffer.length)));
            if (inner != null) inner.onWriteMessage(buffer);
        }

        @Override
        public void onConnectStart(String deviceAddress) {
            LogHelper.d(TAG, "## onConnectStart deviceAddress = " + deviceAddress);
            if (inner != null) inner.onConnectStart(deviceAddress);
        }
    }

    private void processReadBytes(byte[] tmp) {
        Log.e(TAG, "## ============ threadid = " + Thread.currentThread().getName());
        printf("##      processReadBytes current thread " + Thread.currentThread().getId() + ", limit = " + mLimit);
        printf("##      [读取中...] data=" + OutputStringUtil.toHexString(tmp));
        try {
            if (mLimit == 0) {//每个指令都需要指定一个终结符号，没有，说明没有正在执行的指令
                printError(String.format("## [丢弃]终结符为0,data=%s", OutputStringUtil.toHexString(tmp)));
                return;
            }
//            printf( String.format("## [蓝牙]读取到数据,content=%s, len=%s", tmp == null ? "" : new String(tmp), (tmp == null ? 0 : tmp.length)));
            if (cacheCursor + tmp.length >= cache.length) {
                //"超出最大缓冲区");
                synchronized (LOCK) {
                    cacheCursor = CACHE_CURSOR_START;
                    response = tmp;
                    printf("## [读取完毕] 超出最大缓冲区");
                    LOCK.notify();
                    return;
                }
            }
            //寻找tmp中是否存在 limit结束符
            int limitPosition = -1;
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i] == mLimit) {
                    limitPosition = i;
                    break;
                }
            }
            if (limitPosition != -1) {
                printf("##      包含 limit=" + mLimit);
                System.arraycopy(tmp, 0, cache, cacheCursor + 1, tmp.length);
                cacheCursor += (limitPosition + 1);
                synchronized (LOCK) {
                    response = new byte[cacheCursor + 1];
                    System.arraycopy(cache, 0, response, 0, cacheCursor + 1);
                    if (limitPosition != tmp.length - 1) {//有剩余，tmp字节中包含有上一个结果的数据，还有下一个结果的数据
                        for (int i = limitPosition; i < tmp.length; i++) {
                            cache[cacheCursor++] = tmp[i];
                        }
                    }
                    cacheCursor = CACHE_CURSOR_START;
                    printf(String.format("## [读取完毕] %s, len=%s", OutputStringUtil.transferForPrint(response), response == null ? 0 : response.length));
                    this.mLimit = 0;
                    printf("##      change limit = " + mLimit);
                    LOCK.notify();
                }
            } else {
                System.arraycopy(tmp, 0, cache, cacheCursor + 1, tmp.length);
                cacheCursor += tmp.length;
                printf("##      [未读取完毕]，等待...");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            response = null;
            printError("##  ERR: " + ex.getMessage());
            synchronized (LOCK) {
                LOCK.notify();
            }
        }
    }

    private void printLogSendAndReceive(byte[] cmd, byte[] response, long millis) {
        if (cmd == null) {
            printf("### 响应为空");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("### 发送: %s", new String(cmd)));
        sb.append(String.format(" \t => \t 收到: %s", OutputStringUtil.transferForPrint(response)));
        sb.append(String.format(" \t长度:%s", response.length));
        sb.append(String.format(" \t耗时:%s", millis));
        LogHelper.e(TAG, OutputStringUtil.transferForPrint(sb.toString()));
    }

    protected void printf(String str) {
        if (!DEBUG) return;
        LogHelper.e(TAG, OutputStringUtil.transferForPrint(str));
    }

    protected void printError(String str) {
        if (!DEBUG) return;
        LogHelper.e(TAG, OutputStringUtil.transferForPrint(str));
    }


    private class Buffer {
        private static final int MSG_CHECK = 1;
        private HandlerThread handlerThread;
        private Queue<byte[]> queue;
        Handler handler;

        public Buffer() {
            handlerThread = new HandlerThread("Buffer_HandlerThread");
            handlerThread.start(); //创建HandlerThread后一定要记得start()
            queue = new LinkedList<byte[]>();
            handler = new MyHandler(handlerThread.getLooper(), BleConnectionChannelExtra.this);
        }

        public void release() {
            if (handlerThread != null) {
                handlerThread.quit();
                handlerThread = null;
            }
        }

        public synchronized void offer(byte[] tmp) {
            queue.offer(tmp);
            handler.obtainMessage(MSG_CHECK, tmp).sendToTarget();
        }

        public synchronized byte[] poll() {
            return queue.poll();
        }


    }


    private static class MyHandler extends SafeHandler<BleConnectionChannelExtra> {
        private static final int MSG_CHECK = 1;


        public MyHandler(Looper looper, BleConnectionChannelExtra object) {
            super(looper, object);
        }

        @Override
        public void handleMessage(Message msg) {
            if (MSG_CHECK == msg.what) {
                BleConnectionChannelExtra bleConnectionChannelExtra = getInnerObject();
                if (bleConnectionChannelExtra == null) return;
                Buffer buffer = bleConnectionChannelExtra.getBufferPool();
                if (buffer == null) return;
                while (true) {
                    byte[] bytes = buffer.poll();
                    if (bytes == null)
                        break;
                    synchronized (this) {
                        bleConnectionChannelExtra.processReadBytes(bytes);
                    }
                }
            }
            super.handleMessage(msg);
        }
    }

}
