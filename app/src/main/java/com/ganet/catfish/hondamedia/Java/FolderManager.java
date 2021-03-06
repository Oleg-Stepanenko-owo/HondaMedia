package com.ganet.catfish.hondamedia.Java;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class FolderManager {

    private int id, parentId;
    public int calcFrom, filesCount;
    String name;
    Object tag;
    public Vector< Integer > subFoldersID;
    public Vector< Integer > filesID;

    public ArrayList<FileManager> files;
    public ArrayList<FolderManager> folders;

    public FolderManager(int id, String name) {
        files = new ArrayList<>();
        folders = new ArrayList<>();

        subFoldersID = new Vector<Integer>();
        filesID = new Vector<Integer>();

        this.name = name;
        this.id = id;
        calcFrom = 0;
        filesCount = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getTag() {
        return tag;
    }

    public void addFile(FileManager file) {
        files.remove(file);
        files.add(file);
    }

    public void addFolder(FolderManager folder) {
        folders.add(folder);
    }

    public View getView(LayoutInflater inflater, int attachment) {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.inflate_folder, null);
        ((TextView) v.findViewById(R.id.folder_name)).setText(name);

        if (attachment > 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((attachment * 16) + 8, 0, 0, 0);
            v.setLayoutParams(layoutParams);
        }

        LinearLayout children = (LinearLayout) v.findViewById(R.id.childFolder);
        for (FolderManager el : folders) {
            children.addView(el.getView(inflater, attachment + 1));
        }
        for (FileManager el : files) {
            children.addView(el.getView(inflater, attachment + 1));
        }

        v.setTag(tag);
        v.setOnClickListener(FolderOnClick);

        return v;
    }

    public View.OnClickListener FolderOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = v.findViewById(R.id.childFolder);
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
        }
    };

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    public int getId() {
        return id;
    }
}
