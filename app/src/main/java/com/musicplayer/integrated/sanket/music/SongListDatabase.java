package com.musicplayer.integrated.sanket.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Sanket on 20-07-2017.
 */

public class SongListDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "SongsList";
    private static final String COL_ID = "id";

    public static int VER ;
    public static String table;




    public SongListDatabase(Context context , String tableName) {
        super(context, DB_NAME, null,VER);
        table = tableName;
    }
    public SongListDatabase(Context context ) {
        super(context, DB_NAME, null,VER);

    }

    public ArrayList<Integer> getPlaylistSongs(){
        ArrayList<Integer> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select *  from " + table, null);
        while(c.moveToNext() ){
           String id = c.getString(c.getColumnIndex(COL_ID));
            if(!MusicPlayback.idToPos.containsKey(id)){
                deletePlaylist(id);
            }
            else{
                list.add(MusicPlayback.idToPos.get(id));
            }



        }

        return  list;
    }

    public int renamePlaylistName(String newName) {
        int c = 0;
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("ALTER TABLE " + table + " RENAME TO " + newName + ";");
            db.setTransactionSuccessful();
            c = 1;
            return c;
        } finally {
            db.endTransaction();
        }
    }

    public long enterSongPos( String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID , id );
        return db.insert(table,null,contentValues);
    }
    public void deletePlaylist(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM "+ table + " WHERE "+COL_ID+" = '"+id+"'");


    }

     public void deleteSongListDatabase(){
       SQLiteDatabase db = this.getWritableDatabase();
       try{
           db.execSQL("DROP TABLE IF EXISTS " + table);
           Log.d("Sql" , "Deleted");
       }catch (Exception e){
           Log.d("Sql" , "Failed");
       }


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+"Soft"+" ( id text unique )");
        db.execSQL("CREATE TABLE "+"Hard"+" ( id text unique )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ///Preserve Database on Upgrade
        if(MusicPlayback.playlist.size()>0){
            for (String s : MusicPlayback.playlist){
                copyDBToSDCard(s);
            }
        }
        try{
            db.execSQL("DROP TABLE IF EXISTS " + table);
            Log.d("Sql" , "Deleted");
        }catch (Exception e){
            Log.d("Sql" , "Failed");
        }
        db.execSQL("CREATE TABLE "+table+" ( id text unique )");
    }

    public void copyDBToSDCard(String dbname) {
        try {
            InputStream myInput = new FileInputStream("/data/data/com.myproject/databases/"+dbname);

            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/"+dbname);
            if (!file.exists()){
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    Log.i("FO","File creation failed for " + file);
                }
            }

            OutputStream myOutput = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/"+dbname);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Log.i("FO","copied");

        } catch (Exception e) {
            Log.i("FO","exception="+e);
        }


    }



}
