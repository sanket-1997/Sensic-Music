package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import jp.wasabeef.blurry.Blurry;


public class ActivityMainPlayer extends AppCompatActivity {


public static ImageView search ;
private ImageView  more;
private TabLayout tabLayout;
private ViewPager viewPager;

public SharedPreferences musicPlayInfoHolder ;
private SharedPreferences sensorShared;
public  static  SharedPreferences.Editor editor;
public static int lastSongPlayed;
public  ImageView imageView_small_album_art , mainContainer;
public  TextView textView_small_song , textView_small_artist ;
public static TextView  textView_app_name;
public  ImageView imageView_small_play;
public static  int state = 0;
public static final int STATE_INVISIBLE = 0;
public static final int STATE_VISIBLE = 1;
private Proximity proximity;
private     IntentFilter intentFilter;
  public static EditText music_search;
  private final int SAMPLING=4;
    private SongInfoBroadcast songInfoBroadcast;
    private CloseApp closeApp;

    @Override
protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        setup();



MusicPlayback.context = getApplicationContext();


        if(MusicPlayback.shake ==null) {
            MusicPlayback.shake =  new Shake(getApplicationContext());
        }

        if(proximity==null){
            proximity=  new Proximity(this);
        }

sensorShared = getSharedPreferences("ActivitySettings",MODE_PRIVATE);

        songInfoBroadcast = new SongInfoBroadcast();
    intentFilter = new IntentFilter("myaction");


        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
music_search = (EditText)findViewById(R.id.music_search);
music_search.setVisibility(View.INVISIBLE);
textView_app_name = (TextView)findViewById(R.id.textView_AppName);
search = (ImageView)findViewById(R.id.imageView_search);
more = (ImageView)findViewById(R.id.imageView_displayMore);


        imageView_small_play =(ImageView)findViewById(R.id.imageView_small_song_play);
        if(MusicPlayback.getPlayingStatus()){
            imageView_small_play.setImageResource(R.drawable.icon_pause_small);

        }else {
            imageView_small_play.setImageResource(R.drawable.icon_play_small);
        }

     mainContainer = (ImageView) findViewById(R.id.mainContainer);


        musicPlayInfoHolder = getSharedPreferences("MusicData",MODE_PRIVATE);
        editor  = musicPlayInfoHolder.edit();
        SongListDatabase.VER = musicPlayInfoHolder.getInt("ver",1);
        if(SongListDatabase.VER == 1){

            Log.d("Ver",""+SongListDatabase.VER);
            PlayListDatabase playListDatabase = new PlayListDatabase(ActivityMainPlayer.this);
            playListDatabase.createPlaylist("Soft");
            playListDatabase.createPlaylist("Hard");
            new SongListDatabase(ActivityMainPlayer.this);
            SongListDatabase.VER++;
            editor.putInt("ver",SongListDatabase.VER).apply();
            Log.d("Ver",""+SongListDatabase.VER);
            PlayListDatabase database = new PlayListDatabase(getApplicationContext());
            MusicPlayback.playlist = database.getPlaylist();

        }

        lastSongPlayed = musicPlayInfoHolder.getInt("music",0);
        try{
             MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(lastSongPlayed);
        }catch (Exception e){
            lastSongPlayed=0;

        }
        MusicPlayback.isLoop = musicPlayInfoHolder.getBoolean("isLoop",false);
        MusicPlayback.isShuffle = musicPlayInfoHolder.getBoolean("isShuffle",true);



