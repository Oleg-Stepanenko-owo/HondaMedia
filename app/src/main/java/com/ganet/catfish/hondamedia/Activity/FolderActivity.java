package com.ganet.catfish.hondamedia.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.Java.FileManager;
import com.ganet.catfish.hondamedia.Java.FolderManager;
import com.ganet.catfish.hondamedia.R;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class FolderActivity extends AppCompatActivity {
    public static final String TAG = "GaNetService";

    public static final String FOLDERINFO = "com.ganet.catfish.ganet_service.folderinfo";
    public static final String FOLDERINFO_REQ = "com.ganet.catfish.ganet_service.folderinforeq";
    public static final String FOLDERBYREQ = "com.ganet.catfish.ganet_service.folderbyreq";
    public static final String FILESBYREQ = "com.ganet.catfish.ganet_service.filesbyreq";
    public static final String TRACKINFO = "com.ganet.catfish.ganet_service.track";
    public static final String DISKID = "com.ganet.catfish.ganet_service.diskid";
    public static final String ACTIVETR = "com.ganet.catfish.ganet_service.activetr";

    LinearLayout rootFolder;
    TextView folder_name;
    Switch switchCompat;
    BroadcastReceiver br;
    boolean addedRoot;
    int currentDisk;

    Map< Integer, FolderManager > foldersMap;
    Map< Integer, FileManager > fileMap;
    Vector<Integer> noFolderTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
        addedRoot = false;
        currentDisk = 0;

        foldersMap = new TreeMap<Integer, FolderManager>();
        fileMap =  new TreeMap<Integer, FileManager>();
        noFolderTrack = new Vector<Integer>();

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

        rootFolder = (LinearLayout) findViewById(R.id.folder);
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
                rootFolder.removeAllViews();
            }
        });
    }

    //TODO: Main folder without name.
    //TODO: Activity head not actual.
    private void updateFolderUi(final Map<Integer, FolderManager> tmpFoldersMap ) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              if( tmpFoldersMap.containsKey(new Integer(0)) ) {
                                  FolderManager root = tmpFoldersMap.get(new Integer(0));
                                  updateSubFolder( root );

                                  for( int a = 0; a < root.subFoldersID.size(); a++ ) {
                                      if( rootFolder.findViewWithTag( ((FolderManager)foldersMap.get( root.subFoldersID.get(a))).getTag() ) == null ) {
                                          rootFolder.addView( root.getView(getLayoutInflater(), 0) );
                                      }
                                  }
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

    //TODO: select currently active(play selected) track
    private void updateFileUi( final Map<Integer, FileManager> tmpFileMap ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for( Map.Entry<Integer, FileManager> trEl : tmpFileMap.entrySet() ) {
                    FileManager tmpFile = trEl.getValue();
                    int folderID = tmpFile.folderId;

                    if( !foldersMap.containsKey( 0 ) ) {
                        FolderManager mFolder = new FolderManager(0, "");
                        foldersMap.put(folderID, mFolder );
                    }

                    if( foldersMap.containsKey( folderID ) ) {
                        int idx = noFolderTrack.indexOf( new Integer(tmpFile.id) );
                        if( -1 != idx ) {
                            foldersMap.get(0).filesID.remove( new Integer(tmpFile.id) );
                            foldersMap.get(0).files.remove( tmpFile );
                            noFolderTrack.remove(idx);
                            rootFolder.removeView( rootFolder.findViewWithTag( tmpFile.getTag() ) );

                            Log.d(TAG, "Remove unDefTrack ID " + tmpFile.id );
                        }

                        if( !foldersMap.get(folderID).filesID.contains( tmpFile.id ) )
                            foldersMap.get(folderID).filesID.add( tmpFile.id );
                        foldersMap.get(folderID).addFile( tmpFile );
                    } else {
                        noFolderTrack.add( tmpFile.id );
                        Log.w( TAG, "Added unFolderTrack ID:" + tmpFile.id );
                        // added in root
                        foldersMap.get(0).filesID.add( tmpFile.id );
                        foldersMap.get(0).addFile( tmpFile );
                    }
                }

                if( foldersMap.containsKey(new Integer(0)) ) {
                    rootFolder.removeAllViews();

                    FolderManager root = foldersMap.get(new Integer(0));

                    for( int a = 0; a < root.subFoldersID.size(); a++ ) {
                        if( rootFolder.findViewWithTag( ((FolderManager)foldersMap.get( root.subFoldersID.get(a))).getTag() ) == null ) {
                            rootFolder.addView( root.getView(getLayoutInflater(), 0) );
                        }
                        rootFolder.addView( (foldersMap.get(new Integer(0))).getView(getLayoutInflater(), 0));
                    }

                    for( int a = 0; a < root.files.size(); a++ ) {
                        if( rootFolder.findViewWithTag( ((FileManager)root.files.get(a).getTag() )) == null ) {
                            rootFolder.addView( root.files.get(a).getView(getLayoutInflater(), 0) );
                        }
                    }

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
