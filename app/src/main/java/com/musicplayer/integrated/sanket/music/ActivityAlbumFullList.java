package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class ActivityAlbumFullList extends AppCompatActivity {

    private  final int SAMPLING = 4;
    private ArrayList<ModelSongs> modelSongsArrayList;
private ArrayList<Integer>songsArrayIndex;
private  String albumName;
private ListView listView;
private ImageView container;
private ImageView albumArt;
private TextView album ;
private  int layoutPos;
private  AlertDialog dialogRef;
private CloseApp closeApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
        setup();
        setContentView(R.layout.activity_album_full_list);
        container = (ImageView) findViewById(R.id.album_full_list_container);
        album = (TextView)findViewById(R.id.textView_album_full_list_album_name);
        albumArt = (ImageView)findViewById(R.id.imageView_album_full_list);
        albumArt.setAlpha(0.8f);

        modelSongsArrayList = null;
        songsArrayIndex = null;
        modelSongsArrayList = new ArrayList<>();
        songsArrayIndex = new ArrayList<>();
        final Intent intent = getIntent();
        albumName = intent.getStringExtra("Album Name");
        layoutPos = intent.getIntExtra("Int",0);
        MusicPlayback.tSongSet = null;
        MusicPlayback.tShuffleSet = null;
        MusicPlayback.tSongSet = new ArrayList<>();
        MusicPlayback.tShuffleSet = new ArrayList<>();
        try {
            for(int i =0 ; i<MusicPlayback.allTracks.size();i++){
                if(MusicPlayback.allTracks.get(i).getAlbum().equals(albumName)){
                    modelSongsArrayList.add(MusicPlayback.allTracks.get(i));
                    songsArrayIndex.add(i);
                    MusicPlayback.tSongSet.add(i);
                }
            }
        }catch (Exception e){
            System.exit(0);
        }


        for(int i = 0 ; i<MusicPlayback.tSongSet.size();i++){
             MusicPlayback.tShuffleSet.add(MusicPlayback.tSongSet.get(i));
        }
        MusicPlayback.tShuffleSet = new ShuffleLogic(MusicPlayback.tShuffleSet.size()).setArray(MusicPlayback.tShuffleSet).shuffle().getShuffleOrder();

        //MusicPlayback.songSetSize = MusicPlayback.songSet.size();
        album.setText(albumName);
        if(MusicPlayback.albums.get(layoutPos).getAlbumArt() == null){
            albumArt.setImageResource(R.drawable.default_album_art);
            Glide.with(this).asBitmap().load(R.drawable.default_background).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                    Blurry.with(ActivityAlbumFullList.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);
                }
            });


        }
        else{
            albumArt.setImageURI(Uri.parse(MusicPlayback.albums.get(layoutPos).getAlbumArt()));

            Glide.with(this).asBitmap().load(MusicPlayback.albums.get(layoutPos).getAlbumArt()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                    Blurry.with(ActivityAlbumFullList.this).radius(30).async().sampling(SAMPLING).from(resource).into(container);
                }
            });


        }


        listView = (ListView)findViewById(R.id.listView_album_full_list);
        listView.setAdapter(new CustomAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                MusicPlayback.songSet = MusicPlayback.tSongSet;
                MusicPlayback.shuffleSet = MusicPlayback.tShuffleSet;
                MusicPlayback.setChanged = true;
                MusicPlayback.musicSet="";

                if(MusicPlayback.getSongPosition()==-1){
                    Intent intent = new Intent(getApplicationContext() , MusicPlayback.class);
                    Intent intent1 = new Intent(getApplicationContext(),EqualizerService.class);
                    intent.putExtra("Song Position",songsArrayIndex.get(position));
                    startService(intent);
                    startService(intent1);
                    sendBroadcast(new Intent("myaction"));
                    MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(songsArrayIndex.get(position));
                    startActivity(new Intent(getApplicationContext(), ActivityMainFullPlayer.class));
                    overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);


                }
                else{

                    MusicPlayback.startMediaPlayback(songsArrayIndex.get(position));
                    sendBroadcast(new Intent("myaction"));
                    MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(songsArrayIndex.get(position));
                    startActivity(new Intent(getApplicationContext(), ActivityMainFullPlayer.class ));
                    overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);
                }
                MusicPlayback.cursor = position;
                MusicPlayback.isDisturbed=false;


            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, int position, long id) {
                layoutPos = position;
                final AlertDialog.Builder builder =new  AlertDialog.Builder(ActivityAlbumFullList.this);
                View v = getLayoutInflater().inflate(R.layout.manage_to_playlist_dialog,parent,false);
                TextView add = (TextView)v.findViewById(R.id.textView_manage_to_playlist_add_to_list);

                builder.setView(v);
                final AlertDialog dialog = builder.create();
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityAlbumFullList.this);
                        View view1 = getLayoutInflater().inflate(R.layout.manage_to_list_add_dialog,null);
                        ListView listView = (ListView)view1.findViewById(R.id.listView_list_all_playlist);
                        listView.setAdapter(new CustomListAdapter());
                        builder1.setView(view1);
                        AlertDialog dialog1 = builder1.create();
                        dialogRef = dialog1;
                        dialog1.show();
                    }
                });
                return true;
            }
        });

    }

    private void setup(){
        if(MusicPlayback.allTracks==null){
            finish();
        }
    }
    private class  CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return modelSongsArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return modelSongsArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.adapter_album_full,parent,false);

            }

            TextView songName = (TextView)convertView.findViewById(R.id.textView_album_full_list_adapter_song_name);
            TextView totalTime = (TextView)convertView.findViewById(R.id.textView_album_full_list_adapter_time_duration);
            songName.setText(modelSongsArrayList.get(position).getTitle());
            totalTime.setText(MusicPlayback.getTime(modelSongsArrayList.get(position).getTotalDuration()));
            return  convertView;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(R.anim.left_exit_translate,R.anim.right_exit_translate);


    }

    private class CustomListAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return   MusicPlayback.playlist.size();
        }

        @Override
        public Object getItem(int position) {
            return   MusicPlayback.playlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.adapter_all_playlist,parent,false);

            }
            TextView textView = (TextView)convertView.findViewById(R.id.textView_custom_all_playlist_name);
            textView.setText(MusicPlayback.playlist.get(position));
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SongListDatabase.table = MusicPlayback.playlist.get(position);
                    new SongListDatabase(getApplicationContext(),SongListDatabase.table).enterSongPos( MusicPlayback.allTracks.get(MusicPlayback.tSongSet.get(layoutPos)).getSongID());
                    dialogRef.dismiss();
                }
            });

            return convertView;
        }
    }
    private class CloseApp extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();


        }
    }

}


