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

import com.ganet.catfish.hondamedia.Java.ActiveTrackInfo;
import com.ganet.catfish.hondamedia.R;

public class CD_DVD_Activity extends AppCompatActivity {

    BroadcastReceiver br;
    ActiveTrackInfo atInfo;

    boolean play = true;
    TextView track;
    TextView albom;
    TextView disk;
    TextView tvAlbome, tvDisk, tvTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cd__dvd);

        atInfo = new ActiveTrackInfo();

        track = (TextView) findViewById(R.id.track);
        albom = (TextView) findViewById(R.id.albom);
        disk = (TextView) findViewById(R.id.disk);
        tvAlbome = (TextView) findViewById(R.id.tvAlbom);
        tvDisk = (TextView) findViewById(R.id.tvDisk);
        tvTime = (TextView) findViewById(R.id.tvTime1);

        IntentFilter filter = new IntentFilter();

        filter.addAction(MainActivity.ACTIVETR);
        filter.addAction(MainActivity.STARTCDINFO_RES);
        filter.addAction(MainActivity.PINGINFO);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ( intent.getAction() ){
                    case MainActivity.ACTIVETR:

                        if( atInfo.diskID != intent.getIntExtra("diskID", 0 )){
                            atInfo.folderId = -1;
                            atInfo.trackId = 0;
                            atInfo.playTime = "00:00";
                            atInfo.diskID = intent.getIntExtra("diskID", 0 );
                            atInfo.playTrackName = "";
                            atInfo.playAlbome = "";
                        } else if ( atInfo.trackId != intent.getIntExtra("trackId", 0 ) ) {
                            atInfo.folderId = intent.getIntExtra("folderId", 0 );
                            atInfo.trackId = intent.getIntExtra("trackId", 0 );
                            atInfo.playTime = intent.getStringExtra("playTrackTime");
                            atInfo.playTrackName = intent.getStringExtra("playTrackName");
                            atInfo.playAlbome = intent.getStringExtra("playAlbome");
                        } else {
                            atInfo.folderId = intent.getIntExtra("folderId", 0 );
                            atInfo.trackId = intent.getIntExtra("trackId", 0 );
                            atInfo.playTime = intent.getStringExtra("playTrackTime");
                            atInfo.playTrackName = intent.getStringExtra("playTrackName");
                            atInfo.playAlbome = intent.getStringExtra("playAlbome");
                        }
                        updateATrackInfoUi(atInfo);
                        break;
                    case MainActivity.STARTCDINFO_RES:
                        atInfo.diskID = intent.getIntExtra("diskID", 0 );
                        atInfo.folderId = intent.getIntExtra("folderId", 0 );
                        atInfo.trackId = intent.getIntExtra("trackId", 0 );
                        atInfo.playTime = intent.getStringExtra("playTrackTime");
                        atInfo.playTrackName = intent.getStringExtra("playTrackName");
                        break;

                    case MainActivity.PINGINFO:
                        if( intent.hasExtra("Ping" ) && intent.getIntExtra( "Ping", 0 ) > MainActivity.eDevPing.eCD.ordinal() )
                            finishActiv(intent);
                        break;
                }
            }
        };
        registerReceiver(br, filter);
//-----
        Intent in = new Intent(MainActivity.STARTCDINFO_REQ);
        sendBroadcast(in);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(br);
    }

    private void updateATrackInfoUi(ActiveTrackInfo atInfo) {
        tvTime.setText( atInfo.playTime );
        tvDisk.setText( String.valueOf( atInfo.diskID ) );
        if( atInfo.trackId != 0 ){
            track.setText( String.valueOf(atInfo.trackId) + ": "+ atInfo.playTrackName );
            tvAlbome.setText( atInfo.playAlbome );
        } else {
            track.setText("");
            tvAlbome.setText("");
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
            case R.id.play_list: {
                Intent intent = new Intent(this, FolderActivity.class);
                startActivityForResult(intent, 2);
                break;
            }
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    private void finishActiv( Intent intent ) {
        setResult( RESULT_OK, intent );
        finish();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if ( data == null || RESULT_OK != resultCode ) { return; }
        int finWithResult = data.getIntExtra( "Ping", MainActivity.eDevPing.eNone.ordinal() );

        if ( finWithResult > MainActivity.eDevPing.eCD.ordinal() ) finishActiv(data);
    }

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

    void setTrack(String str){
        track.setText(str);
    }

    void setAlbom(String str){
        albom.setText(str);
    }

    void setDisk(String str){
        disk.setText(str);
    }

    String getTrack() {
        return track.getText().toString();
    }

    String getAlbom() {
        return albom.getText().toString();
    }

    String getDisk() {
        return disk.getText().toString();
    }
}
