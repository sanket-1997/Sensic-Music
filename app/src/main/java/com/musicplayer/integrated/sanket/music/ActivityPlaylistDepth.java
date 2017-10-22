package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.l4digital.fastscroll.FastScrollRecyclerView;


import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.blurry.Blurry;

public class ActivityPlaylistDepth extends AppCompatActivity {

    private final int SAMPLING = 4;
    private ImageView container;
    private FastScrollRecyclerView list;
    private TextView title,trackCount,ripple;
    private SharedPreferences sharedPreferences,shared;
    private int position;
    private Proximity proximity;
    private SharedPreferences.Editor editor;
    private CustomAdapter adapter;
    private    MyReceiver receiver;
    private IntentFilter intentFilter;
    private HashMap<Integer , Integer> trackPosition;
    private CloseApp closeApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
        setup_();
        setContentView(R.layout.activity_playlist_depth);
        trackPosition = new HashMap<>();
        container = (ImageView) findViewById(R.id.playlist_depth_container);
        ripple = (TextView)findViewById(R.id.textView_playlist_depth_ripple);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ActivityPlaylistDepth.this,
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider,null));
        if(proximity==null)proximity=new Proximity(this);
        sharedPreferences = getSharedPreferences("ActivitySettings",MODE_PRIVATE);
        shared =  getSharedPreferences("MusicData",MODE_PRIVATE);
        editor = shared.edit();
        receiver = new MyReceiver();
        intentFilter = new IntentFilter("myaction");


        ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        try{
        if(MusicPlayback.getSongPosition()==-1){
            if (MusicPlayback.allTracks.get(ActivityMainPlayer.lastSongPlayed).getAlbumArt() == null) {
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                    }
                });


            } else {
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(MusicPlayback.allTracks.get(ActivityMainPlayer.lastSongPlayed).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                    }
                });
            }

        }
        else{
            if (MusicPlayback.allTracks.get(MusicPlayback.songPosition).getAlbumArt() == null) {
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                    }
                });


            } else {
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(MusicPlayback.allTracks.get(MusicPlayback.getSongPosition()).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                        Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                    }
                });
            }

        }}catch (Exception e){
            System.exit(0);
        }
        adapter = new CustomAdapter();

        title = (TextView)findViewById(R.id.textView_playlist_depth_title);
        trackCount = (TextView)findViewById(R.id.textView_playlist_depth_track_count);
        Intent intent = getIntent();
        title.setText(intent.getStringExtra("Title"));
        list = (FastScrollRecyclerView)findViewById(R.id.recylcerView_playList_depth_list_all);
        list.addItemDecoration(dividerItemDecoration);
        list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        list.setAdapter(adapter);
        MusicPlayback.tShuffleSet = null;


        if(!title.getText().toString().equals(MusicPlayback.musicSet)) {

            MusicPlayback.tShuffleSet = new ArrayList<>();
            for(int i = 0 ; i<MusicPlayback.songDatabaseSet.size();i++){
                MusicPlayback.tShuffleSet.add(MusicPlayback.songDatabaseSet.get(i));
            }
            MusicPlayback.tShuffleSet = new ShuffleLogic(MusicPlayback.tShuffleSet.size()).setArray(MusicPlayback.tShuffleSet).shuffle().getShuffleOrder();

        }



        String s = MusicPlayback.songDatabaseSet.size()>1 ? "s" : "";
        trackCount.setText(String.format("Track%s : %d ",s,MusicPlayback.songDatabaseSet.size()));

    }


    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.adapter_track,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ModelSongs s =MusicPlayback.allTracks.get(MusicPlayback.songDatabaseSet.get(position));

            holder.song.setText(s.getTitle());
            holder.artist.setText(s.getArtist());
            if( s.getAlbumArt() == null)
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(R.drawable.default_album_art_track).into(holder.albumArt);

            else
                Glide.with(ActivityPlaylistDepth.this).asBitmap().load(s.getAlbumArt()).into(holder.albumArt);


        }



        @Override
        public int getItemCount() {
            return MusicPlayback.songDatabaseSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
            public ImageView albumArt;
            public TextView song , artist ,ripple;

            public ViewHolder(View itemView) {
                super(itemView);
                albumArt = (ImageView)itemView.findViewById(R.id.imageView_display_track_album_art);
                song = (TextView)itemView.findViewById(R.id.textView_display_track_song_name);
                artist = (TextView)itemView.findViewById(R.id.textView_display_track_artist_name);
                ripple = (TextView)itemView.findViewById(R.id.textView_display_track_ripple_area);
                ripple.setOnClickListener(this);
                ripple.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                position = getLayoutPosition();

                MusicPlayback.isDisturbed=false;

                if(MusicPlayback.tShuffleSet !=null){

                        MusicPlayback.setChanged = true;
                        MusicPlayback.songSet = MusicPlayback.songDatabaseSet;
                        MusicPlayback.shuffleSet = MusicPlayback.tShuffleSet;
                        MusicPlayback.musicSet=title.getText().toString();
                        MusicPlayback.tShuffleSet=null;
                        Log.d("Playlist","Changed");


                }


                setup();





                Log.d("ShufflePos",""+MusicPlayback.shuffleIndexPosition+":"+MusicPlayback.cursor);


            }

            @Override
            public boolean onLongClick(View v) {
               position = getLayoutPosition();

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityPlaylistDepth.this);
                View view = getLayoutInflater().inflate(R.layout.playlist_depth_options_dialog,null);
                TextView remove = (TextView)view.findViewById(R.id.textView_playlist_depth_options_remove);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();



                remove.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Log.d("Table ", SongListDatabase.table);
                        SongListDatabase d = new SongListDatabase(ActivityPlaylistDepth.this, SongListDatabase.table);
                        d.deletePlaylist(MusicPlayback.allTracks.get(MusicPlayback.songDatabaseSet.get(position)).getSongID());
                        MusicPlayback.songDatabaseSet = null;
                        MusicPlayback.tShuffleSet = null;
                        MusicPlayback.tShuffleSet = new ArrayList<Integer>();
                        MusicPlayback.songDatabaseSet = d.getPlaylistSongs();
                        for (int i = 0; i < MusicPlayback.songDatabaseSet.size(); i++) {
                            MusicPlayback.tShuffleSet.add(MusicPlayback.songDatabaseSet.get(i));
                        }
                        MusicPlayback.tShuffleSet = new ShuffleLogic(MusicPlayback.tShuffleSet.size()).setArray(MusicPlayback.tShuffleSet).shuffle().getShuffleOrder();

                        MusicPlayback.songSet = MusicPlayback.songDatabaseSet;
                        MusicPlayback.shuffleSet = MusicPlayback.tShuffleSet;
                        MusicPlayback.setChanged = true;
                        String s = MusicPlayback.songDatabaseSet.size()>1 ? "s" : "";
                        trackCount.setText(String.format("Track%s : %d ",s,MusicPlayback.songDatabaseSet.size()));
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }

                });

                return true;
            }
        }


        public void  setup(){
            MusicPlayback.isDisturbed=false;
            MusicPlayback.value=0;
            if(MusicPlayback.getSongPosition()==-1){
                Intent intent = new Intent(getApplicationContext() , MusicPlayback.class);
                Intent intent1 = new Intent(getApplicationContext(),EqualizerService.class);
                intent.putExtra("Song Position",MusicPlayback.songSet.get(position));
                MusicPlayback.songPosition = MusicPlayback.songSet.get(position);
                startService(intent);
                startService(intent1);


                startActivity(new Intent(getApplicationContext(), ActivityMainFullPlayer.class ));
                overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);

            }
            else{


                        MusicPlayback.startMediaPlayback(MusicPlayback.songSet.get(position));
                        Log.d("PlayList",""+position +" : "+ MusicPlayback.cursor );

                        startActivity(new Intent(getApplicationContext(), ActivityMainFullPlayer.class ));
                        overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);


                MusicPlayback.songPosition = MusicPlayback.songSet.get(position);

            }
            sendBroadcast(new Intent("myaction"));

        }

    }
    private void setup_(){
        if(MusicPlayback.allTracks==null){

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,intentFilter);
        if(sharedPreferences.getBoolean("Pro",true)){
            proximity.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(sharedPreferences.getBoolean("Pro",true)){
            proximity.stop();
        }
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_exit_translate,R.anim.right_exit_translate);

    }

    public  class  MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

                ModelSongs song = MusicPlayback.allTracks.get(MusicPlayback.songPosition);
                if (song.getAlbumArt() != null) {
                    Glide.with(ActivityPlaylistDepth.this).asBitmap().load(song.getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                        }
                    });

                } else {
                    Glide.with(ActivityPlaylistDepth.this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                            Blurry.with(ActivityPlaylistDepth.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);

                        }
                    });
                }

        }
    }


    private class CloseApp extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();


        }
    }

}
