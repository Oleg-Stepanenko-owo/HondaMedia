package com.ganet.catfish.hondamedia.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganet.catfish.hondamedia.R;

public class FM_AM_Activity extends AppCompatActivity {

    public static final String RADIOINFO = "com.ganet.catfish.ganet_service.radio";

    public enum eRadioType {
        eFM1,
        eFM2,
        eAM
    }

    public enum eRadioCommand {
        ePlay,  //0100
        eSeekR, //0400
        eSeekL, //0500
        eChange,//0110
        eNone
    }

    class RADIOData {
        eRadioCommand activeCommand;
        eRadioType currentType;
        String frequency;
        int soredId;
        int quality;

        eRadioCommand valueOf(int val) {
            switch (val){
                case 0:
                    return eRadioCommand.ePlay;
                case 1:
                    return eRadioCommand.eSeekR;
                case 2:
                    return eRadioCommand.eSeekL;
                case 3:
                    return eRadioCommand.eChange;
            }
            return eRadioCommand.eNone;
        }

        eRadioType valueTypeOf(int val) {
            switch (val){
                case 0:
                    return eRadioType.eFM1;
                case 1:
                    return eRadioType.eFM2;
                case 2:
                    return eRadioType.eAM;
            }
            return eRadioType.eFM1;
        }
    }

    BroadcastReceiver br;
    RADIOData rData;
    boolean play = true;
    TextView tvFrq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm__am);
        longClick();
        tvFrq = (TextView) findViewById(R.id.tvFrq);

        rData = new RADIOData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(RADIOINFO);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ( intent.getAction() ) {
                    case RADIOINFO:
                        rData.activeCommand = rData.valueOf( intent.getIntExtra("radioCommand", 0 ) );
                        rData.currentType = rData.valueTypeOf( intent.getIntExtra("radioType", 0 ) );
                        rData.frequency = intent.getStringExtra("radioFr");
                        rData.quality = intent.getIntExtra("radioQuality", 0 );
                        rData.soredId = intent.getIntExtra("radioStoreId", 0 );
                        updateRadioInfoUi(rData);
                        break;
                }
            }
        };
        registerReceiver(br, filter);
    }

    /**
     *
     * @param rData
     */
    private void updateRadioInfoUi(RADIOData rData) {
        tvFrq.setText(rData.frequency);
    }

    void longClick() {
        findViewById(R.id.fm1_1).setOnLongClickListener(longClick);
        findViewById(R.id.fm1_2).setOnLongClickListener(longClick);
        findViewById(R.id.fm1_3).setOnLongClickListener(longClick);
        findViewById(R.id.fm1_4).setOnLongClickListener(longClick);
        findViewById(R.id.fm1_5).setOnLongClickListener(longClick);
        findViewById(R.id.fm1_6).setOnLongClickListener(longClick);

        findViewById(R.id.fm2_1).setOnLongClickListener(longClick);
        findViewById(R.id.fm2_2).setOnLongClickListener(longClick);
        findViewById(R.id.fm2_3).setOnLongClickListener(longClick);
        findViewById(R.id.fm2_4).setOnLongClickListener(longClick);
        findViewById(R.id.fm2_5).setOnLongClickListener(longClick);
        findViewById(R.id.fm2_6).setOnLongClickListener(longClick);

        findViewById(R.id.am1).setOnLongClickListener(longClick);
        findViewById(R.id.am2).setOnLongClickListener(longClick);
        findViewById(R.id.am3).setOnLongClickListener(longClick);
        findViewById(R.id.am4).setOnLongClickListener(longClick);
        findViewById(R.id.am5).setOnLongClickListener(longClick);
        findViewById(R.id.am6).setOnLongClickListener(longClick);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev: {
                Prev();
                break;
            }
            case R.id.play_pause: {
                if (play) {
                    ((ImageView) findViewById(R.id.play_pause)).setImageResource(R.drawable.pause);
                    Play();
                } else {
                    ((ImageView) findViewById(R.id.play_pause)).setImageResource(R.drawable.play);
                    Pause();
                }
                play = !play;
                break;
            }
            case R.id.next: {
                Next();
                break;
            }
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    public void onClickRadio(View v) {
        switch (v.getTag().toString()) {
            case "fm1": {
                Fm1(v);
                break;
            }
            case "fm2": {
                Fm2(v);
                break;
            }
            case "am": {
                Am(v);
                break;
            }
        }
    }

    View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(getApplicationContext(), "long click", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    void Prev() {
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Play() {
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Pause() {
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Next() {
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Fm1(View v){
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Fm2(View v){
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }

    void Am(View v){
        Toast.makeText(this, "Подключите api", Toast.LENGTH_SHORT).show();
    }
}
