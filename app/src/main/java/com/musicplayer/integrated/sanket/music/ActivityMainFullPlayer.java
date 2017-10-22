package com.musicplayer.integrated.sanket.music;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;

import com.bumptech.glide.request.transition.Transition;

import ak.sh.ay.musicwave.MusicWave;
import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;
import jp.wasabeef.blurry.Blurry;


public class ActivityMainFullPlayer extends AppCompatActivity {


private ImageView fullPlayer ,fullPlayer_album, fullPlayer_shuffle , fullPlayer_loop , more;
private   TextView fullPlayer_song , fullPlayer_artist , fullPlayer_currentTime , fullPlayer_maxTime;
    private SeekBar seekBar;
private ModelSongs song;
public  ImageView fullPlayer_play;
private Handler progressHandler;
private static Runnable   progressRunnable;
private  Proximity proximity;
private MusicWave musicWave;
private Visualizer visualizer;
private SwipeDetector swipeDetector;
private RelativeLayout relativeLayout;
private final  int SAMPLING = 4;
private CloseApp closeApp;
private FeatureCoverFlow coverFlow;
private CustomBroadcast broadcast;
    private  SharedPreferences shared ,sh ;
    private  SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setup();
        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main_full_player);

        fullPlayer = (ImageView) findViewById(R.id.mainFullPlayerContainer);
        fullPlayer_play = (ImageView)findViewById(R.id.imageView_full_player_play);
        fullPlayer_album = (ImageView) findViewById(R.id.imageView_full_player_album_art);
        fullPlayer_song = (TextView)findViewById(R.id.textView_full_player_song);
        fullPlayer_artist = (TextView)findViewById(R.id.textView_full_player_artist);
        fullPlayer_currentTime = (TextView)findViewById(R.id.textView_current_time);
        fullPlayer_maxTime = (TextView)findViewById(R.id.textView_total_length);
        relativeLayout = (RelativeLayout)findViewById(R.id.h);

        more = (ImageView)findViewById(R.id.imageView_full_player_more);
        shared = getSharedPreferences("ActivitySettings",MODE_PRIVATE);
        sh = getSharedPreferences("MusicData",MODE_PRIVATE);
        editor = sh.edit();


        fullPlayer_song.setSelected(true);
        fullPlayer_artist.setSelected(true);

        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){

            coverFlow = (FeatureCoverFlow)findViewById(R.id.coverflow);
            coverFlow.setAdapter(new FeatureCoverflowAdapter());
            coverFlow.scrollToPosition(MusicPlayback.songSet.size());

            coverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(MusicPlayback.songSet.size()==MusicPlayback.allTracks.size()){
                        MusicPlayback.cursor = position;
                        MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(MusicPlayback.cursor);
                        MusicPlayback.startMediaPlayback(position);
                    }
                    else{
                        try {
                            MusicPlayback.songPosition = MusicPlayback.songSet.get(position);
                            MusicPlayback.startMediaPlayback(MusicPlayback.songSet.get(position));
                        }catch (Exception e){
                            position%=MusicPlayback.songSet.size();
                            MusicPlayback.songPosition = MusicPlayback.songSet.get(position);
                            MusicPlayback.startMediaPlayback(MusicPlayback.songSet.get(position));

                        }
                    }
                }
            });

        }

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(ActivityMainFullPlayer.this , v);
                p.getMenuInflater().inflate(R.menu.more_options,p.getMenu());
                p.show();
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_more_action_guide :
                                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainFullPlayer.this);
                                View view = getLayoutInflater().inflate(R.layout.guide_layout,null);
                                TextView ok =(TextView) view.findViewById(R.id.textView_guide_ok);

                                builder.setView(view);
                                final AlertDialog dialog = builder.create();
                                dialog.show();
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                break;
                            case  R.id.menu_more_action_about_dev:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityMainFullPlayer.this);
                                View view1 = getLayoutInflater().inflate(R.layout.about_dev,null);
                                TextView ok1 =(TextView) view1.findViewById(R.id.textView_about_ok);

                                builder1.setView(view1);
                                final AlertDialog dialog1 = builder1.create();
                                dialog1.show();
                                ok1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog1.dismiss();
                                    }
                                });
                                break;

                            case R.id.menu_more_action_settings : startActivity( new Intent(getApplicationContext() , ActivitySettings.class));
                                overridePendingTransition(R.anim.left_enter_translate , R.anim.right_enter_translate);
                                break;
                            case R.id.menu_more_action_equalizer :startActivity(new Intent(getApplicationContext(),ActivityEqualizer.class));
                                overridePendingTransition(R.anim.left_enter_translate , R.anim.right_enter_translate);
                        }
                        return true;
                    }
                });
            }
        });


        swipeDetector = new SwipeDetector(relativeLayout);
        swipeDetector.setMinDistanceInPixels(240);
        swipeDetector.setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                if(swipeType == SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT){
                    playNext();
                }
                if(swipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT){
                    playPrev();
                }
            }
        });

