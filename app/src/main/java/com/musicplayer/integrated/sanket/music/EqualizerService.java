package com.musicplayer.integrated.sanket.music;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.Virtualizer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Sanket on 23-07-2017.
 */

public class EqualizerService extends Service{
    public static Equalizer eq;
    public static BassBoost bb;
    public  static Virtualizer vi;
    public static LoudnessEnhancer le;
    public static  boolean isEq , isBb , isVi , isLe;
    SharedPreferences preferences;

   public void  setup(){
    preferences = getSharedPreferences("EqualizerSettings",MODE_PRIVATE);
    int minValue = 0 ,maxValue =0 , newValue=0;
       if(eq == null){
           eq = new Equalizer(0,0);}

       if(bb == null){
           bb = new BassBoost(0,0);}
       if(vi == null){
           vi = new Virtualizer(0,0);}
       if(le == null){
           le = new LoudnessEnhancer(0);}

       try {  isEq = preferences.getBoolean("eq",true);
           Log.d("isEq",""+isEq);
           isBb = preferences.getBoolean("ba",false);
           Log.d("isEq",""+isBb);
           isVi= preferences.getBoolean("vi",false);
           Log.d("isEq",""+isVi);
           isLe = preferences.getBoolean("lo",false);
           Log.d("isEq",""+isLe);
           eq.setEnabled(isEq);
           bb.setEnabled(isBb);
           vi.setEnabled(isVi);
           le.setEnabled(isLe);

           short range[] = eq.getBandLevelRange();
            minValue = range[0];
           maxValue = range[1];
           Log.d("factor"," "+minValue + " " +maxValue);

           if(isBb){
               bb.setStrength((short)1000);
           }
           if(isVi){
               vi.setStrength((short)1000);
           }
           if(isLe){
               le.setTargetGain(1000);
           }

           newValue = minValue + (maxValue - minValue)*preferences.getInt("s1",50)/100;
           Log.d("S1",""+newValue);
           eq.setBandLevel((short)0,(short)(newValue));

           newValue = minValue + (maxValue - minValue)*preferences.getInt("s2",50)/100;
           Log.d("S2",""+newValue);
           eq.setBandLevel((short)1,(short)(newValue));

           newValue = minValue + (maxValue - minValue)*preferences.getInt("s3",50)/100;
           Log.d("S3",""+newValue);
           eq.setBandLevel((short)2,(short)(newValue));

           newValue = minValue + (maxValue - minValue)*preferences.getInt("s4",50)/100;
           Log.d("S4",""+newValue);
           eq.setBandLevel((short)3,(short)(newValue));

           newValue = minValue + (maxValue - minValue)*preferences.getInt("s5",50)/100;
           Log.d("S5",""+newValue);
           eq.setBandLevel((short)4,(short)(newValue));


       }catch (Exception e){

       }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
          setup();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