        /*
        * These are used to show shared preferences
        * */
        imageView_small_album_art = (ImageView)findViewById(R.id.imageView_small_song_album_art);
        textView_small_song = (TextView)findViewById(R.id.textView_small_song_song_name);
        textView_small_artist =(TextView)findViewById(R.id.textView_small_song_artist_name);
//Toast.makeText(getApplicationContext(),String.valueOf(lastSongPlayed),Toast.LENGTH_LONG).show();
try {


    textView_small_song.setText(MusicPlayback.allTracks.get(lastSongPlayed).getTitle());
    textView_small_artist.setText(MusicPlayback.allTracks.get(lastSongPlayed).getArtist());


    // Set Initial Background
    if (MusicPlayback.allTracks.get(lastSongPlayed).getAlbumArt() == null) {
        Glide.with(ActivityMainPlayer.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                Blurry.with(ActivityMainPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(mainContainer);
            }
        });
        imageView_small_album_art.setImageResource(R.drawable.default_album_art_track);
    } else {

        Glide.with(ActivityMainPlayer.this).asBitmap().load(MusicPlayback.allTracks.get(lastSongPlayed).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                Blurry.with(ActivityMainPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(mainContainer);
            }
        });
        Glide.with(ActivityMainPlayer.this).asBitmap().load(MusicPlayback.allTracks.get(lastSongPlayed).getAlbumArt()).into(imageView_small_album_art);


    }
}catch (Exception e){
    System.exit(0);
}



        tabLayout = (TabLayout)findViewById(R.id.tabLayout_fragment_holder);   // this will populate fragment tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() !=1){
                    search.setVisibility(View.INVISIBLE);
                    if(state==STATE_VISIBLE){
                    music_search.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(music_search.getWindowToken(),0);  // this is to hide the keyboard
                    music_search.setVisibility(View.INVISIBLE);
                    search.setImageResource(R.drawable.icon_search);
                    textView_app_name.setVisibility(View.VISIBLE);
                    state = STATE_INVISIBLE;
                    }
                }
                else{
                    search.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
     viewPager = (ViewPager)findViewById(R.id.viewpager_fragment_viewer);  // this is fragment holder



/// Fragments Adapter

        MyCustomFragmentAdapter myCustomFragmentAdapter = new MyCustomFragmentAdapter(getSupportFragmentManager());  // Custom Adapter for inflating fragments
        myCustomFragmentAdapter.add(new FragmentPlaylist(),"PLAYLIST");
        myCustomFragmentAdapter.add(new FragmentAllTracks() , "TRACKS");
        myCustomFragmentAdapter.add(new FragmentAlbum(),"ALBUMS");

        viewPager.setAdapter(myCustomFragmentAdapter);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);



        /// Search and More Images


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(state == STATE_INVISIBLE){
                    music_search.setVisibility(View.VISIBLE);

                    textView_app_name.setVisibility(View.INVISIBLE);
                    search.setImageResource(R.drawable.close);
                    state = STATE_VISIBLE;
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(mainContainer.getWindowToken(),
                            InputMethodManager.SHOW_FORCED,0);
                }
                else if(state == STATE_VISIBLE){

                        music_search.setText("");
                        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(music_search.getWindowToken(),0);  // this is to hide the keyboard
                        music_search.setVisibility(View.INVISIBLE);
                        textView_app_name.setVisibility(View.VISIBLE);
                        search.setImageResource(R.drawable.icon_search);
                        state=STATE_INVISIBLE;

                }
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu p = new PopupMenu(ActivityMainPlayer.this , v);
                p.getMenuInflater().inflate(R.menu.more_options,p.getMenu());
                p.show();
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       switch (item.getItemId()){
                           case R.id.menu_more_action_guide :
                               AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainPlayer.this);
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
                               AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityMainPlayer.this);
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
                           case R.id.menu_more_action_equalizer :
                               if(MusicPlayback.getSongPosition() != -1){
                               startActivity(new Intent(getApplicationContext(),ActivityEqualizer.class));
                               overridePendingTransition(R.anim.left_enter_translate , R.anim.right_enter_translate);}
                                else
                                    Toast.makeText(getApplicationContext(),"START MUSIC FIRST...",Toast.LENGTH_SHORT).show();
                       }
                        return true;
                    }
                });
            }
        });


        music_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Intent intent = new Intent("data");
                intent.putExtra("song",s.toString());
                sendBroadcast(intent);
            }
        });





         more.setOnLongClickListener(new View.OnLongClickListener() {
             @Override
             public boolean onLongClick(View v) {
                showPopup(v);
                 return true;
             }
         });

        search.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
               showPopup(v);
                return true;
            }
        });

    }

    @Override
protected void onResume() {

        super.onResume();
        registerReceiver(songInfoBroadcast,intentFilter);
        MusicPlayback.isMainPlayerOpen = true;

        if(sensorShared.getBoolean("Pro",true)){
            proximity.start();

        }
        if(sensorShared.getBoolean("Acc",true)){
            MusicPlayback.shake.start();
        }


    }

    @Override
