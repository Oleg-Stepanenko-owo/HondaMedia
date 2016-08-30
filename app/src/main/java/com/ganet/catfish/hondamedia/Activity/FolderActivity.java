package com.ganet.catfish.hondamedia.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.Java.FileManager;
import com.ganet.catfish.hondamedia.Java.FolderManager;
import com.ganet.catfish.hondamedia.R;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class FolderActivity extends AppCompatActivity {

    public static final String FOLDERINFO = "com.ganet.catfish.ganet_service.folderinfo";
    public static final String FOLDERINFO_REQ = "com.ganet.catfish.ganet_service.folderinforeq";
    public static final String FOLDERBYREQ = "com.ganet.catfish.ganet_service.folderbyreq";
    public static final String FILESBYREQ = "com.ganet.catfish.ganet_service.filesbyreq";
    public static final String TRACKINFO = "com.ganet.catfish.ganet_service.track";
    public static final String DISKID = "com.ganet.catfish.ganet_service.diskid";
    public static final String ACTIVETR = "com.ganet.catfish.ganet_service.activetr";

    LinearLayout folders;
    TextView folder_name;
    Switch switchCompat;
    BroadcastReceiver br;
    boolean addedRoot;
    int currentDisk;

    Map< Integer, FolderManager > foldersMap;
    Map< Integer, FileManager > fileMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        addedRoot = false;
        currentDisk = 0;

        foldersMap = new TreeMap<Integer, FolderManager>();
        fileMap =  new TreeMap<Integer, FileManager>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(FOLDERINFO);
        filter.addAction(FOLDERBYREQ);
        filter.addAction(FILESBYREQ);
        filter.addAction(TRACKINFO);
        filter.addAction(DISKID);
        filter.addAction(ACTIVETR);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch ( intent.getAction() ){
                    case FOLDERINFO:
                        if( intent.hasExtra("FolderId") ){
                            int folderId = intent.getIntExtra("FolderId", 0);
                            int parentId = intent.getIntExtra("FolderParentId", 0);
                            String name = intent.getStringExtra("FolderName");
                            FolderManager mFolder;

                            if( foldersMap.containsKey( new Integer(folderId) ) ){
                                mFolder = foldersMap.get( new Integer(folderId) );
                            } else {
                                mFolder = new FolderManager(folderId, name);
                                foldersMap.put(folderId, mFolder );
                            }

                            mFolder.setParentId( parentId );
                            mFolder.setName( name );

                            int subFCount = intent.getIntExtra("SubFCount", 0);
                            if( subFCount != 0 ) {
                                for( int a = 0; a < subFCount; a++ ) {
                                    int sub = intent.getIntExtra("subID" + String.valueOf(a), 0);
                                    if((sub != 0) && (!mFolder.subFoldersID.contains(sub)) )
                                        mFolder.subFoldersID.add(sub);
                                }
                            }
                            {
                                final Map< Integer, FolderManager > tmpFoldersMap = foldersMap;
                                updateFolderUi(tmpFoldersMap);
                                final Map< Integer, FileManager > tmpFileMap = fileMap;
                                updateFileUi(tmpFileMap);
                            }
                        }

                        break;
                    case FOLDERBYREQ:
                    {
                        /*
                        in.putExtra( "diskID", mGANET.mActiveTrack.diskID );
                        int folderCount = mGANET.mFolder.mFoldersData.size();
                        in.putExtra( "folderCount", folderCount );
                        int a = 0;
                        for( Map.Entry<Integer, FolderData> el : mGANET.mFolder.mFoldersData.entrySet() ) {
                            final FolderData fData = el.getValue();
                            in.putExtra( "keyFolder_" + String.valueOf(a), el.getKey() );
                            in.putExtra( "parentIDFolder_" + String.valueOf(a), fData.parentID );
                            in.putExtra( "nameFolder_" + String.valueOf(a), fData.getName() );
                            in.putExtra( "calcFromFolder_" + String.valueOf(a), fData.trackCalcFrom );
                            in.putExtra( "trackCountFolder_" + String.valueOf(a), fData.trackCount );
                            int subFolders = fData.subFoldersId.size();
                            in.putExtra( "subFolderCountFolder_" + String.valueOf(a), subFolders );
                            for( int subFoldersId = 0; subFoldersId < fData.subFoldersId.size(); subFoldersId++ ) {
                                in.putExtra( "subFolderID" + subFoldersId + "CountFolder_" + String.valueOf(a), fData.subFoldersId.get(subFoldersId) );
                            }
                         */
                        if( intent.hasExtra("diskID") && intent.hasExtra("folderCount") ) {
                            for( int fCount = 0; fCount < intent.getIntExtra("folderCount", 0); fCount++ ) {
                                int folderID = intent.getIntExtra("keyFolder_" + fCount, 0);
                                String folderName = intent.getStringExtra("nameFolder_" + fCount );
                                FolderManager mFolder;
                                if( foldersMap.containsKey( new Integer(folderID) ) ){
                                    mFolder = foldersMap.get( new Integer(folderID) );
                                } else {
                                    mFolder = new FolderManager(folderID, folderName);
                                    foldersMap.put(folderID, mFolder );
                                }

                                mFolder.setParentId( intent.getIntExtra("parentIDFolder_" + fCount, 0) );
                                mFolder.calcFrom = intent.getIntExtra("calcFromFolder_" + fCount, 0);
                                mFolder.filesCount = intent.getIntExtra("trackCountFolder_" + fCount, 0);
                                for( int subF = 0; subF < intent.getIntExtra("subFolderCountFolder_" + fCount, 0); subF++ ) {
                                    int subFolderID = intent.getIntExtra( "subFolderID" + subF + "CountFolder_" + fCount, 0);
                                    if( !mFolder.subFoldersID.contains( subFolderID ) )
                                    mFolder.subFoldersID.add( subFolderID );
                                }
                            }
                        }
                        final Map< Integer, FolderManager > tmpFoldersMap  = foldersMap;
                        updateFolderUi(tmpFoldersMap);
                    }
                    break;
                    case FILESBYREQ:
                    {
                        /*
                        in.putExtra( "fileCount", fileCount );
                        a = 0;
                        for( Map.Entry<Integer, Track> trEl : trackMap.entrySet() ) {
                            Track currTr = trEl.getValue();
                            in.putExtra( "trackId_" + String.valueOf(a), currTr.getTrackId() );
                            in.putExtra( "trackFId_" + String.valueOf(a), currTr.getFolderId() );
                            in.putExtra( "trackName_"+ String.valueOf(a), currTr.getName() );
                            in.putExtra( "trackSelect_"+ String.valueOf(a), currTr.selectedTrack );
                        }

                        */
                        if( intent.hasExtra("fileCount") ) {
                            for( int fCount = 0; fCount < intent.getIntExtra("fileCount", 0); fCount++ ) {
                                int trID = intent.getIntExtra("trackId_" + fCount, 0);
                                int trFID = intent.getIntExtra("trackFId_" + fCount, 0);
                                String trName = intent.getStringExtra("trackName_" + fCount);
                                boolean rtIsSelect = intent.getBooleanExtra("trackSelect_" + fCount,false);

                                FileManager mFileTr;
                                if( fileMap.containsKey( new Integer(trID) ) ){
                                    mFileTr = fileMap.get( new Integer(trID) );
                                } else {
                                    mFileTr = new FileManager(trID, trName);
                                    fileMap.put(trID, mFileTr );
                                }
                                mFileTr.folderId = trFID;
                                mFileTr.isSelect = rtIsSelect;
                            }
                        }

                        final Map< Integer, FileManager > tmpFileMap = fileMap;
                        updateFileUi(tmpFileMap);
                        break;
                    }
                    case TRACKINFO:
                        if( intent.hasExtra("TrackId") ) {
                            int trackId = intent.getIntExtra("TrackId", 0);
                            int folderId = intent.getIntExtra("FolderId", 0);
                            boolean isSelect = intent.getBooleanExtra("selected", false);
                            String name = intent.getStringExtra("TrackName");

                            if( fileMap.containsKey(trackId) ) {
                                final FileManager fm = fileMap.get(trackId);
                                fileMap.remove(trackId);
                                fm.isSelect = isSelect;
                                fm.folderId = folderId;
                                if( !fm.getName().isEmpty())
                                    fm.setName(name);
                                fileMap.put(trackId, fm);
                            } else {
                                FileManager fm = new FileManager(trackId, name);
                                fm.isSelect = isSelect;
                                fm.folderId = folderId;
                                fileMap.put(trackId, fm);
                            }
                        }
                        final Map< Integer, FileManager > tmpFileMap = fileMap;
                        updateFileUi(tmpFileMap);
                        break;
                    case DISKID:
                        foldersMap.clear();
                        fileMap.clear();
                        if( intent.hasExtra("diskID") ) {
                            currentDisk = intent.getIntExtra("diskID", 0);
                        }
                        ((TextView) findViewById(R.id.folder_name)).setText( "DISK#" + currentDisk );
                        cleanFolderUi();
                        break;
                    case ACTIVETR:
                        if( currentDisk == 0 ) {
                            if( intent.hasExtra("diskID") ) {
                                currentDisk = intent.getIntExtra("diskID", 0);
                                ((TextView) findViewById(R.id.folder_name)).setText( "DISK#" + currentDisk );
                            }
                        }
                        break;
                }
            }
        };
        registerReceiver(br, filter);

        folders = (LinearLayout) findViewById(R.id.folder);
        folder_name = (TextView) findViewById(R.id.folder_name);
        switchCompat = (Switch) findViewById(R.id.is_folder);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    File();
                }
                else {
                    Folder();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(foldersMap.size() == 0)
        {
            Intent in = new Intent(FOLDERINFO_REQ);
            sendBroadcast(in);
        }
    }


    private void cleanFolderUi( ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                folders.removeAllViews();
            }
        });
    }
                    //TODO: if is files, after updating also add its.
    //TODO: Main folder without name.
    //TODO: Activity head not actual.
    private void updateFolderUi(final Map<Integer, FolderManager> tmpFoldersMap ) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              if( tmpFoldersMap.containsKey(new Integer(0)) ) {
                                  FolderManager root = tmpFoldersMap.get(new Integer(0));
                                  updateSubFolder( root );
                                  folders.removeView(root.getView(getLayoutInflater(), 0));
                                  folders.addView(root.getView(getLayoutInflater(), 0));
                              }
                          }

                          private void updateSubFolder(FolderManager folder) {
                              for( int a = 0; a < folder.subFoldersID.size(); a++ ) {
                                    Integer id = folder.subFoldersID.get(a);
                                  if(tmpFoldersMap.containsKey(id) ) {
                                      boolean isExistFolder = false;
                                      for( int b = 0; b < folder.folders.size(); b++ )
                                      {
                                          if( ((FolderManager)folder.folders.get(b)).getId() == id ){
                                              isExistFolder = true;
                                              break;
                                          }
                                      }
                                      FolderManager nextFolder = tmpFoldersMap.get(id);
                                      if( !isExistFolder ) folder.addFolder(nextFolder);
                                      updateSubFolder(nextFolder);
                                  }
                              }
                          }
                      }
        );
    }

    //TODO: added request for automaticly updating new files in list
    //TODO: select currently active(play selected) track
    private void updateFileUi( final Map<Integer, FileManager> tmpFileMap ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for( Map.Entry<Integer, FileManager> trEl : tmpFileMap.entrySet() ) {
                    FileManager tmpFile = trEl.getValue();
                    int folderID = tmpFile.folderId;
                    if( foldersMap.containsKey( folderID ) ){
                        if( !foldersMap.get(folderID).filesID.contains( tmpFile.id ) ) {
                            foldersMap.get(folderID).addFile( tmpFile );
                            foldersMap.get(folderID).filesID.add( tmpFile.id );
                        } else {
                            foldersMap.get(folderID).addFile( tmpFile );
                        }
                    }
                }

                if( foldersMap.containsKey(new Integer(0)) )
                {
                    folders.removeAllViews();
                    folders.addView( (foldersMap.get(new Integer(0))).getView(getLayoutInflater(), 0));
                }
            }
        });
    }

    void File() {

    }

    void Folder() {

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back: {
                finish();
                break;
            }
        }
    }

    void setFolderName(String str){
        folder_name.setText(str);
    }

    String getFolderName(){
        return  folder_name.getText().toString();
    }
}
