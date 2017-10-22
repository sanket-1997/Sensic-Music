package com.musicplayer.integrated.sanket.music;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {
    static final int MY_PERMISSIONS_REQUEST=0;
    static boolean PERMISSION = false;
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new MyTask().execute();



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                                               String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    PERMISSION = true;


                } else {

                    Toast.makeText(SplashScreen.this,"All Permissions Required",Toast.LENGTH_LONG).show();

                }
                finish();



            }

        }
    }



    private  class  MyTask extends AsyncTask<Void , Void , Boolean>{


        @Override
        protected void onPreExecute() {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED   ||
                        checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED  ||
                        checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED


                        ) {
                    permissions = new String[3];
                    permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
                    permissions[1] = Manifest.permission.READ_PHONE_STATE;
                    permissions[2] = Manifest.permission.RECORD_AUDIO;

                    requestPermissions(permissions,
                            MY_PERMISSIONS_REQUEST);

                }
                else {
                    PERMISSION =true;
                }

            }
            else {
                PERMISSION = true;
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean ok = false;
            if(PERMISSION){
                try {
                    if(MusicPlayback.idToPos==null)MusicPlayback.idToPos = new HashMap<>();
                    if(MusicPlayback.allTracks == null){
                        MusicPlayback.allTracks = new MediaFilesRetriever().getSongsList(getApplicationContext());
                        Collections.sort(MusicPlayback.allTracks, new Comparator<ModelSongs>() {
                            @Override
                            public int compare(ModelSongs o1, ModelSongs o2) {
                                return o1.getTitle().compareToIgnoreCase(o2.getTitle());
                            }
                        });

                    }

                    if(MusicPlayback.totalSongs == null){
                        MusicPlayback.totalSongs = new ArrayList<>();
                        for(int i = 0 ; i<MusicPlayback.allTracks.size();i++){
                            MusicPlayback.idToPos.put(MusicPlayback.allTracks.get(i).getSongID(),i);
                            MusicPlayback.totalSongs.add(i);
                        }
                    }



                    if (MusicPlayback.shuffleIndex== null  && MusicPlayback.allTracks.size()>0){
                        MusicPlayback.shuffleIndex = new ShuffleLogic(MusicPlayback.allTracks.size()).setupInOrder().shuffle().getShuffleOrder();
                    }
                    if(MusicPlayback.trackPosition == null){
                        MusicPlayback.trackPosition = new HashMap<>();

                        for(int  i = 0 ;i<MusicPlayback.shuffleIndex.size();i++){
                            MusicPlayback.trackPosition.put(MusicPlayback.shuffleIndex.get(i),i);
                        }
                    }



                    if(MusicPlayback.albums == null){
                        MusicPlayback.albums = new MediaFilesRetriever().getListOfAlbums(getApplicationContext());

                    }


                    if(MusicPlayback.songSet == null){
                        MusicPlayback.songSet = new ArrayList<>();
                        for(int i = 0 ; i<MusicPlayback.allTracks.size();i++){
                            MusicPlayback.songSet.add(i);
                        }
                    }

                    if(MusicPlayback.shuffleSet== null){
                        MusicPlayback.shuffleSet = MusicPlayback.shuffleIndex;
                    }

                    if(MusicPlayback.playlist==null){
                        PlayListDatabase database = new PlayListDatabase(getApplicationContext());
                        MusicPlayback.playlist = database.getPlaylist();
                        MusicPlayback.smallPlaylist = new ArrayList<>();
                        for(int i = 0 ; i < MusicPlayback.playlist.size();i++){
                            if(i<2)continue;
                            MusicPlayback.smallPlaylist.add(MusicPlayback.playlist.get(i));
                        }

                    }

                    if(MusicPlayback.allTracks.size()>0){



                        ok=true;




                    }
                }
                catch (Exception e){

                }



            }
           return ok;
        }

        @Override
        protected void onPostExecute(Boolean b ) {
            if(b){


                MusicPlayback.start=0;
                Intent intent = new Intent(SplashScreen.this, ActivityMainPlayer.class);
                if(MusicPlayback.getSongPosition()==-1){
                startService(new Intent(SplashScreen.this,EqualizerService.class));}
                finish();
                startActivity(intent);


            }
            else {
                Toast.makeText(getApplicationContext(), "Add Music!",Toast.LENGTH_LONG).show();
                finish();
            }

        }
    }






}