protected void onPause() {
        super.onPause();
    unregisterReceiver(songInfoBroadcast);
        MusicPlayback.isMainPlayerOpen = true;
        Log.e("Main Player", ""+MusicPlayback.isMainPlayerOpen);
        if(sensorShared.getBoolean("Pro",true)){
          proximity.stop();
        }
    }

    @Override
protected void onDestroy() {
        MusicPlayback.isMainPlayerOpen = false;
        music_search=null;
        search=null;
        super.onDestroy();
    }

private  void showPopup(View view){

       switch (view.getId()){
           case R.id.imageView_search : if(state == STATE_INVISIBLE)
                                        showToolTip(search,"Search");
                                        else
                                            showToolTip(search,"Close Search");
                                       break;
           case R.id.imageView_displayMore  : showToolTip(more , "More Options");
                                            break;
       }



    }

private void showToolTip(View view , String message ){
        ImageView imageView = (ImageView)view;
        final Tooltip tooltip = new Tooltip.Builder(imageView).setText(message).setBackgroundColor(Color.argb(255,240,240,240)).setGravity(Gravity.BOTTOM).setDismissOnClick(true).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               tooltip.dismiss();
            }
        },1000);


    }

public  void startPause(View view){

    if(MusicPlayback.getPlayingStatus()){
        imageView_small_play.setImageResource(R.drawable.icon_play_small);
        MusicPlayback.pauseMediaPlayback();
    }
    else{

        if(MusicPlayback.getSongPosition()==-1){
            Intent intent = new Intent(getApplicationContext(), MusicPlayback.class);
            Intent intent1 = new Intent(getApplicationContext(),EqualizerService.class);
            intent.putExtra("Song Position",lastSongPlayed);
            MusicPlayback.songPosition = lastSongPlayed;
            MusicPlayback.cursor = MusicPlayback.songPosition;
            sendBroadcast(new Intent("myaction"));
            startService(intent);
            startService(intent1);
        }
        else{
            MusicPlayback.resumeMediaPlayback();
        }
        imageView_small_play.setImageResource(R.drawable.icon_pause_small);

    }


}

public  void playNext(View view){
    Log.d("log_next","next");

    if(MusicPlayback.getPlayingStatus()){

        MusicPlayback.startMediaPlayback(MusicPlayback.getPlayNextMediaIndex());
        updateMainUI(MusicPlayback.songPosition);
        Log.d("log_next","next_true");
    }
    else{
        Log.d("log_next","next_false");
        if(MusicPlayback.getSongPosition()==-1){
            Intent intent = new Intent(getApplicationContext(), MusicPlayback.class);
            Intent intent1 = new Intent(getApplicationContext(),EqualizerService.class);
            lastSongPlayed++;
            if(lastSongPlayed==MusicPlayback.allTracks.size()){
               MusicPlayback.songPosition = lastSongPlayed=0;

            }
            Log.e("Last Song Played", String.valueOf(lastSongPlayed));
            intent.putExtra("Song Position",lastSongPlayed);
            MusicPlayback.songPosition = lastSongPlayed;
            updateMainUI(MusicPlayback.songPosition);
            startService(intent);
            startService(intent1);
        }
        else{

            MusicPlayback.startMediaPlayback(MusicPlayback.getPlayNextMediaIndex());
            updateMainUI(MusicPlayback.songPosition);
        }


        imageView_small_play.setImageResource(R.drawable.icon_pause_small);
    }
    //MusicPlayback.songPosition=index;
    //Log.d("index position",String.valueOf(index));
    Log.d("song position",String.valueOf(MusicPlayback.songPosition));


    sendBroadcast(  new Intent("myaction"));
  //  updateMainUI(MusicPlayback.songPosition);

}

