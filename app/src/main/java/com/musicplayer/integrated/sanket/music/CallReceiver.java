package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

    TelephonyManager telManager;
    Context context;


    @Override
    public void onReceive(Context context, Intent intent) {


        this.context=context;

        telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        if(MusicPlayback.getPlayingStatus()){
                            MusicPlayback.pauseMediaPlayback();
                        }
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {

                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        if(!MusicPlayback.getPlayingStatus() && MusicPlayback.getSongPosition() != -1 ){
                            MusicPlayback.resumeMediaPlayback();
                        }

                        break;
                    }

                }
            } catch (Exception ex) {

            }
        }
    };
}
