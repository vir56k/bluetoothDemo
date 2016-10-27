package com.zhangyunfei.bluetirepersuretools.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bluetoothlib.BlueToothMode;
import com.zhangyunfei.bluetirepersuretools.R;
import com.zhangyunfei.bluetirepersuretools.activity.lookservice.LookServiceActivity;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickView(View v) {
        if (v.getId() == R.id.button1) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", BlueToothMode.MODE_SIMPLE);
            startActivity(intent);
        } else if (v.getId() == R.id.button2) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", BlueToothMode.MODE_BLE);
            startActivity(intent);
        } else if (v.getId() == R.id.button3) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", BlueToothMode.MODE_AUTO);
            startActivity(intent);
        }  else if (v.getId() == R.id.btnTongbu) {
            Intent intent = new Intent(this, BluetoothTongbuActivity2.class);
            intent.putExtra("TYPE", BlueToothMode.MODE_AUTO);
            startActivity(intent);
        } else if (v.getId() == R.id.btnLookService) {
            Intent intent = new Intent(this, LookServiceActivity.class);
            startActivity(intent);
        }
    }
}
