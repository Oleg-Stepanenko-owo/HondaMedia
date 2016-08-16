package com.ganet.catfish.hondamedia.Java;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.R;

public class FileManager {

    String name;
    Object tag;

    public FileManager(String _name) {
        name = _name;
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

    public View getView(LayoutInflater inflater, int attachment) {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.inflate_file, null);
        ((TextView) v.findViewById(R.id.file_name)).setText(name);

        if (attachment > 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins((attachment * 16) + 8, 0, 0, 0);
            v.setLayoutParams(layoutParams);
        }

        v.setTag(tag);
        v.setOnClickListener(FileOnClick);

        return v;
    }

    public View.OnClickListener FileOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
}
