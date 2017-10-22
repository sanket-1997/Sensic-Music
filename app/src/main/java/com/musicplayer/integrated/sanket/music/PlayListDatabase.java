package com.musicplayer.integrated.sanket.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Sanket on 18-07-2017.
 */

public class PlayListDatabase extends SQLiteOpenHelper {
private static final String DB_NAME = "Playlist";
private static final String TABLE ="playlists";
private static  final int VER = 1;
private static final String COL ="name";

    public PlayListDatabase(Context context) {
        super(context, DB_NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL("CREATE TABLE "+TABLE+" ( name text )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long createPlaylist(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL ,name );
        return db.insert(TABLE,null,contentValues);
    }

    public int deletePlaylist(String name){

        SQLiteDatabase database = this.getWritableDatabase();

        return database.delete(TABLE ,  COL + " = '" + name + "'" , null);

    }

    public int renamePlaylist(String oldName, String newName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL, newName);
        String[] args = new String[VER];
        args[0] = oldName;
        try {
            db.update(TABLE, values, "name =?", args);
            return VER;
        } catch (Exception e) {
            return 0;
        }
    }
    public ArrayList<String> getPlaylist(){
        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select *  from " + TABLE , null);
        while(c.moveToNext() ){
            String name = c.getString(c.getColumnIndex(COL));
            list.add(name);

        }

        return  list;
    }




}
