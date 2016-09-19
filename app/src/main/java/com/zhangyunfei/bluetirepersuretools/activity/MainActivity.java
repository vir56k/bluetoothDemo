package com.zhangyunfei.bluetirepersuretools.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zhangyunfei.bluetirepersuretools.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickView(View v) {
        if (v.getId() == R.id.button1) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", 1);
            startActivity(intent);
        } else if (v.getId() == R.id.button2) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", 2);
            startActivity(intent);
        } else if (v.getId() == R.id.button3) {
            Intent intent = new Intent(this, BluetoothDemoActivity2.class);
            intent.putExtra("TYPE", 3);
            startActivity(intent);
        }
    }
}
