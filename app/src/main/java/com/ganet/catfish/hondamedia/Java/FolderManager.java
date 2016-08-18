package com.ganet.catfish.hondamedia.Java;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.R;

import java.util.ArrayList;

public class FolderManager {

    private int id, parentId;
    String name;
    Object tag;
    ArrayList<FileManager> files;
    ArrayList<FolderManager> folders;

    public FolderManager(int id, String _name) {
        files = new ArrayList<>();
        folders = new ArrayList<>();
        name = _name;
        this.id = id;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void setTag(String _tag) {
        tag = _tag;
    }

    public Object getTag() {
        return tag;
    }

    public void addFile(FileManager file) {
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

        LinearLayout children = (LinearLayout) v.findViewById(R.id.children);
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
            View view = v.findViewById(R.id.children);
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
}
