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

    LinearLayout folders;
    TextView folder_name;
    Switch switchCompat;
    BroadcastReceiver br;

    Map< Integer, FolderManager > foldersMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        foldersMap = new TreeMap<Integer, FolderManager>();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FOLDERINFO);
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
                            final Map< Integer, FolderManager > tmpFoldersMap  = foldersMap;
                            updateFolderUi(tmpFoldersMap);
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

//        //Пример
//        FolderManager root = new FolderManager("Root");
//        FolderManager dir1 = new FolderManager("Dir1");
//        FileManager file11 = new FileManager("file1");
//        FileManager file12 = new FileManager("file2");
//        FolderManager dir2 = new FolderManager("Dir2");
//        FileManager file21 = new FileManager("file1");
//        FileManager file22 = new FileManager("file2");
//        FileManager file1 = new FileManager("file1");
//        FileManager file2 = new FileManager("file2");
//
//        root.addFolder(dir1);
//        root.addFile(file1);
//        root.addFile(file2);
//
//        dir1.addFolder(dir2);
//        dir1.addFile(file11);
//        dir1.addFile(file12);
//
//        dir2.addFile(file21);
//        dir2.addFile(file21);


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }

    private void updateFolderUi(final Map<Integer, FolderManager> tmpFoldersMap ) {
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              if( tmpFoldersMap.containsKey(new Integer(0)) ) {
                                  FolderManager root = tmpFoldersMap.get(new Integer(0));
                                  updateSubFolder( root );
                                  folders.removeAllViews();
                                  folders.addView(root.getView(getLayoutInflater(), 0));
                              }
                          }

                          private void updateSubFolder(FolderManager folder) {
                              folder.folders.clear();
                              folder.files.clear();
                              for( int a =0; a < folder.subFoldersID.size(); a++ ) {
                                    Integer id = folder.subFoldersID.get(a);
                                  if(tmpFoldersMap.containsKey(id)) {
                                      FolderManager nextFolder = tmpFoldersMap.get(id);
                                      folder.addFolder(nextFolder);
                                      updateSubFolder(nextFolder);
                                  }
                              }
                          }
                      }
        );
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