public  void playPrev(View view){

    if(MusicPlayback.getPlayingStatus()){
        if(MusicPlayback.mediaPlayer.getCurrentPosition()>5000){
            MusicPlayback.startMediaPlayback(MusicPlayback.songPosition);
        }
        else{
            MusicPlayback.startMediaPlayback(MusicPlayback.getPlayPrevMediaIndex());

        }
        updateMainUI(MusicPlayback.songPosition);
    }
    else{
        if(MusicPlayback.getSongPosition()==-1){
            Intent intent = new Intent(getApplicationContext(),MusicPlayback.class);
            Intent intent1 = new Intent(getApplicationContext(),EqualizerService.class);
            lastSongPlayed--;
            if(lastSongPlayed==-1){
                lastSongPlayed= MusicPlayback.allTracks.size()-1;
                 MusicPlayback.songPosition=lastSongPlayed;
                updateMainUI(MusicPlayback.songPosition);
            }
            intent.putExtra("Song Position",lastSongPlayed);
            startService(intent);
            startService(intent1);

            Log.e("Play Prev Position", "song position = "+lastSongPlayed);
        }
        else{

            MusicPlayback.startMediaPlayback( MusicPlayback.getPlayPrevMediaIndex());
            Log.e("Play Prev Position", "song position = -1");
            updateMainUI(MusicPlayback.songPosition);

        }
        imageView_small_play.setImageResource(R.drawable.icon_pause_small);
    }



    //Log.d("index Position", String.valueOf(MusicPlayback.songPosition));
    Log.d("song position",String.valueOf(MusicPlayback.songPosition));
    Intent i =  new Intent("myaction");
    sendBroadcast(i);




}

public  void updateMainUI(int index){


    if(MusicPlayback.allTracks.get(index).getAlbumArt()!=null) {
        Glide.with(ActivityMainPlayer.this).asBitmap().load(MusicPlayback.allTracks.get(index).getAlbumArt()).into(imageView_small_album_art);
       // imageView_small_album_art.setImageURI(Uri.parse(MusicPlayback.allTracks.get(index).getAlbumArt()));
        Log.d("log_update",""+index);

    }
    else {
        imageView_small_album_art.setImageResource(R.drawable.default_album_art_track);
        Log.d("log_update",""+index);

    }
    Log.d("log_update",""+MusicPlayback.allTracks.get(index).getTitle());
    textView_small_song.setText(MusicPlayback.allTracks.get(index).getTitle());
    textView_small_artist.setText(MusicPlayback.allTracks.get(index).getArtist());



}

private void setup(){

    if(MusicPlayback.allTracks==null){
      finish();
    }




}




    // Innner Class for Fragments

private  class MyCustomFragmentAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> fragment = new ArrayList<>();
    private  ArrayList<String> fName = new ArrayList<>();
    public MyCustomFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    public void  add(Fragment f , String n){
        fragment.add(f);
        fName.add(n);
    }




    @Override
    public Fragment getItem(int position) {
        return fragment.get(position);
    }

    @Override
    public int getCount() {
        return fragment.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fName.get(position);
    }
}


public void openFullMusicPlayer(View view){

    if(MusicPlayback.songPosition != -1 ){
        startActivity(new Intent(getApplicationContext(), ActivityMainFullPlayer.class));
    }
    else{
        Toast.makeText(getApplicationContext(),"START MUSIC FIRST...",Toast.LENGTH_SHORT).show();
    }
}
    class SongInfoBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            ex();


        }
    }
    private void ex(){
    int index;
        if(MusicPlayback.getPlayingStatus()){
            imageView_small_play.setImageResource(R.drawable.icon_pause_small);
        }
        else {
            imageView_small_play.setImageResource(R.drawable.icon_play_small);
        }

if(MusicPlayback.getSongPosition()==-1){index=lastSongPlayed;}
else index = MusicPlayback.getSongPosition();
        if(MusicPlayback.allTracks.get(index).getAlbumArt()!=null){

            try {
                Glide.with(ActivityMainPlayer.this).asBitmap().load(MusicPlayback.allTracks.get(index).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityMainPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(mainContainer);

                       imageView_small_album_art.setImageBitmap(resource);
                    }
                });


            }catch (Exception e){

            }


        }
        else{
            try {
                Glide.with(ActivityMainPlayer.this).asDrawable().load(R.drawable.default_album_art_track).into(imageView_small_album_art);
                Glide.with(ActivityMainPlayer.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        Blurry.with(ActivityMainPlayer.this).radius(30).async().sampling(SAMPLING).from(resource).into(mainContainer);
                    }
                });


            }catch (Exception e){

            }

        }

        String songName = MusicPlayback.allTracks.get(index).getTitle();
       String artistName = MusicPlayback.allTracks.get(index).getArtist();
       textView_small_song.setText(songName);
    textView_small_artist.setText(artistName);

    }

    class CloseApp extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();


        }
    }
}
