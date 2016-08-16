package com.ganet.catfish.hondamedia.Java;

/**
 * Created by oleg on 11.08.2016.
 */
public class ActiveTrackInfo {

    public int diskID;
    public String playTime;
    public String playTrackName;
    public String playAlbome;
    public int folderId;
    public int trackId;

    public ActiveTrackInfo() {
        diskID = -1;
        playTime = "";
        playTrackName = "";
        folderId = -1;
        trackId = -1;
        playAlbome = "";
    }
}
