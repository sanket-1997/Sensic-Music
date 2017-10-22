package com.musicplayer.integrated.sanket.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Sanket on 12-07-2017.
 *
 */

public class FragmentAlbum extends Fragment{

 private GridView album;

    private CustomAlbumAdapter adapter;

    private ModelAlbum albumModel;
    private   int layoutPos;
    AlertDialog dialogReference;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album,container, false);
        album = (GridView)root.findViewById(R.id.albumGrid);
        album.setSmoothScrollbarEnabled(true);
        adapter = new CustomAlbumAdapter(MusicPlayback.albums);
        album.setAdapter(adapter);

        album.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutPos =position;
                albumModel =(ModelAlbum) adapter.getItem(position);
                Intent intent = new Intent(getContext(),ActivityAlbumFullList.class);
                intent.putExtra("Album Name",albumModel.getAlbumName());
                intent.putExtra("Int",layoutPos);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);


            }
        });
        album.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                layoutPos = position;
                final AlertDialog.Builder builder  = new AlertDialog.Builder(getContext());
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.manage_to_playlist_dialog,null);
                TextView add = (TextView)view1.findViewById(R.id.textView_manage_to_playlist_add_to_list);
                builder.setView(view1);
                final AlertDialog dialog = builder.create();
                dialog.show();
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                        View view2 = getActivity().getLayoutInflater().inflate(R.layout.manage_to_list_add_dialog,null);
                        ListView listView = (ListView)view2.findViewById(R.id.listView_list_all_playlist);
                        listView.setAdapter(new CustomListAdapter());
                        builder1.setView(view2);
                        AlertDialog dialog1 = builder1.create();
                        dialogReference = dialog1;
                        dialog1.show();

                    }
                });

                return true;
            }
        });





        return root;
    }

    private class CustomAlbumAdapter extends BaseAdapter{

        private ArrayList<ModelAlbum> albumModels;


        public CustomAlbumAdapter(ArrayList<ModelAlbum> albumModels){
            this.albumModels = albumModels;
            Log.e("album Model size",""+albumModels.size());
        }



        @Override
        public int getCount() {
            return this.albumModels.size();
        }

        @Override
        public Object getItem(int position) {
            return albumModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_album,parent,false);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);

        }
        else {
            holder = (MyViewHolder)convertView.getTag();


        }



            if(albumModels.get(position).getAlbumArt() == null){
                Glide.with(getContext()).asBitmap().load(R.drawable.default_album_art_track).into(holder.albumCover);

            }
            else{
                Glide.with(getContext()).asBitmap().load(albumModels.get(position).getAlbumArt()).into(holder.albumCover);

            }
             holder.albumName.setText(albumModels.get(position).getAlbumName());



            return  convertView;
        }
    }


    private class MyViewHolder{
      ImageView albumCover;
        TextView albumName;

       public  MyViewHolder(View v){
            albumCover =(ImageView)v.findViewById(R.id.imageView_display_album_album_art);
           albumName = (TextView)v.findViewById(R.id.textView_display_album_album_name);
        }

    }

    private class  CustomListAdapter extends BaseAdapter{

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

                    SongListDatabase.table = MusicPlayback.playlist.get(position+2);
                    ArrayList<Integer> songPos = new ArrayList<Integer>();
                    ////// Search and add pos in arraylist
                    for(int i =0 ; i < MusicPlayback.albums.size();i++ ){
                        if(MusicPlayback.albums.get(layoutPos).getAlbumName().equals(MusicPlayback.allTracks.get(i).getAlbum())){
                            songPos.add(i);
                        }
                    }
                    SongListDatabase songListDatabase = new SongListDatabase(getContext(),SongListDatabase.table);
                    for(int i = 0 ; i<songPos.size();i++){
                        songListDatabase.enterSongPos( MusicPlayback.allTracks.get(songPos.get(i)).getSongID());
                    }
                    dialogReference.dismiss();
                }
            });
            return  convertView;
        }
    }



}
