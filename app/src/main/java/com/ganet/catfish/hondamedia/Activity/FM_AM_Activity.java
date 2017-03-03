package com.ganet.catfish.hondamedia.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ganet.catfish.hondamedia.R;

public class FM_AM_Activity extends AppCompatActivity {
    static final String TAG = "GaNetService";
    public static final String RADIOINFO = "com.ganet.catfish.ganet_service.radio";
    int idFMAM;
    SharedPreferences sPref;

    int fm1IDs[] = { R.id.fm1_1, R.id.fm1_2, R.id.fm1_3, R.id.fm1_4, R.id.fm1_5, R.id.fm1_6 };
    int fm2IDs[] = { R.id.fm2_1, R.id.fm2_2, R.id.fm2_3, R.id.fm2_4, R.id.fm2_5, R.id.fm2_6 };
    int amIDs[] = { R.id.am1, R.id.am2, R.id.am3, R.id.am4, R.id.am5, R.id.am6 };

    String radioIDs[] = { "FM1_1", "FM1_2", "FM1_3", "FM1_4", "FM1_5", "FM1_6", "FM2_1", "FM2_2", "FM2_3", "FM2_4", "FM2_5", "FM2_6", "AM1", "AM2", "AM3", "AM4", "AM5", "AM6" };

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

        String getStrType (eRadioType currentType) {
            if( currentType == eRadioType.eFM1 )
                return "FM1";
            if( currentType == eRadioType.eFM2 )
                return "FM2";
            if( currentType == eRadioType.eAM )
                return "AM";

            return "";
        }

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
    TextView tvFrq, tvRType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idFMAM = 0;

        setContentView(R.layout.activity_fm__am);
        longClick();
        tvFrq = (TextView) findViewById(R.id.tvFrq);
        tvRType = (TextView) findViewById(R.id.tvRType);

        rData = new RADIOData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(RADIOINFO);
        filter.addAction(MainActivity.PINGINFO);

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
                        final RADIOData sendData = rData;
                        updateRadioInfoUi(sendData);
                        break;
                    case MainActivity.PINGINFO:
                        if( intent.hasExtra("Ping" ) && intent.getIntExtra( "Ping", 0 ) == MainActivity.eDevPing.eCD.ordinal() )
                            finishActiv(intent);
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
    private void finishActiv( Intent intent ) {
        setResult( RESULT_OK, intent );
        finish();
    }

    private void updateRadioInfoUi(RADIOData sendData) {
        int lastId = idFMAM;
        tvFrq.setText(sendData.frequency);
        tvRType.setText(sendData.getStrType(sendData.currentType));
        if( eRadioCommand.ePlay == rData.activeCommand ) {
            ((ImageView) findViewById(R.id.play_pause)).setImageResource(R.drawable.pause);
        } else {
            ((ImageView) findViewById(R.id.play_pause)).setImageResource(R.drawable.play);
        }

        if( sendData.soredId > 0 && sendData.soredId <= 6 ) {
            int idStore = -1;
            if( eRadioType.eFM1 == sendData.currentType ) idFMAM = fm1IDs[sendData.soredId - 1];
            else if( eRadioType.eFM2 == sendData.currentType ){ idFMAM = fm2IDs[sendData.soredId - 1]; idStore = 5; }
            else if( eRadioType.eAM == sendData.currentType ){ idFMAM = amIDs[sendData.soredId - 1]; idStore = 11; }

            ((TextView)findViewById(idFMAM)).setText(sendData.frequency);
            ((TextView)findViewById(idFMAM)).setTextColor( getResources().getColor( R.color.activeRadioColor) );
            if( (lastId != 0 && lastId != idFMAM) )
                ((TextView)findViewById(lastId)).setTextColor( getResources().getColor( R.color.primary_text_default_material_dark ) );


            idStore += sendData.soredId;

            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString( radioIDs[idStore], sendData.frequency );
            ed.commit();

        } else {
            ((TextView)findViewById(lastId)).setTextColor( getResources().getColor( R.color.primary_text_default_material_dark ) );
            Log.w( TAG, "Not in the store = " + sendData.soredId );
        }
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

        sPref = getPreferences(MODE_PRIVATE);
        for ( int a = 0; a < radioIDs.length; a++ ) {
            String savedText = sPref.getString( radioIDs[a], radioIDs[a] );
            if( a < 6 ) ((TextView)findViewById(fm1IDs[a])).setText(savedText);
            else if( a >= 6 && a < 12 ) ((TextView)findViewById(fm2IDs[a-6])).setText(savedText);
            else ((TextView)findViewById(amIDs[a-12])).setText(savedText);
        }
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
