package com.musicplayer.integrated.sanket.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sanket on 19-07-2017.
 */

public class FragmentPlaylist extends Fragment {
    private RecyclerView playList;
    private TextView playList_create;
    private CustomPlaylistAdapter adapter;
    private TextView soft, hard;
    private int pos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist,container, false);


        adapter = new CustomPlaylistAdapter(getContext());
        playList = (RecyclerView) root.findViewById(R.id.recylcerView_playList);
        playList.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        playList.setLayoutManager(layoutManager);
        //relativeLayout = (RelativeLayout)root.findViewById(R.id.playlist_fragment_container);
        soft = (TextView)root.findViewById(R.id.playlist_soft_ripple);
        hard = (TextView)root.findViewById(R.id.playlist_hard_ripple);
        soft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            openList(0);

            }
        });
        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            openList(1);
            }
        });
        playList.setFocusable(false);


        playList_create = (TextView)root.findViewById(R.id.textView_create_list);
        playList_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final PlayListDatabase database = new PlayListDatabase(getContext());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final View view = getActivity().getLayoutInflater().inflate(R.layout.create_playlist_dialog,null);
                final EditText name = (EditText)view.findViewById(R.id.editText_playlist_dialog);
                TextView create = (TextView)view.findViewById(R.id.textView_playlist_create);
                final TextView cancel = (TextView)view.findViewById(R.id.textView_playlist_cancel);
                builder.setView(view);
                final AlertDialog dialog =builder.create();
                dialog.show();
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getWindow().setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        dialog.dismiss();
                    }
                });
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean found = false;
                        if(!name.getText().toString().isEmpty()){
                            boolean result = true;
                            found = name.getText().toString().matches("[A-Za-z_]+");
                            if(!found){
                                Toast.makeText(getContext(),"Playlist name should contain only alphabets and underscore!",Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for(String s : MusicPlayback.playlist){
                                if(s.equals(name.getText().toString())){
                                    result = false;
                                    break;
                                }
                            }

                            if(result){
                                SongListDatabase.VER++;
                                ActivityMainPlayer.editor.putInt("ver",SongListDatabase.VER).apply();
                                Log.d("Ver",""+SongListDatabase.VER);

                                SongListDatabase.table =    name.getText().toString();
                               new SongListDatabase(getContext(),SongListDatabase.table);

                             long check = database.createPlaylist(name.getText().toString());
                            if(check ==-1 ){
                                Toast.makeText(getContext(), "Error!",Toast.LENGTH_SHORT).show();
                            }
                            else {

                                ArrayList<String> temp;
                                temp =new PlayListDatabase(getContext()).getPlaylist();
                                MusicPlayback.playlist = temp;
                                MusicPlayback.smallPlaylist = null;
                                MusicPlayback.smallPlaylist = new ArrayList<String>();
                                for(int i = 0 ; i < MusicPlayback.playlist.size();i++){
                                    if(i<2)continue;
                                    MusicPlayback.smallPlaylist.add(MusicPlayback.playlist.get(i));
                                }

                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                getActivity().getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                            }
                            }
                            else{
                                if(found){
                                    Toast.makeText(getContext(),name.getText().toString() +" already exists!",Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            }

                        }
                        else{
                          Toast.makeText(getContext(),"Empty Field!",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });
        return root;
    }



    private void openList(int pos){
        MusicPlayback.songDatabaseSet = null;
        SongListDatabase.table =MusicPlayback.playlist.get(pos);
        SongListDatabase d = new SongListDatabase(getContext(),SongListDatabase.table);
        MusicPlayback.songDatabaseSet = d.getPlaylistSongs();
        d.close();
        Intent intent = new Intent(getContext() , ActivityPlaylistDepth.class);
        intent.putExtra("Title",MusicPlayback.playlist.get(pos));
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.left_enter_translate,R.anim.right_enter_translate);
    }
    private class CustomPlaylistAdapter extends RecyclerView.Adapter<CustomPlaylistAdapter.ViewHolder>{
    LayoutInflater layoutInflater;


        CustomPlaylistAdapter(Context context){
            layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View v = layoutInflater.inflate(R.layout.adapter_playlist,parent,false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {


               holder.playlist_name.setText(MusicPlayback.smallPlaylist.get(position));

        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            holder.itemView.setOnLongClickListener(null);
            super.onViewRecycled(holder);
        }

        @Override
        public int getItemCount() {

               return MusicPlayback.smallPlaylist.size();


        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {
            public  TextView playlist_name;



            public ViewHolder(View itemView) {
                super(itemView);
                playlist_name = (TextView)itemView.findViewById(R.id.textView_playlist_adapter_name);
                playlist_name.setOnClickListener(this);
                playlist_name.setOnLongClickListener(this);



            }

            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext() , "Clicked " +getLayoutPosition(),Toast.LENGTH_SHORT).show();
                pos = getLayoutPosition();
                openList(pos+2);

            }


            @Override
            public boolean onLongClick(View v) {
                pos = getLayoutPosition();
                Log.d("FP",""+pos);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getActivity().getLayoutInflater().inflate(R.layout.playlist_options_dialog,null);
                TextView delete = (TextView)view.findViewById(R.id.textView_playlist_options_delete);
                TextView rename = (TextView)view.findViewById(R.id.textView_playlist_options_rename);
                builder.setView(view);
                final AlertDialog dialog =builder.create();
                dialog.show();
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new PlayListDatabase(getContext()).deletePlaylist(MusicPlayback.playlist.get(pos+2));
                        SongListDatabase.table = MusicPlayback.playlist.get(pos+2);
                        new SongListDatabase(getContext(),SongListDatabase.table).deleteSongListDatabase();
                        MusicPlayback.playlist.remove(pos+2);
                        MusicPlayback.smallPlaylist = new ArrayList<>();
                        for(int i = 0 ; i < MusicPlayback.playlist.size();i++){
                            if(i<2)continue;
                            MusicPlayback.smallPlaylist.add(MusicPlayback.playlist.get(i));
                        }

                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        pos =-1;
                    }
                });
                rename.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        AlertDialog.Builder builderi = new AlertDialog.Builder(getContext());
                        View view = FragmentPlaylist.this.getActivity().getLayoutInflater().inflate(R.layout.playlist_rename_dialog, null);
                        final EditText editText = (EditText) view.findViewById(R.id.editText_playlist_rename_edit);
                        TextView cancel = (TextView) view.findViewById(R.id.textView_playlist_rename_cancel);
                        TextView renamei = (TextView) view.findViewById(R.id.textView_playlist_rename_rename);
                        builderi.setView(view);
                        final AlertDialog dialogi = builderi.create();
                        dialogi.show();
                        InputMethodManager inputMethodManager =
                                (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                        renamei.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (editText.getText().toString().isEmpty()) {
                                    Toast.makeText(getContext(), "Enter the name", Toast.LENGTH_SHORT).show();
                                } else if (editText.getText().toString().matches("[A-Za-z_]+")) {
                                    boolean result = true;
                                    Iterator it = MusicPlayback.playlist.iterator();
                                    while (it.hasNext()) {
                                        if (((String) it.next()).equals(editText.getText().toString())) {
                                            result = false;
                                            break;
                                        }
                                    }
                                    if (result) {
                                        int c = new PlayListDatabase(getContext()).renamePlaylist((String) MusicPlayback.playlist.get(FragmentPlaylist.this.pos + 2), editText.getText().toString());
                                        SongListDatabase.table =  MusicPlayback.playlist.get(FragmentPlaylist.this.pos + 2);
                                        int c2 = new SongListDatabase(getContext(), SongListDatabase.table).renamePlaylistName(editText.getText().toString());
                                        if (c == 1 && c2 == 1) {
                                            Toast.makeText(FragmentPlaylist.this.getContext(), "Renamed", Toast.LENGTH_SHORT).show();
                                            MusicPlayback.playlist.remove(FragmentPlaylist.this.pos + 2);
                                            MusicPlayback.playlist.add(FragmentPlaylist.this.pos + 2, editText.getText().toString());
                                            MusicPlayback.smallPlaylist = null;
                                            MusicPlayback.smallPlaylist = new ArrayList();
                                            for (int i = 0; i < MusicPlayback.playlist.size(); i++) {
                                                if (i >= 2) {
                                                    MusicPlayback.smallPlaylist.add(MusicPlayback.playlist.get(i));
                                                }
                                            }
                                            FragmentPlaylist.this.adapter.notifyDataSetChanged();
                                            dialogi.dismiss();
                                            getActivity().getWindow().setSoftInputMode(
                                                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                            return;
                                        }
                                        return;
                                    }
                                    Toast.makeText(FragmentPlaylist.this.getContext(), editText.getText().toString() + " already exists ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(FragmentPlaylist.this.getContext(), "Playlist name should contain only alphabets and underscore!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                getActivity().getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                dialogi.dismiss();
                            }
                        });
                    }
                });


                return true;
            }
        }



    }


}
