package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Sanket on 21-07-2017.
 */

public class MusicIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    Log.d("Start",""+MusicPlayback.start);
                    if(MusicPlayback.getSongPosition() !=-1){
                        if(MusicPlayback.getPlayingStatus() && MusicPlayback.start !=0){

                            MusicPlayback.pauseMediaPlayback();
                        }

                    }
                    if(MusicPlayback.start==0){
                                 MusicPlayback.start=1;
                             }


                    Log.d("Headset", "Headset is unplugged");
                    break;
                case 1:
                    if(MusicPlayback.getSongPosition()!=-1){
                        if(!MusicPlayback.getPlayingStatus()){
                            MusicPlayback.resumeMediaPlayback();
                        }
                    }
                    if(MusicPlayback.start==0){
                        MusicPlayback.start=1;
                    }


                    Log.d("Headset", "Headset is plugged");
                    break;

            }
        }
    }
}
