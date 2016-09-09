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

import com.ganet.catfish.hondamedia.Java.TrackTree;
import com.ganet.catfish.hondamedia.R;

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

    TrackTree tree;

    Vector<Integer> noFolderTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);
//        rootFolder = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_folder, null);

        addedRoot = false;
        currentDisk = 0;

        tree = new TrackTree(getLayoutInflater());
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
                Log.d(TAG, "onReceive: " + intent.getAction() );
                switch ( intent.getAction() ){
                    case FOLDERINFO:
                        if( intent.hasExtra("FolderId") ){
                            int folderId = intent.getIntExtra("FolderId", 0);
                            int parentId = intent.getIntExtra("FolderParentId", 0);
                            String name = intent.getStringExtra("FolderName");

                            Log.d( TAG, "FolderId Id:" + folderId + "; ParentId:" + parentId + "; Name:" + name );

                            TrackTree.GaItem item = tree.updateFolder(folderId, parentId, name );

                            int subFCount = intent.getIntExtra("SubFCount", 0);
                            if( subFCount != 0 ) {
                                for( int a = 0; a < subFCount; a++ ) {
                                    int sub = intent.getIntExtra("subID" + String.valueOf(a), 0);
                                    if( sub != 0 )
                                        tree.addSubFoldersID(item, sub);
                                }
                            }
                            tree.resolveTrack( rootFolder );
                            final TrackTree UI_Tree = tree;
                            updateTreeUi(UI_Tree);
                        }

                        break;
                    case FOLDERBYREQ:
                    {
                        if( intent.hasExtra("diskID") && intent.hasExtra("folderCount") ) {
                            for( int fCount = 0; fCount < intent.getIntExtra("folderCount", 0); fCount++ ) {
                                int folderID = intent.getIntExtra("keyFolder_" + fCount, 0);
                                String folderName = intent.getStringExtra("nameFolder_" + fCount );
                                int parentId = intent.getIntExtra("parentIDFolder_" + fCount, 0);

                                TrackTree.GaItem item = tree.updateFolder(folderID, parentId, folderName );
                                tree.setItemCalcFrom( item, intent.getIntExtra("calcFromFolder_" + fCount, 0), TrackTree.E_ITEMTYPE.e_folder);
                                tree.setFolderTrackCount(item, intent.getIntExtra("trackCountFolder_" + fCount, 0));

                                for( int subF = 0; subF < intent.getIntExtra("subFolderCountFolder_" + fCount, 0); subF++ ) {
                                    tree.addSubFoldersID(item, intent.getIntExtra( "subFolderID" + subF + "CountFolder_" + fCount, 0) );
                                }
                            }
                        }
                        final TrackTree UI_Tree = tree;
                        updateTreeUi(UI_Tree);
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

                                TrackTree.GaItem item = tree.updateFile(trID, trFID, trName );
                                tree.setItemIsSelected( item, rtIsSelect, TrackTree.E_ITEMTYPE.e_file );
                            }
                        }
                        final TrackTree UI_Tree = tree;
                        updateTreeUi(UI_Tree);
                        break;
                    }
                    case TRACKINFO:
                        if( intent.hasExtra("TrackId") ) {
                            int trackId = intent.getIntExtra("TrackId", 0);
                            int folderId = intent.getIntExtra("FolderId", 0);
                            boolean isSelect = intent.getBooleanExtra("selected", false);
                            String name = intent.getStringExtra("TrackName");

                            Log.d( TAG, "Track Id:" + trackId + "; FolderID:" + folderId + "; Name:" + name );
                            TrackTree.GaItem item  = tree.updateFile( trackId, folderId, name );
                            tree.setItemIsSelected(item, isSelect, TrackTree.E_ITEMTYPE.e_file);
                        }
                        final TrackTree UI_Tree = tree;
                        updateTreeUi(UI_Tree);
                        break;
                    case DISKID:
                        tree.clear();
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
        Intent in = new Intent(FOLDERINFO_REQ);
        sendBroadcast(in);

    }

    private void cleanFolderUi( ) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rootFolder.removeAllViews();
            }
        });
    }

    private void updateTreeUi( final TrackTree treeUI ) {
        runOnUiThread(new Runnable() {
                          private int _attachment;
                            final private LinearLayout llRootFolder = rootFolder;
                          @Override
                          public void run() {
                              _attachment = 0;
                              updateSubFolder( treeUI.getRoot(), _attachment );
                          }

                          private void updateSubFolder( TrackTree.GaItem gaRootFolderItem, int attachment ) {
                              for( int a = 0; a < gaRootFolderItem.subItemIds.size(); a++ ) {
                                  TrackTree.GaItem subItem = gaRootFolderItem.subItemIds.get(a);

                                  updateSubFolder(subItem, attachment+1);

                                  LinearLayout view;

                                  LinearLayout llItemChFolder = llRootFolder;
                                  if(attachment != 0) llItemChFolder = (LinearLayout) gaRootFolderItem.getLLView().findViewById(R.id.childFolder);

                                  subItem.updateAttacheLevel( attachment + 1 );

                                  int rId = 0;
                                  if( attachment == 0 || subItem.getType() == TrackTree.E_ITEMTYPE.e_folder ) {
                                      rId  =  subItem.getType() == TrackTree.E_ITEMTYPE.e_folder ? R.id.folder_name : R.id.file_name;

                                      if( llItemChFolder.findViewWithTag( subItem.getTag() ) != null ) { // update text
                                          view = (LinearLayout) llItemChFolder.findViewWithTag(subItem.getTag());
                                          ((TextView) view.findViewById(rId)).setText(subItem.getName());
                                      } else {
                                          view = subItem.getLLView();
                                          ((TextView) view.findViewById(rId)).setText(subItem.getName());
                                          Log.d(TAG, "updateSubFolder(" + attachment + ") ChildFolder add - " + subItem.getName());
                                          llItemChFolder.addView( subItem.getLLView() );
                                      }
                                  } else {
                                      rId = R.id.file_name;
                                      if( gaRootFolderItem.getLLView().findViewWithTag( subItem.getTag() ) != null ) { // find in file block
                                          view = (LinearLayout) gaRootFolderItem.getLLView().findViewWithTag(subItem.getTag());
                                          ((TextView) view.findViewById(rId)).setText(subItem.getName());
                                      } else {
                                          view = subItem.getLLView();
                                          ((TextView) view.findViewById(rId)).setText(subItem.getName());
                                          Log.d(TAG, "updateSubFolder(" + attachment + ") ChildFile add - " + subItem.getName());
                                          ((LinearLayout) gaRootFolderItem.getLLView().getChildAt(1)).addView( view );
                                      }
                                  }
                              }
                          }
                      }
        );
    }

    //TODO: select currently active(play selected) track
