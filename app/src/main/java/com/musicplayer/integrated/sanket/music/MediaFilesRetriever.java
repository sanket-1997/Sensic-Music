package com.musicplayer.integrated.sanket.music;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sanket on 28-06-2017.
 */

public class MediaFilesRetriever {

 private ArrayList<ModelSongs> trackList;
    private ArrayList<String> extensions;

    public MediaFilesRetriever(){
        trackList = new ArrayList<>();
        extensions = new ArrayList<>();
        extensions.add(".mp3");
        extensions.add(".aac");
        extensions.add(".wav");

    }

    public ArrayList<ModelSongs> getSongsList(Context context ){
        String selectionMedia = MediaStore.Audio.Media.IS_MUSIC;
        String projectionMedia[] = {MediaStore.Audio.Media._ID,
               MediaStore.Audio.Media.ARTIST,
               MediaStore.Audio.Media.ALBUM,
               MediaStore.Audio.Media.TITLE,
               MediaStore.Audio.Media.DISPLAY_NAME,
               MediaStore.Audio.Media.ALBUM_ID,
               MediaStore.Audio.Media.DURATION,
               MediaStore.Audio.Media.DATA
       };

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projectionMedia,selectionMedia,null,null);
      //  Log.e("Path",MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.);

         while (cursor.moveToNext()){

             ModelSongs song = new ModelSongs();
             String temp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
             String extension="";
             extension = temp.substring(temp.length()-4,temp.length());
             if(!extensions.contains(extension.toLowerCase().toString()))continue;


             song.setSongID(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
             song.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
             song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
             song.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));



             song.setSongName(temp.replace(extension,""));
             song.setAlbumId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
             song.setAlbumArt(getAlbumArtwork(context , song.getAlbumId()));
             song.setData(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
             song.setTotalDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));

             trackList.add(song);
         }

        return  trackList;
    }

    private String getAlbumArtwork(Context context, int albumId) {
         String albumArtURI = null;
        String projection[] = {MediaStore.Audio.AlbumColumns.ALBUM_ART};
        Cursor c = context.getContentResolver().query(ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,albumId),projection,null,null,null);
      if(c.moveToFirst())
          albumArtURI = c.getString(0);


      c.close();
      return  albumArtURI;
    }


    public ArrayList<ModelAlbum> getListOfAlbums(Context context) {

        String where = null;

        final Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Log.e("Path",uri.toString());
        final String _id = MediaStore.Audio.Albums._ID;
        final String album_name = MediaStore.Audio.Albums.ALBUM;
        final String artist = MediaStore.Audio.Albums.ARTIST;
        final String albumArt = MediaStore.Audio.Albums.ALBUM_ART;
        final String tracks = MediaStore.Audio.Albums.NUMBER_OF_SONGS;
        final String[] columns = { _id, album_name, artist, albumArt, tracks };
        Cursor cursor = context.getContentResolver().query(uri, columns, where,
                null, null);

        ArrayList<ModelAlbum> list = new ArrayList<ModelAlbum>();

        // add playlsit to list

        if (cursor.moveToFirst()) {
            do {

                ModelAlbum albumData = new ModelAlbum();

                albumData
                        .setAlbumID(cursor.getLong(cursor.getColumnIndex(_id)));

                albumData.setAlbumName(cursor.getString(cursor
                        .getColumnIndex(album_name)));

                albumData.setALbumArtist(cursor.getString(cursor
                        .getColumnIndex(artist)));

                albumData.setAlbumArt(cursor.getString(cursor
                        .getColumnIndex(albumArt)));

                albumData.setTracks(cursor.getString(cursor
                        .getColumnIndex(tracks)));

                list.add(albumData);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }


}
