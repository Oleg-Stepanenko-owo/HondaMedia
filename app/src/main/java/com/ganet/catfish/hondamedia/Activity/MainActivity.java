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
    public static final String VOLUMEINFO = "com.ganet.catfish.ganet_service.volumeinfo";
    public static final String PINGINFO = "com.ganet.catfish.ganet_service.pinginfo";

    static public enum eDevPing
    {
        eNone,
        eCD,
        eFM1,
        eFM2,
        eAM
    }

    BroadcastReceiver br;
    TextView tvTime;
    private boolean isActiveChildScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isActiveChildScreen = false;

        IntentFilter filter = new IntentFilter();

        filter.addAction(TIMEINFO);
        filter.addAction(ACTIVETR);
        filter.addAction(VOLUMEINFO);
        filter.addAction(PINGINFO);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ( intent.getAction() ){
                    case TIMEINFO:
                        updateTimeUi(intent.getStringExtra("Time"));
                        break;
                    case VOLUMEINFO:
                        if( intent.hasExtra("VOL") )  {
                            CharSequence text = String.valueOf(intent.getIntExtra("VOL", 0));
                            Toast.makeText( context, "Volume:" + text, Toast.LENGTH_SHORT ).show();
                        }
                        break;
                    case PINGINFO:
                        if( !isActiveChildScreen && intent.hasExtra("Ping") ) {

                            final int pingID = intent.getIntExtra( "Ping", eDevPing.eNone.ordinal() );
                            if( pingID == eDevPing.eCD.ordinal() ) {
                                Intent activityIn = new Intent( getApplicationContext(), CD_DVD_Activity.class);
                                startActivityForResult(activityIn, 1);
                                isActiveChildScreen = true;
                            } else if ( pingID > eDevPing.eCD.ordinal() ) {
                                Intent activityIn = new Intent( getApplicationContext(), FM_AM_Activity.class);
                                startActivityForResult(activityIn, 1);
                                isActiveChildScreen = true;
                            }
                        }
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

//    @Override
//    protected void onResume() {
//        isActiveChildScreen = false;
//        super.onResume();
//    }

    public void onClick(View v) {
        switch ( v.getId() ){
            case R.id.cd_dvd:{
                Intent intent = new Intent(this, CD_DVD_Activity.class);
                startActivityForResult(intent, 1);
                break;
            }
            case R.id.fm_am:{
                Intent intent = new Intent(this, FM_AM_Activity.class);
                startActivityForResult(intent, 1);
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

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if ( data == null || RESULT_OK != resultCode ) { return; }
        int finWithResult = data.getIntExtra( "Ping", eDevPing.eNone.ordinal() );

        if( finWithResult == eDevPing.eCD.ordinal() ) {
            Intent activityIn = new Intent( getApplicationContext(), CD_DVD_Activity.class );
            startActivityForResult(activityIn, 1);
            isActiveChildScreen = true;
        } else if ( finWithResult > eDevPing.eCD.ordinal() ) {
            Intent activityIn = new Intent( getApplicationContext(), FM_AM_Activity.class );
            startActivityForResult(activityIn, 1);
            isActiveChildScreen = true;
        }
    }
}