//    private void updateFileUi( final Map<Integer, FileManager> tmpFileMap ) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for( Map.Entry<Integer, FileManager> trEl : tmpFileMap.entrySet() ) {
//                    FileManager tmpFile = trEl.getValue();
//                    int folderID = tmpFile.folderId;
//
//                    if( !foldersMap.containsKey( 0 ) ) {
//                        FolderManager mFolder = new FolderManager(0, "");
//                        foldersMap.put(folderID, mFolder );
//                    }
//
//                    if( foldersMap.containsKey( folderID ) ) {
//                        int idx = noFolderTrack.indexOf( new Integer(tmpFile.id) );
//                        if( -1 != idx ) {
//                            foldersMap.get(0).filesID.remove( new Integer(tmpFile.id) );
//                            foldersMap.get(0).files.remove( tmpFile );
//                            noFolderTrack.remove(idx);
//                            rootFolder.removeView( rootFolder.findViewWithTag( tmpFile.getTag() ) );
//
//                            Log.d(TAG, "Remove unDefTrack ID " + tmpFile.id );
//                        }
//
//                        if( !foldersMap.get(folderID).filesID.contains( tmpFile.id ) )
//                            foldersMap.get(folderID).filesID.add( tmpFile.id );
//                        foldersMap.get(folderID).addFile( tmpFile );
//                    } else {
//                        noFolderTrack.add( tmpFile.id );
//                        Log.w( TAG, "Added unFolderTrack ID:" + tmpFile.id );
//                        // added in root
//                        foldersMap.get(0).filesID.add( tmpFile.id );
//                        foldersMap.get(0).addFile( tmpFile );
//                    }
//                }
//
//                if( foldersMap.containsKey(new Integer(0)) ) {
//                    rootFolder.removeAllViews();
//
//                    FolderManager root = foldersMap.get(new Integer(0));
//
//                    for( int a = 0; a < root.subFoldersID.size(); a++ ) {
//                        if( rootFolder.findViewWithTag( ((FolderManager)foldersMap.get( root.subFoldersID.get(a))).getTag() ) == null ) {
//                            rootFolder.addView( root.getView(getLayoutInflater(), 0) );
//                        }
//                        rootFolder.addView( (foldersMap.get(new Integer(0))).getView(getLayoutInflater(), 0));
//                    }
//
//                    for( int a = 0; a < root.files.size(); a++ ) {
//                        if( rootFolder.findViewWithTag( ((FileManager)root.files.get(a).getTag() )) == null ) {
//                            rootFolder.addView( root.files.get(a).getView(getLayoutInflater(), 0) );
//                        }
//                    }
//
//                }
//            }
//        });
//    }

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
