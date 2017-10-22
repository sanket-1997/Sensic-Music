package com.musicplayer.integrated.sanket.music;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * Created by Sanket on 13-07-2017.
 */

public class Proximity implements  SensorEventListener{

private static SensorManager sensorManager;
private static Sensor sensor;
private static int count=0;
private static boolean start = false;

private static Context context;

    public  Proximity(Context context) {
        this.context = context;
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

    }
    public  void  start(){
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);

    }
    public void stop(){
        sensorManager.unregisterListener(this);
        start = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Proximity",""+count);
        if(MusicPlayback.getSongPosition()!=-1){
        context.sendBroadcast(new Intent("myaction"));}
     if(MusicPlayback.getSongPosition() !=-1 && start) {
        if(MusicPlayback.getPlayingStatus() &&count==0 ){
            MusicPlayback.pauseMediaPlayback();

        }
        else if(!MusicPlayback.getPlayingStatus() &&count==0){
            MusicPlayback.resumeMediaPlayback();

        }
        count++;

     }
     if(count==2)count=0;
        start = true;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
