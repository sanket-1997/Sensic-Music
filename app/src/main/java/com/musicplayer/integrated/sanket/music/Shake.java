package com.musicplayer.integrated.sanket.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.net.Uri;

import com.squareup.seismic.ShakeDetector;



/**
 * Created by Sanket on 13-07-2017.
 */

public class Shake implements ShakeDetector.Listener {
    private SensorManager sensorManager;
    private ShakeDetector shakeDetector;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public Shake(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        shakeDetector = new ShakeDetector(this);
        this.context = context;
        shakeDetector.setSensitivity(ShakeDetector.SENSITIVITY_MEDIUM);
    }

    public void start() {
        shakeDetector.start(sensorManager);
    }

    public void stop() {
        shakeDetector.stop();
    }

    @Override
    public void hearShake() {
        int temp;

        if (sharedPreferences == null)
            sharedPreferences = context.getSharedPreferences("MusicData", Context.MODE_PRIVATE);
        if (editor == null) editor = sharedPreferences.edit();
        if (MusicPlayback.getSongPosition() == -1) {
            Intent intent = new Intent(context, MusicPlayback.class);
            intent.putExtra("Song Position", ActivityMainPlayer.lastSongPlayed);
            temp = MusicPlayback.songPosition = ActivityMainPlayer.lastSongPlayed;
            MusicPlayback.cursor = temp;

            context.startService(intent);


            editor.putInt("music", temp).apply();

            } else {
                temp = MusicPlayback.getPlayNextMediaIndex();
                MusicPlayback.startMediaPlayback(temp);

                editor.putInt("music", temp).apply();

            }

        context.sendBroadcast(new Intent("myaction"));


        }
    }

