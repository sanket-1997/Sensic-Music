package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class ActivityEqualizer extends AppCompatActivity  {

    private SeekBar seekBar1,seekBar2,seekBar3,seekBar4,seekBar5;
    private SwitchCompat eq ;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private int minLevel =0  ,maxLevel=100;
    private CloseApp closeApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
        setup();
        setContentView(R.layout.activity_equalizer);
        short[] range = EqualizerService.eq.getBandLevelRange();
        minLevel = range[0];
        maxLevel = range[1];
        Log.d("Range0",""+range[0]);
        Log.d("Range1",""+range[1]);
        seekBar1 = (SeekBar)findViewById(R.id.seekBar_basic_seek1);
        seekBar2 = (SeekBar)findViewById(R.id.seekBar_basic_seek2);
        seekBar3 = (SeekBar)findViewById(R.id.seekBar_basic_seek3);
        seekBar4 = (SeekBar)findViewById(R.id.seekBar_basic_seek4);
        seekBar5 = (SeekBar)findViewById(R.id.seekBar_basic_seek5);
        eq = (SwitchCompat)findViewById(R.id.switch_equalizer_start);
        sharedPreferences = getSharedPreferences("EqualizerSettings",MODE_PRIVATE);
        editor  = sharedPreferences.edit();

        seekBar1.setProgress(sharedPreferences.getInt("s1",50));
        seekBar2.setProgress(sharedPreferences.getInt("s2",50));
        seekBar3.setProgress(sharedPreferences.getInt("s3",50));
        seekBar4.setProgress(sharedPreferences.getInt("s4",50));
        seekBar5.setProgress(sharedPreferences.getInt("s5",50));
        eq.setChecked(sharedPreferences.getBoolean("eq",false));



        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int new_level = minLevel + (maxLevel - minLevel)*progress/100;
                    editor.putInt("s1",progress).commit();
                    EqualizerService.eq.setBandLevel((short)0,(short)new_level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int new_level = minLevel + (maxLevel - minLevel)*progress/100;
                editor.putInt("s2",progress).commit();
                EqualizerService.eq.setBandLevel((short)1,(short)new_level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int new_level = minLevel + (maxLevel - minLevel)*progress/100;
                editor.putInt("s3",progress).commit();
                EqualizerService.eq.setBandLevel((short)2,(short)new_level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBar4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int new_level = minLevel + (maxLevel - minLevel)*progress/100;
                Log.d("new_level",""+new_level);
                editor.putInt("s4",progress).commit();
                EqualizerService.eq.setBandLevel((short)3,(short)new_level);
                Log.d("Eq",""+EqualizerService.eq.getBandLevel((short)3));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        seekBar5.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int new_level = minLevel + (maxLevel - minLevel)*progress/100;
                editor.putInt("s5",progress).commit();
                EqualizerService.eq.setBandLevel((short)4,(short)new_level);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        eq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EqualizerService.isEq = isChecked;
                EqualizerService.eq.setEnabled(EqualizerService.isEq);
                editor.putBoolean("eq",isChecked).commit();
            }
        });



    }



    public void defaultButton(View v){
        seekBar1.setProgress(50);
        seekBar2.setProgress(50);
        seekBar3.setProgress(50);
        seekBar4.setProgress(50);
        seekBar5.setProgress(50);
        editor.putInt("s1",50).apply();
        EqualizerService.eq.setBandLevel((short)0,(short)50);
        editor.putInt("s2",50).apply();
        EqualizerService.eq.setBandLevel((short)1,(short)50);
        editor.putInt("s3",50).apply();
        EqualizerService.eq.setBandLevel((short)2,(short)50);
        editor.putInt("s4",50).apply();
        EqualizerService.eq.setBandLevel((short)3,(short)50);
        editor.putInt("s5",50).apply();
        EqualizerService.eq.setBandLevel((short)4,(short)50);

    }

    private void setup(){
        if(MusicPlayback.allTracks==null){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_exit_translate,R.anim.right_exit_translate);
    }
    private class CloseApp extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            finish();


        }
    }

}