try{
        if(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt() != null){

//            d = new BitmapDrawable(getResources(),ImageEnhancer.getAdjustedOpacity(ImageEnhancer.getConvertedImage(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt(), SIZE),OPACITY));
//            fullPlayer.setBackground(d);
         fullPlayer_album.setImageURI(Uri.parse(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt()));

            Glide.with(this).asBitmap().load(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                    Blurry.with(ActivityMainFullPlayer.this).radius(30).sampling(SAMPLING).from(resource).into(fullPlayer);
                }
            });

        }
        else {
            Glide.with(ActivityMainFullPlayer.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                    Blurry.with(ActivityMainFullPlayer.this).radius(30).sampling(SAMPLING).from(resource).into(fullPlayer);
                }
            });
          fullPlayer_album.setImageResource(R.drawable.default_album_art);
        }}catch (Exception e){

    System.exit(0);
}

        fullPlayer_maxTime.setText(MusicPlayback.getTime(MusicPlayback.mediaPlayer.getDuration()));
        fullPlayer_shuffle = (ImageView)findViewById(R.id.imageView_full_player_shuffle);
        fullPlayer_loop = (ImageView)findViewById(R.id.imageView_full_player_loop);

    if(proximity==null)proximity= new Proximity(this);

        if(MusicPlayback.isShuffle)fullPlayer_shuffle.setImageResource(R.drawable.shuffle_on);
        else fullPlayer_shuffle.setImageResource(R.drawable.shuffle_off);

        if(MusicPlayback.isLoop)fullPlayer_loop.setImageResource(R.drawable.loop_current);
        else fullPlayer_loop.setImageResource(R.drawable.loop_all);


        seekBar = (SeekBar)findViewById(R.id.seekBar);

        song = MusicPlayback.allTracks.get(MusicPlayback.songPosition);


        fullPlayer_song.setText(song.getTitle());
        fullPlayer_artist.setText(song.getArtist());


       broadcast = new CustomBroadcast();
        IntentFilter intentFilter = new IntentFilter("myaction");
        registerReceiver(broadcast,intentFilter);

        if(MusicPlayback.getPlayingStatus()){
            fullPlayer_play.setImageResource(R.drawable.music_widget_pause);
        }
        else
        {
            fullPlayer_play.setImageResource(R.drawable.music_widget_play);
        }

        seekBar.setMax(MusicPlayback.mediaPlayer.getDuration());


        progressHandler = new Handler();




        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){

                    fullPlayer_currentTime.setText(MusicPlayback.getTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progressHandler.removeCallbacks(progressRunnable);
                MusicPlayback.isDisturbed = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayback.mediaPlayer.seekTo(seekBar.getProgress());
                progressHandler.post(progressRunnable);
            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();

        progressRunnable = new Runnable() {
            @Override
            public void run() {

                seekBar.setProgress(MusicPlayback.mediaPlayer.getCurrentPosition());
                fullPlayer_currentTime.setText(MusicPlayback.getTime(MusicPlayback.mediaPlayer.getCurrentPosition()));
                progressHandler.postDelayed(progressRunnable,100);

            }
        };
        progressHandler.postDelayed(progressRunnable,1000);
        if(shared.getBoolean("Pro",true)){
            proximity.start();
        }
        if(shared.getBoolean("Vis",true)){
            musicWave = (MusicWave)findViewById(R.id.musicWave);
            int speed =Visualizer.getMaxCaptureRate()/2;

            visualizer = new Visualizer(0);

            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    try {

                        musicWave.updateVisualizer(waveform);
                    }catch (Exception e){

                    }

                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {

                }
            },speed,true,false);
            visualizer.setEnabled(true);


        }


    }

    @Override
    protected void onPause() {
        super.onPause();

        if(shared.getBoolean("Pro",true)){
           proximity.stop();
        }
        if(shared.getBoolean("Vis",true)){
        visualizer.release();
        musicWave = null;
        }

    }

    public  void loop(View view){
    MusicPlayback.isLoop = !MusicPlayback.isLoop;

    if(MusicPlayback.isLoop)fullPlayer_loop.setImageResource(R.drawable.loop_current);
    else fullPlayer_loop.setImageResource(R.drawable.loop_all);


    editor.putBoolean("isLoop",MusicPlayback.isLoop).apply();


}

    public  void shuffle(View view){
        MusicPlayback.isShuffle = !MusicPlayback.isShuffle;

        if(MusicPlayback.isShuffle){fullPlayer_shuffle.setImageResource(R.drawable.shuffle_on);
            if(MusicPlayback.songPosition>=0){
             MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(MusicPlayback.songPosition);
            }
        }
        else {
            Log.d("Cursor",""+MusicPlayback.cursor);
            Log.d("Song Pos ", ""+MusicPlayback.songPosition);
            fullPlayer_shuffle.setImageResource(R.drawable.shuffle_off);
           for(int i = 0 ; i<MusicPlayback.songSet.size();i++){
               if(MusicPlayback.allTracks.get(MusicPlayback.songPosition).getTitle().equals(MusicPlayback.allTracks.get(MusicPlayback.songSet.get(i)).getTitle())){
                   MusicPlayback.cursor = i; // change cursor position to the currently playing song when user switch offs the shuffle
                   break;
               }
           }
        }


      editor.putBoolean("isShuffle",MusicPlayback.isShuffle).apply();




    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!MusicPlayback.isMainPlayerOpen){
            startActivity(new Intent(this , ActivityMainPlayer.class));
            this.overridePendingTransition(R.anim.left_exit_translate,R.anim.right_exit_translate);
        }
        else    this.overridePendingTransition(R.anim.left_exit_translate,R.anim.right_exit_translate);



    }

    public  void playPrevFull(View v){

        playPrev();
    }

    public void playNextFull(View v) {
        playNext();
    }

    private  void playNext(){
        if(MusicPlayback.getPlayingStatus()){

            MusicPlayback.startMediaPlayback(MusicPlayback.getPlayNextMediaIndex());
           // ActivityMainPlayer.updateMainUI(MusicPlayback.songPosition);
            Log.d("log_next","next_true");
        }
        else{
            Log.d("log_next","next_false");

            MusicPlayback.startMediaPlayback(MusicPlayback.getPlayNextMediaIndex());

        }


        Log.d("song position",String.valueOf(MusicPlayback.songPosition));
        updateUI();
        fullPlayer_play.setImageResource(R.drawable.music_widget_pause);


    }
    private void playPrev(){
        if(MusicPlayback.getPlayingStatus()){
            if(seekBar.getProgress()>5000){
                MusicPlayback.startMediaPlayback(MusicPlayback.songPosition);
            }
            else{
                MusicPlayback.startMediaPlayback(MusicPlayback.getPlayPrevMediaIndex());


            }

            Log.d("log_next","next_true");
        }
        else{
            Log.d("log_next","next_false");

            MusicPlayback.startMediaPlayback(MusicPlayback.getPlayPrevMediaIndex());




        }

        Log.d("song position",String.valueOf(MusicPlayback.songPosition));
        updateUI();


        fullPlayer_play.setImageResource(R.drawable.music_widget_pause);



    }


    private void updateUI(){
        fullPlayer_song.setText(MusicPlayback.allTracks.get(MusicPlayback.songPosition).getTitle());
        fullPlayer_artist.setText(MusicPlayback.allTracks.get(MusicPlayback.songPosition).getArtist());
        seekBar.setMax(MusicPlayback.mediaPlayer.getDuration());
        fullPlayer_maxTime.setText(MusicPlayback.getTime(MusicPlayback.mediaPlayer.getDuration()));
        ex();
    }

    public  void  startPauseFull(View v){


        if(MusicPlayback.getPlayingStatus()){
            fullPlayer_play.setImageResource(R.drawable.music_widget_play);

            MusicPlayback.pauseMediaPlayback();
        }
        else{

            MusicPlayback.resumeMediaPlayback();
            fullPlayer_play.setImageResource(R.drawable.music_widget_pause);

        }

    }
    private void setup(){
        if(MusicPlayback.allTracks==null){
        finish();
        }
    }

    private class CustomBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           ex();
        }
    }
    private void ex(){

        if(MusicPlayback.getPlayingStatus()){
            fullPlayer_play.setImageResource(R.drawable.music_widget_play);

        }
        else{

            fullPlayer_play.setImageResource(R.drawable.music_widget_pause);

        }
            ModelSongs song = MusicPlayback.allTracks.get(MusicPlayback.songPosition);

            if (song.getAlbumArt() != null) {

                Glide.with(ActivityMainFullPlayer.this).asBitmap().load(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityMainFullPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(fullPlayer);
                    }
                });
                fullPlayer_album.setImageURI(Uri.parse(song.getAlbumArt()));
            } else {
                Glide.with(ActivityMainFullPlayer.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityMainFullPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(fullPlayer);

                    }
                });
                fullPlayer_album.setImageResource(R.drawable.default_album_art);
            }

            fullPlayer_song.setText(song.getTitle());
            fullPlayer_artist.setText(song.getArtist());
            seekBar.setMax(MusicPlayback.mediaPlayer.getDuration());
            fullPlayer_maxTime.setText(MusicPlayback.getTime(MusicPlayback.mediaPlayer.getDuration()));
            if (MusicPlayback.getPlayingStatus()) {
                fullPlayer_play.setImageResource(R.drawable.music_widget_pause);
            } else {
                fullPlayer_play.setImageResource(R.drawable.music_widget_play);
            }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
        try{
            coverFlow.releaseAllMemoryResources();
        }catch (Exception e ){

        }

    }


    private class CloseApp extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();


        }
    }
    private class FeatureCoverflowAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return MusicPlayback.songSet.size();
        }

        @Override
        public Object getItem(int position) {
            return MusicPlayback.allTracks.get(MusicPlayback.songSet.get(position)).getAlbumArt();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView = getLayoutInflater().inflate(R.layout.adapter_coverflow,parent,false);
            }
            ImageView imageView = (ImageView)convertView.findViewById(R.id.coverflow_adapter_image);
            if(MusicPlayback.allTracks.get(MusicPlayback.songSet.get(position)).getAlbumArt()!=null){
                Glide.with(ActivityMainFullPlayer.this)
                        .asBitmap()
                        .load(MusicPlayback.allTracks.get(MusicPlayback.songSet.get(position)).getAlbumArt())
                        .into(imageView);}
            else {
                Glide.with(ActivityMainFullPlayer.this)
                        .asBitmap()
                        .load(R.drawable.default_album_art_track)
                        .into(imageView);}

            return convertView;
        }
    }
}




