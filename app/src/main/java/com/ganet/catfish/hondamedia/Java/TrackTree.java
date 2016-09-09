package com.ganet.catfish.hondamedia.Java;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    public static final String TAG = "GaNetService";

    static public enum E_ITEMTYPE {
        e_file,
        e_folder
    };

    GaItem rootItem;
    LayoutInflater inflater;
    Vector <Integer> unResolvedTracks;

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
            gaView.setTag(tag);
            gaView.setOnClickListener(FOnClick);
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

        public View.OnClickListener FOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewFolder = v.findViewById(R.id.childFolder);
                View viewFile = v.findViewById(R.id.childFile);

                if (viewFolder.getVisibility() == View.VISIBLE) {
                    viewFolder.setVisibility(View.GONE);
                    viewFile.setVisibility(View.GONE);
                } else {
                    viewFolder.setVisibility(View.VISIBLE);
                    viewFile.setVisibility(View.VISIBLE);
                }
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

        private void addView( GaItem item ) {
            int ID = item.getType() == E_ITEMTYPE.e_folder ? R.id.childFolder : R.id.childFile;
            LinearLayout children = (LinearLayout) gaView.findViewById( ID );
            if( children.findViewWithTag( item.getTag() ) == null ) {
                Log.d(TAG, "addView - Added; Children ID =" + ID );
                children.addView( item.getLLView() );
            }
        }

        public LinearLayout getLLView() {
            return gaView;
        }

        public void updateAttacheLevel( int attachment ) {
            if (attachment > 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins((attachment * 16) + 8, 0, 0, 0);
                gaView.setLayoutParams(layoutParams);
            }
        }
    }

    public void addSubFoldersID(GaItem gaItem , int sub) {
        boolean isIdCondain = false;
        for(int a = 0; a < gaItem.subItemIds.size(); a++) {
            GaItem subItem = gaItem.subItemIds.get(a);
            if(subItem.getType() == E_ITEMTYPE.e_folder && subItem.getID() == sub ){
                isIdCondain = true;
                break;
            }
        }
        if( !isIdCondain ) {
            GaItem subItem = new GaItem( new Integer(sub), E_ITEMTYPE.e_folder, "NoNameFolder_" + sub );
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

    public TrackTree(LayoutInflater inflater) {
        this.inflater = inflater;
        rootItem = new GaItem(0, E_ITEMTYPE.e_folder, "");
        unResolvedTracks = new Vector<Integer>();
    }

    public GaItem getRoot() {
        return this.rootItem;
    }

    public void resolveTrack( LinearLayout rootFolder ) {
        if( unResolvedTracks.size() != 0 ) {
            Log.d( TAG, "resolveTrack size = " + unResolvedTracks.size() );
            for( int a = 0; a < rootItem.subItemIds.size(); a++ ){
                GaItem item = rootItem.subItemIds.get(a);
                if( item.getType() == E_ITEMTYPE.e_file && unResolvedTracks.contains( new Integer(item.getID() ))) {
                    //find folder id
                    GaItem gaFolder = isContain(this.rootItem, item.parentId, E_ITEMTYPE.e_folder);
                    if( gaFolder != null && (rootFolder.findViewWithTag(item.getTag()) != null) ) {
                        Log.d( TAG, "resolveTrack rootFolder (" + gaFolder.getID() +  ") " + gaFolder.getName()  );
                        rootFolder.removeView( item.getLLView() );
                        rootItem.subItemIds.remove(a);
                        unResolvedTracks.remove( new Integer(item.getID()) );
                        Log.d( TAG, "resolveTrack addItem (" + item.getID() +  ") " + item.getName() + "; Type:" + item.getType());
                        gaFolder.addView( item );
                        gaFolder.subItemIds.add(item);
                    }
                }
            }
        }
    }

    private GaItem isContain(GaItem rootItem, int id, E_ITEMTYPE type) {
        GaItem gaFolder = rootItem.isContain(id, type);
        if( gaFolder == null ) {
            for( int a = 0; a < rootItem.subItemIds.size(); a++ ){
                if( rootItem.subItemIds.get(a).getType() == type ){
                    gaFolder = isContain( rootItem.subItemIds.get(a), id, type );
                    if( gaFolder != null ) break;
                }
            }
        }
        return gaFolder;
    }

    public GaItem updateFolder( int id, int parentId, String name ) {
        GaItem gaFolder = rootItem.isContain(id, E_ITEMTYPE.e_folder);
        GaItem gaParentFolder = rootItem.isContain(parentId, E_ITEMTYPE.e_folder);

        if( gaFolder == null) { //new
            GaItem gaItem = new GaItem( id, E_ITEMTYPE.e_folder, name );
            gaItem.setParentId(parentId);
            if(gaParentFolder != null){
                Log.d(TAG, "in folder(" + gaParentFolder.getID() + ") " + gaParentFolder.getName() + " Added Folder (" + gaItem.getID() + " )" + gaItem.getName() );
                gaParentFolder.subItemIds.add( gaItem );
                gaFolder = gaItem;
            }
        } else {
            Log.d(TAG, "Update name for folder(" + gaFolder.getID() + ") " + gaFolder.getName() + " parent id (" + gaFolder.parentId + ")" );
            gaFolder.setName(name);
            gaFolder.setParentId( parentId );
        }
        return gaFolder;
    }

    public GaItem updateFile( int id, int parentId, String name ) {
        GaItem gaFile = rootItem.isContain( id, E_ITEMTYPE.e_file );
        GaItem gaFolderFile = rootItem.isContain( parentId, E_ITEMTYPE.e_folder );

        if( gaFile == null) { //new
            GaItem gaItem = new GaItem( id, E_ITEMTYPE.e_file, name );
            gaItem.setParentId(parentId);
            if(gaFolderFile != null) {
                Log.w(TAG, "Added track ID# " + id + "; Name:" + name + "; In folder Id:" + gaFolderFile.getID() + "; Name: " + gaFolderFile.getName() );
                gaFolderFile.subItemIds.add( gaItem );
            } else {
                Log.w(TAG, "Parent folder# " + parentId + "not found!!!");
                rootItem.subItemIds.add(gaItem);
                unResolvedTracks.add( new Integer(id) );
            }
            gaFile = gaItem;
        } else {
            Log.d(TAG, "Update name for file(" + gaFile.getID() + ") " + gaFile.getName() + " folder id (" + gaFile.parentId + ")" );
            gaFile.setName(name);
            gaFile.setParentId( parentId );
        }
        return gaFile;
    }
}
