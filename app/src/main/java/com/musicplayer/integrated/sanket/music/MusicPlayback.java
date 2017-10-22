package com.musicplayer.integrated.sanket.music;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public  class MusicPlayback extends Service implements MediaPlayer.OnCompletionListener , Visualizer.OnDataCaptureListener {
 public static MediaPlayer mediaPlayer;
 public  SharedPreferences sharedPreferences=null ;
 public static SharedPreferences.Editor editor=null;
 public static int songPosition = -1;



 private  int songIntentPosition;

 private static boolean isPrepared = false;
 private static boolean isPlaying = false;
 public static boolean isLoop = false;
 public  static  boolean isShuffle = false;
 public static boolean isMainPlayerOpen=true;
 public static boolean isDisturbed = false;
public static long value=0;
public  static  double totalRate = 0;
private  AudioManager audioManager ;
private int vol;
 public static ArrayList<ModelSongs> allTracks ;
 public static ArrayList<ModelAlbum> albums;

public static ArrayList<Integer> shuffleIndex;
public static HashMap<Integer , Integer> trackPosition;
public static HashMap<String , Integer> idToPos; // Maps Song id with Position
public static int shuffleIndexPosition;
public static Context context;
    public static Shake shake;
public static ArrayList<Integer> songSet; // stores song position
public static ArrayList<Integer> shuffleSet; // stores shuffle position
public static ArrayList<Integer> tSongSet; /// temp song set
public static ArrayList<Integer> tShuffleSet; // temp shuffle set
public static ArrayList<Integer> totalSongs; // helps to map all the song position
public static ArrayList<String> smallPlaylist; // use to store playlist except soft and hard list
public static ArrayList<String>  playlist;         /// use to store playlist from the database
public static ArrayList<Integer> songDatabaseSet; /// use to store position from the database
public static String musicSet="";

public MusicIntentReceiver musicIntentReceiver;
public static int cursor;
public static boolean setChanged = false;
public static int start= 0;
private  Visualizer visualizer;

    public static void startMediaPlayback(int position){

        songPosition = position;
        editor.putInt("music",position).apply();
        mediaPlayer.reset();
        Log.e("media","reset");

        try {
            mediaPlayer.setDataSource(MusicPlayback.allTracks.get(position).getData());
            mediaPlayer.prepare();
            isPrepared=true;
            isPlaying = true;
            mediaPlayer.start();
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(serviceIntent);
            totalRate = Double.parseDouble(getTotalTime(mediaPlayer.getDuration()))*60000;
            value=0;
            isDisturbed=false;
            Log.d("Total",""+ totalRate);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public  static  void pauseMediaPlayback(){
        mediaPlayer.pause();
        isPlaying = false;
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        context.startService(serviceIntent);
    }

    public  static void resumeMediaPlayback(){
        if(isPrepared){
            mediaPlayer.start();
            Intent serviceIntent = new Intent(context, NotificationService.class);
            serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            context.startService(serviceIntent);
        }
        else
        {
            startMediaPlayback(songPosition);
        }
        isPlaying=true;
    }

    public static void stopMediaPlayback(){

        mediaPlayer.stop();
        Log.e("media","stop");
        mediaPlayer.reset();
       Log.e("media","reset");

       // mediaPlayer.reset();
        isPrepared=false;
        isPlaying=false;

//        Intent serviceIntent = new Intent(context, NotificationService.class);
//        serviceIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//        context.stopService(serviceIntent);
    }

    public  static  int getPlayPrevMediaIndex(){

        value=0;
        int index = 0;
        if( MusicPlayback.setChanged && songSet.size() != allTracks.size()){ /// new list is loaded
            shuffleIndexPosition=0;
            MusicPlayback.setChanged=false;
            Log.d("Shuffle index Position" , ""+shuffleIndexPosition);
        }

        if(isShuffle){
            if(shuffleIndexPosition <= 0){
                shuffleIndexPosition = shuffleSet.size() -1;
            }
            else {
                shuffleIndexPosition--;
            }
            try{
                index = shuffleSet.get(shuffleIndexPosition);
            }catch (Exception e){
                Log.e("Exception is shuffle" , e.toString());
            }
            Log.e("index prev ",""+index);
            Log.e("shuffle index",String.valueOf(shuffleIndexPosition));
        }
        else {

            if(cursor<=0)
                cursor=shuffleSet.size() -1;
            else
                cursor--;

            index = songSet.get(cursor);
        }

        songPosition=index;

        return index;
    }



    public  static  int getPlayNextMediaIndex(){

        value=0;
        int index = 0;
        if( MusicPlayback.setChanged  && songSet.size() != allTracks.size()){ /// new list is loaded
            shuffleIndexPosition=0;
            MusicPlayback.setChanged=false;
            Log.d("Shuffle index Position" , ""+shuffleIndexPosition);
        }


        if(isShuffle){
            if(shuffleIndexPosition >= shuffleSet.size() -1 ){
                shuffleIndexPosition = 0;
            }
            else {
                shuffleIndexPosition++;
            }
            try{
                index = shuffleSet.get(shuffleIndexPosition);
            }catch (Exception e){
                Log.e("Exception is shuffle" , e.toString());
            }
            Log.e("index next ",""+index);
            Log.e("shuffle index",String.valueOf(shuffleIndexPosition));
        }
        else {

            if(cursor==songSet.size()-1)
                cursor=0;
            else
                cursor++;

            index = songSet.get(cursor);
        }
        Log.d("Next Index : ",""+index );

        songPosition=index;
        return index;
    }





    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if(musicIntentReceiver == null){
            musicIntentReceiver = new MusicIntentReceiver();

        }

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            registerReceiver(musicIntentReceiver, filter);
        }catch (Exception e){

        }
        if(intent != null ){
                if(sharedPreferences==null)sharedPreferences =  getSharedPreferences("MusicData",MODE_PRIVATE);
                if(editor == null)editor = sharedPreferences.edit();

        mediaPlayer = new MediaPlayer();
        visualizer = new Visualizer(1);
        audioManager =(AudioManager) getSystemService(AUDIO_SERVICE);
        mediaPlayer.setOnCompletionListener(this);
        songIntentPosition = intent.getIntExtra("Song Position",0);
        songPosition = songIntentPosition;
        try {
            startMediaPlayback(songIntentPosition);
            visualizer.setDataCaptureListener(this,Visualizer.getMaxCaptureRate()/2,true,false);
            visualizer.setEnabled(true);

        }
        catch (Exception e){

        }

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("destroy","Service destroyed");
       // stopMediaPlayback();
    }

    @Override
    public boolean stopService(Intent name) {
        stopMediaPlayback();
        return true;
    }


    public static int getSongPosition(){
        return  songPosition;
    }

    public static boolean getPlayingStatus(){
        return  isPlaying;}



    public static boolean getPrepared() {
        return isPrepared;
    }

    public static String getTime(int milliseconds){
        String val_min, val_sec;
        int min = (milliseconds/1000)/60;
        int sec = (milliseconds/1000)%60;
        if(min==0){
            val_min="0";
        }
        else{
            val_min= String.valueOf(min);
        }
        if(sec==0){
            val_sec="00";
        }
        else{
            val_sec= String.valueOf(sec);
            if(val_sec.length()==1){
                val_sec = "0"+val_sec;
            }
        }
        return val_min+":"+val_sec;
    }


   private static String getTotalTime(int milliseconds){
        String val_min, val_sec;
        int min = (milliseconds/1000)/60;
        int sec = (milliseconds/1000)%60;
        if(min==0){
            val_min="0";
        }
        else{
            val_min= String.valueOf(min);
        }
        if(sec==0){
            val_sec="00";
        }
        else{
            val_sec= String.valueOf(sec);
            if(val_sec.length()==1){
                val_sec = "0"+val_sec;
            }
        }
        return val_min+"."+val_sec;
    }






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
       int val =(int)Math.ceil(value/totalRate);
        final int THRESH_HOLD = 100;
        if(!isDisturbed){
        // Add to the database;
            if(val>THRESH_HOLD){
                if(val>171){
                    //Hard Music
                    SongListDatabase.table = "Hard";
                    new SongListDatabase(context,SongListDatabase.table).enterSongPos(MusicPlayback.allTracks.get(songPosition).getSongID());
                }
                else {
                    SongListDatabase.table = "Soft";
                    new SongListDatabase(context,SongListDatabase.table).enterSongPos(MusicPlayback.allTracks.get(songPosition).getSongID());

                }

            }

       }
        Log.d("Val",""+val);
        value=0;

        if(isLoop){
              startMediaPlayback(songPosition);
              editor.putInt("music",songPosition).commit();

        }
        else{
            int save = getPlayNextMediaIndex();
            startMediaPlayback(save);
            songPosition=save;
            editor.putInt("music",save).commit();
            sendBroadcast(new Intent("myaction"));

        }




    }


    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        int threshHold =(int)(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*0.67);
        vol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if(isPlaying && vol>=threshHold){
            for (int i =0 ; i<waveform.length/8;i++){
                value+=Math.abs(waveform[i]);
            }
            if(value<threshHold){
                isDisturbed=true;
            }

            Log.d("Value",""+value+":"+value/totalRate);
        }
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

    }
}
