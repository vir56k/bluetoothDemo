package obd.mapbar.com.bleperipheraldemo;

import android.text.TextUtils;

/**
 * 输出字符辅助类
 * Created by zhangyunfei on 16/8/11.
 */
public final class OutputStringUtil {

    /**
     * 指令字符里很多字符是终结符： \r ，在输出日志的无法打印，为了日志能看到，处理掉这个字符
     *
     * @param bytes 字节
     * @return 字符串
     */
    public static String transferForPrint(byte... bytes) {
        if (bytes == null)
            return null;
        return transferForPrint(new String(bytes));
    }

    /**
     * @param str 字符串
     * @return 字符串
     */
    public static String transferForPrint(String str) {
        if (TextUtils.isEmpty(str))
            return str;
        str = str.replace('\r', ' ');
        str = str.replace('\n', ' ');
        if (str.endsWith(">")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    /**
     * 转换成 16进制字符串
     *
     * @param b 字节
     * @return 字符串
     */
    private static String toHexStr(byte b) {
        String str = Integer.toHexString(0xFF & b);
        if (str.length() == 1)
            str = "0" + str;
        return str.toUpperCase();
    }

    /**
     * 转换成 16进制字符串
     *
     * @param bytes 字节
     * @return 字符串
     */
    public static String toHexString(byte... bytes) {
        if (bytes == null)
            return null;
        StringBuilder sb = new StringBuilder();
        if (bytes.length < 20) {
            sb.append("[");
            for (int i = 0; i < bytes.length; i++) {
                sb.append(toHexStr(bytes[i])).append(",");
            }
            sb.append("]");
        } else {
            sb.append("[");
            for (int i = 0; i < 4; i++) {
                sb.append(toHexStr(bytes[i])).append(",");
            }
            sb.append("...");
            for (int i = bytes.length - 5; i < bytes.length; i++) {
                sb.append(toHexStr(bytes[i])).append(",");
            }
            sb.setLength(sb.length() - 1);
            sb.append("]");
        }
        return sb.toString();
    }
}
