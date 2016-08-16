package com.ganet.catfish.hondamedia.Activity;

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

public class FolderActivity extends AppCompatActivity {

    LinearLayout folders;
    TextView folder_name;
    Switch switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

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

        //Пример
        FolderManager root = new FolderManager("Root");
        FolderManager dir1 = new FolderManager("Dir1");
        FileManager file11 = new FileManager("file1");
        FileManager file12 = new FileManager("file2");
        FolderManager dir2 = new FolderManager("Dir2");
        FileManager file21 = new FileManager("file1");
        FileManager file22 = new FileManager("file2");
        FileManager file1 = new FileManager("file1");
        FileManager file2 = new FileManager("file2");

        root.addFolder(dir1);
        root.addFile(file1);
        root.addFile(file2);

        dir1.addFolder(dir2);
        dir1.addFile(file11);
        dir1.addFile(file12);

        dir2.addFile(file21);
        dir2.addFile(file21);

        //Добавление корня директории на экран
        folders.addView(root.getView(getLayoutInflater(), 0));

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
