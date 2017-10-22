package com.musicplayer.integrated.sanket.music;

/**
 * Created by Sanket on 12-07-2017.
 */

public class ModelAlbum {
    private long albumID;
    private String albumName, albumArtist,tracks,albumArt;

    public long getAlbumID() {
        return albumID;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getTracks() {
        return tracks;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setALbumArtist(String ALbumArtist) {
        this.albumArtist = ALbumArtist;
    }

    public void setTracks(String tracks) {
        this.tracks = tracks;
    }
    public  void setAlbumArt(String albumArt){
        this.albumArt = albumArt;
    }

}
