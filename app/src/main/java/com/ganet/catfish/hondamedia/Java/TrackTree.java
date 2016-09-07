package com.ganet.catfish.hondamedia.Java;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ganet.catfish.hondamedia.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by oleg on 01.09.2016.
 */
public class TrackTree {

    public void addSubFoldersID(GaItem gaItem , int sub) {
        boolean isIdCondain = false;
        for(int a = 0; a < gaItem.subItemIds.size(); a++) {
            GaItem subItem = gaItem.subItemIds.get(a);
            if(subItem.getType() == E_ITEMTYPE.e_folder && subItem.getID() == sub ){
                isIdCondain = true;
                break;
            }
        }
        if( !isIdCondain ){
            GaItem subItem = new GaItem( new Integer(sub), E_ITEMTYPE.e_folder, "NoName" );
            gaItem.subItemIds.add(subItem);
        }
        Collections.sort(rootItem.subItemIds);
    }

    public void setItemCalcFrom(GaItem gaItem, int intExtra, E_ITEMTYPE type) {
        if( gaItem.getType() == type ) gaItem.calcFrom = intExtra;
    }

    public void setItemIsSelected(GaItem gaItem, boolean intExtra, E_ITEMTYPE type) {
        if( gaItem.getType() == type ) gaItem.isSelect = intExtra;
    }

    public void setFolderTrackCount(GaItem gaItem, int intExtra) {;
        if( gaItem.getType() == E_ITEMTYPE.e_folder )
            gaItem.trackCount = intExtra;
    }

    public void clear() {
        rootItem = new GaItem(0, E_ITEMTYPE.e_folder, "");
    }

    static public enum E_ITEMTYPE {
        e_file,
        e_folder
    };

    public class GaItem implements Comparable<GaItem> {
        int id;
        int parentId; //folder id for track
        E_ITEMTYPE type;
        String name;
        public List<GaItem> subItemIds;
        int itemCount;
        boolean isSelect;
        int calcFrom;
        int trackCount; // only for folder
        LinearLayout gaView;

        Object tag;

        GaItem( int id, E_ITEMTYPE type, String name ) {
            this.id = id;
            this.type = type;
            this.name = name;
            subItemIds = new ArrayList<GaItem>() ;
            parentId = -1;
            itemCount = 0;
            isSelect = false;
            calcFrom = 0;
            trackCount = 0;
            tag = new Object();
            gaView = (this.type == E_ITEMTYPE.e_folder ?
                    (LinearLayout) inflater.inflate(R.layout.inflate_folder, null):
                    (LinearLayout) inflater.inflate(R.layout.inflate_file, null));
        }

        public boolean isEqual( int id, E_ITEMTYPE type ) {
            return (this.id == id && this.type == type);
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public E_ITEMTYPE getType() {
            return this.type;
        }

        public int getID(){
            return this.id;
        }

        public Object getTag(){
            return this.tag;
        }

        public int compareTo(GaItem other) {
            return (this.id > other.getID()) ? 1 : (this.id < other.getID()) ? -1 : 0;
        }

        public View getView(LayoutInflater inflater, int attachment) {
            LinearLayout v = (this.type == E_ITEMTYPE.e_folder ?
                    (LinearLayout) inflater.inflate(R.layout.inflate_folder, null):
                    (LinearLayout) inflater.inflate(R.layout.inflate_file, null));
            int rId = (this.type == E_ITEMTYPE.e_folder ? R.id.folder_name : R.id.file_name);
            ((TextView) v.findViewById(rId)).setText(this.name);

            if (attachment > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins((attachment * 16) + 8, 0, 0, 0);
                v.setLayoutParams(layoutParams);
            }

            v.setTag(tag);
            v.setOnClickListener(FOnClick);

            return v;
        }

        public View.OnClickListener FOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        public GaItem isContain(int id, E_ITEMTYPE type) {
            if(this.isEqual(id, type)) return this;

            for( int a = 0; a < this.subItemIds.size(); a++ ){
                if(this.subItemIds.get(a).isEqual(id, type )){
                    return this.subItemIds.get(a);
                }
                GaItem gaItemTmp = this.subItemIds.get(a).isContain(id, type);
                if(null != gaItemTmp) return gaItemTmp;
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void addView(GaItem subItem, LayoutInflater layoutInflater, int attachment) {
            LinearLayout children = (LinearLayout) gaView.findViewById(R.id.children);
            children.addView(this.getView(layoutInflater, attachment));
        }
    }

    GaItem rootItem;
    LayoutInflater inflater;

    public TrackTree(LayoutInflater inflater) {
        this.inflater = inflater;
        rootItem = new GaItem(0, E_ITEMTYPE.e_folder, "");
    }

    public GaItem getRoot() {
        return this.rootItem;
    }

    public GaItem updateFolder( int id, int parentId, String name ) {
        GaItem gaFolder = rootItem.isContain(id, E_ITEMTYPE.e_folder);
        GaItem gaParentFolder = rootItem.isContain(parentId, E_ITEMTYPE.e_folder);

        if( gaFolder == null) { //new
            GaItem gaItem = new GaItem( id, E_ITEMTYPE.e_folder, name );
            gaItem.setParentId(parentId);
            if(gaParentFolder != null){
                gaParentFolder.subItemIds.add( gaItem );
                gaFolder = gaItem;
            }
        } else {
            gaFolder.setName(name);
            gaFolder.setParentId( parentId );
        }
        return gaFolder;
    }

    public GaItem updateFile( int id, int parentId, String name ) {
//        int idFile = isTrackContain(id);
        GaItem gaFile = rootItem.isContain(id, E_ITEMTYPE.e_file);
        GaItem gaFolderFile = rootItem.isContain(parentId, E_ITEMTYPE.e_folder);

        if( gaFile == null) { //new
            GaItem gaItem = new GaItem( id, E_ITEMTYPE.e_file, name );
            gaItem.setParentId(parentId);
            if(gaFolderFile != null) gaFolderFile.subItemIds.add( gaItem );
            else rootItem.subItemIds.add(gaItem);
            gaFile = gaItem;
        } else {
            gaFile.setName(name);
            gaFile.setParentId( parentId );
        }
        return gaFile;
    }
}
