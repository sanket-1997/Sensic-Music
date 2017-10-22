package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.l4digital.fastscroll.FastScrollRecyclerView;


import java.util.ArrayList;




/**
 * Created by Sanket on 28-06-2017.
 */

public class FragmentAllTracks extends Fragment {

    private FastScrollRecyclerView recyclerView_allTracks;

    private int pos = 0,color=0;
    public  CustomAllTracksAdapter adapter;
    private  String songName , artistName;


    public  TextView textView_small_song_info_song_name , textView_small_song_info_artist_name ;


    private SongInfoBroadcast songInfoBroadcast;
    private SearchGetBroadcast searchGetBroadcast;
    private  AlertDialog alertDialog;
    private   int layoutPos =0;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tracks,container,false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.line_divider,null));
        recyclerView_allTracks = (FastScrollRecyclerView) root.findViewById(R.id.recylcerView_all_tracks_fragment_list);
        recyclerView_allTracks.addItemDecoration(dividerItemDecoration);

        adapter = new CustomAllTracksAdapter(MusicPlayback.allTracks);



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_allTracks.setLayoutManager(layoutManager);
        recyclerView_allTracks.setAdapter(adapter);


        textView_small_song_info_song_name = (TextView)getActivity().findViewById(R.id.textView_small_song_song_name);
        textView_small_song_info_artist_name=(TextView)getActivity().findViewById(R.id.textView_small_song_artist_name);
        textView_small_song_info_song_name.setSelected(true);
        textView_small_song_info_artist_name.setSelected(true);


        if(MusicPlayback.songPosition>=0) {

            pos = MusicPlayback.songPosition;
            songName= MusicPlayback.allTracks.get(pos).getTitle();
            artistName = MusicPlayback.allTracks.get(pos).getArtist();
            textView_small_song_info_song_name.setText(songName);
            textView_small_song_info_artist_name.setText(artistName);


        }
        songInfoBroadcast = new SongInfoBroadcast();
        searchGetBroadcast = new SearchGetBroadcast();
        IntentFilter intentFilter = new IntentFilter("myaction");
       getContext().registerReceiver(songInfoBroadcast,intentFilter);

         IntentFilter songIntentFilter = new IntentFilter("data");
        getContext().registerReceiver(searchGetBroadcast,songIntentFilter);



        return root;
    }




    /// Adapter Class

    public  class CustomAllTracksAdapter extends  RecyclerView.Adapter<CustomAllTracksAdapter.ViewHolder>  implements com.l4digital.fastscroll.FastScroller.SectionIndexer {

        ArrayList<ModelSongs> c;




     public CustomAllTracksAdapter(ArrayList<ModelSongs> arrayList){
         c=arrayList;
     }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_track,parent,false);

            return  new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {



                    holder.textView_all_tracks_song_name.setText( c.get(position).getTitle());
                    holder.textView_all_tracks_artist_name.setText( c.get(position).getArtist());
                    holder.x = position;

                    if( c.get(position).getAlbumArt() == null) {
                            Glide.with(getContext()).asBitmap().load(R.drawable.default_album_art_track).into(holder.imageView_all_tracks_album_art);

                    }else {
                      Glide.with(getContext()).asBitmap().load(c.get(position).getAlbumArt()).into(holder.imageView_all_tracks_album_art);

                    }
            if(pos!=holder.x){
                holder.textView_all_tracks_song_name.setTextColor(Color.parseColor("#f5f5f5"));
                holder.textView_all_tracks_artist_name.setTextColor(Color.parseColor( "#f5f5f5"));
            }
            else{
                if(color!=0) {
                    holder.textView_all_tracks_song_name.setTextColor(color);
                  holder.textView_all_tracks_artist_name.setTextColor(color);

                    }
                else{
                    holder.textView_all_tracks_song_name.setTextColor(Color.parseColor("#f5f5f5"));
                    holder.textView_all_tracks_artist_name.setTextColor(Color.parseColor( "#f5f5f5"));

                }


            }






        }



        @Override
        public int getItemCount() {
            return  c.size();
        }

        @Override
        public String getSectionText(int position) {
            char c = MusicPlayback.allTracks.get(position).getTitle().toUpperCase().charAt(0);
            if(!Character.isAlphabetic(c))
                   return "#";

            return  MusicPlayback.allTracks.get(position).getTitle().toUpperCase().substring(0,1);
        }


        public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {

                public  ImageView imageView_all_tracks_album_art;
                private int x;
                public  TextView textView_all_tracks_song_name , textView_all_tracks_artist_name , textView_all_tracks_ripple_area;


            public ViewHolder(View itemView) {
                super(itemView);


                imageView_all_tracks_album_art = (ImageView)itemView.findViewById(R.id.imageView_display_track_album_art);
                imageView_all_tracks_album_art.setScaleType(ImageView.ScaleType.CENTER_CROP);
                textView_all_tracks_song_name = (TextView)itemView.findViewById(R.id.textView_display_track_song_name);
                textView_all_tracks_artist_name = (TextView)itemView.findViewById(R.id.textView_display_track_artist_name);
                textView_all_tracks_ripple_area = (TextView)itemView.findViewById(R.id.textView_display_track_ripple_area);
                textView_all_tracks_ripple_area.setOnClickListener(this);
                textView_all_tracks_ripple_area.setOnLongClickListener(this);

            }

            @Override
            public void onClick(View v) {
                int temp=0;

                if(MusicPlayback.songSet.size() != MusicPlayback.allTracks.size()){
                    Log.d("Set Changed","heck");
                    MusicPlayback.setChanged = true;
                }
                MusicPlayback.shuffleSet = MusicPlayback.shuffleIndex;
                MusicPlayback.songSet = MusicPlayback.totalSongs;
                Log.d("Song Set Size",""+MusicPlayback.songSet.size());
                pos = x;
                MusicPlayback.cursor = pos;
                MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(MusicPlayback.cursor);
                MusicPlayback.musicSet="";
                Log.d("Cursor",""+MusicPlayback.cursor);
                Log.d("Shuffle",""+MusicPlayback.shuffleIndexPosition);



                if(c.size()<MusicPlayback.allTracks.size()){


                    for(int i = 0; i<MusicPlayback.allTracks.size();i++){
                        if(c.get(pos).getTitle().equals(MusicPlayback.allTracks.get(i).getTitle())){
                            adapter=null;
                            adapter = new CustomAllTracksAdapter(MusicPlayback.allTracks);
                            recyclerView_allTracks.setAdapter(adapter);
                            temp=i;



                            if(MusicPlayback.getSongPosition()!=-1){
                               MusicPlayback.startMediaPlayback(temp);

                            }
                            else{
                                Intent intent =  new Intent(getContext(),MusicPlayback.class);
                                Intent intent1 = new Intent(getContext(),EqualizerService.class);
                                intent.putExtra("Song Position",temp);
                                getActivity().startService(intent);
                                getActivity().startService(intent1);
                            }


                                break;

                        }

                    }
                    pos = temp;
                    startActivity(  new Intent(getContext() , ActivityMainFullPlayer.class));
                    ActivityMainPlayer.search.setImageResource(R.drawable.icon_search);
                    getActivity().overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);

                    ActivityMainPlayer.textView_app_name.setVisibility(View.VISIBLE);
                    ActivityMainPlayer.music_search.setText("");
                    ActivityMainPlayer.music_search.setVisibility(View.INVISIBLE);
                    ActivityMainPlayer.state= ActivityMainPlayer.STATE_INVISIBLE;

                    ex();
                    Log.e("come","out");
                    MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(pos);
                    songName =MusicPlayback.allTracks.get(pos).getTitle();
                    artistName = MusicPlayback.allTracks.get(pos).getArtist();



                }
                else{

                    MusicPlayback.shuffleIndexPosition = MusicPlayback.trackPosition.get(pos);


                    if(MusicPlayback.getPlayingStatus()){
                        if(pos == MusicPlayback.getSongPosition()){
                            // When same is song is clicked open new Big player



                            startActivity(  new Intent(getContext() , ActivityMainFullPlayer.class));
                            getActivity().overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);
                            Log.d("Song Condition","if pos == getPlayingStatus");
                        }
                        else{
                            MusicPlayback.stopMediaPlayback();
                            playAndChange();
                            Log.d("Song Condition","if pos != getPlayingStatus");
                        }

                    }
                    else{
                        playAndChange();
                        Log.d("Song Condition","Starting Condition");
                    }






                }





            }
    public  void playAndChange(){
            changeColor(pos);
            if(MusicPlayback.getSongPosition()==-1){
            Intent intent = new Intent(getContext(), MusicPlayback.class);
            Intent intent1 = new Intent(getContext(),EqualizerService.class);
            intent.putExtra("Song Position",pos);
            getContext().startService(intent);
            getContext().startService(intent1);

            }else{
                MusicPlayback.startMediaPlayback(pos);
            }
        }

            private  void changeColor(int pos){
                songName = c.get(pos).getTitle();
                artistName = c.get(pos).getArtist();

                 ex();
                textView_small_song_info_song_name.setText(songName);
                textView_small_song_info_artist_name.setText( artistName);
                adapter.notifyDataSetChanged();

            }

            @Override
            public boolean onLongClick(View v) {
                layoutPos = getLayoutPosition();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final View view = getActivity().getLayoutInflater().inflate(R.layout.manage_to_playlist_dialog,null);
                TextView add = (TextView)view.findViewById(R.id.textView_manage_to_playlist_add_to_list);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        View view1 = getActivity().getLayoutInflater().inflate(R.layout.manage_to_list_add_dialog,null);
                         ListView listView = (ListView)view1.findViewById(R.id.listView_list_all_playlist);
                       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,MusicPlayback.playlist);
                        listView.setAdapter(new ListCustomAdapter());
                        builder1.setView(view1);


                        AlertDialog dialog1 = builder1.create();
                        alertDialog = dialog1;
                        listView.setClickable(true);

                        dialog1.show();



                    }
                });


                return true;
            }


        }


        private   class ListCustomAdapter extends BaseAdapter{

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
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.adapter_all_playlist,parent,false);

                }
                TextView textView = (TextView)convertView.findViewById(R.id.textView_custom_all_playlist_name);
                textView.setText(MusicPlayback.playlist.get(position));
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(c.size()<MusicPlayback.allTracks.size()){
                            for( int i = 0 ; i<MusicPlayback.allTracks.size();i++){
                                if(c.get(layoutPos).getTitle().equals(MusicPlayback.allTracks.get(i).getTitle())){
                                    SongListDatabase.table = MusicPlayback.playlist.get(position);
                                    new SongListDatabase(getContext(),SongListDatabase.table).enterSongPos(MusicPlayback.allTracks.get(i).getSongID());
                                    break;
                                }

                            }
                        }
                        else{
                            SongListDatabase.table = MusicPlayback.playlist.get(position);
                            new SongListDatabase(getContext(),SongListDatabase.table).enterSongPos( MusicPlayback.allTracks.get(layoutPos).getSongID());

                        }
                        alertDialog.dismiss();
                    }
                });
                return convertView;
            }
        }

    }


    class SearchGetBroadcast extends  BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

           String s = intent.getStringExtra("song");
            ArrayList<ModelSongs> tempList = new ArrayList<>();
            for(ModelSongs so : MusicPlayback.allTracks){

                if(so.getTitle().toLowerCase().contains(s.toLowerCase())){
                    tempList.add(so);

                }
            }


            Log.d("broad",s);
            if(tempList.size()>=0 && tempList.size() != MusicPlayback.allTracks.size()) {
                adapter = null;
                adapter = new CustomAllTracksAdapter(tempList);
                recyclerView_allTracks.setAdapter(adapter);

                for(ModelSongs modelSongs :tempList){
                    Log.e("ModelSongs", modelSongs.getTitle());
                }

            }
            else {
                adapter = null;
                adapter = new CustomAllTracksAdapter(MusicPlayback.allTracks);
                recyclerView_allTracks.setAdapter(adapter);
            }
        }
    }

    class SongInfoBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            pos = MusicPlayback.getSongPosition();
            Log.d("log_position",""+pos);
            ex();
            adapter.notifyDataSetChanged();

        }
    }

  private void ex() {

      if (MusicPlayback.allTracks.get(pos).getAlbumArt() != null) {


          Bitmap bitmap = ImageEnhancer.getConvertedImage(MusicPlayback.allTracks.get(pos).getAlbumArt(), 500);
          try {
              color = Palette.from(bitmap).generate().getVibrantColor(Color.argb(255, 29, 202, 255));
          }catch (Exception e){
              Log.d("Album_art","False path");
              color = Color.argb(255, 29, 202, 255);
          }
          try{
              bitmap.recycle();
              bitmap =null;



          }catch (Exception e){

          }





      } else {

          color = Color.argb(255, 29, 202, 255);
      }
  }
}
