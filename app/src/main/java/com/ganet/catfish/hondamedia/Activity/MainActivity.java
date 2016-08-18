package com.ganet.catfish.hondamedia.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ganet.catfish.hondamedia.R;

public class MainActivity extends AppCompatActivity {

    public static final String TIMEINFO = "com.ganet.catfish.ganet_service.timeinfo";
    public static final String ACTIVETR = "com.ganet.catfish.ganet_service.activetr";
    public static final String TRACKINFO = "com.ganet.catfish.ganet_service.track";
    public static final String FOLDERINFO = "com.ganet.catfish.ganet_service.folderinfo";
    public static final String STARTCDINFO_REQ = "com.ganet.catfish.ganet_service.startsdreq";
    public static final String STARTCDINFO_RES = "com.ganet.catfish.ganet_service.startsdres";

    BroadcastReceiver br;
    TextView tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();

        filter.addAction(TIMEINFO);
        filter.addAction(ACTIVETR);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ( intent.getAction() ){
                    case TIMEINFO:
                        updateTimeUi(intent.getStringExtra("Time"));
                        break;
                }
            }
        };
        registerReceiver(br, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cd_dvd:{
                Intent intent = new Intent(this, CD_DVD_Activity.class);
                startActivity(intent);
                break;
            }
            case R.id.fm_am:{
                Intent intent = new Intent(this, FM_AM_Activity.class);
                startActivity(intent);
                break;
            }
        }
    }

    public void updateTimeUi( final String timeUI ) {
        tvTime = (TextView) findViewById(R.id.tvTime);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              tvTime.setText( timeUI );
                          }
                      }
        );
    }
}
