package com.musicplayer.integrated.sanket.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;


public class ActivitySettings extends AppCompatActivity {

private CheckBox p , a, v;
    private TextView settings;
  private  SharedPreferences shared;
    private    SharedPreferences.Editor editor;
    private CloseApp closeApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setup();
        closeApp = new CloseApp();
        IntentFilter intentF = new IntentFilter("close");
        registerReceiver(closeApp,intentF);
        setContentView(R.layout.activity_settings);
        p = (CheckBox)findViewById(R.id.proximity_check);
        a = (CheckBox)findViewById(R.id.acc_check);
        v = (CheckBox)findViewById(R.id.visualiser);
         
        settings = (TextView)findViewById(R.id.textView25);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       shared = getSharedPreferences("ActivitySettings",MODE_PRIVATE);
       editor = shared.edit();

        p.setChecked(shared.getBoolean("Pro",true));
        a.setChecked(shared.getBoolean("Acc",true));
        v.setChecked(shared.getBoolean("Vis",true));


    }
    private void setup(){
        if(MusicPlayback.allTracks==null){
            finish();
        }
    }
    public  void onClickItems(View v){
        boolean isChecked;
        switch (v.getId()){

            case R.id.proximity_check :
                    isChecked = p.isChecked();
                editor.putBoolean("Pro",isChecked).commit();
                Log.d("Pro",""+isChecked);
                                        break;

            case  R.id.acc_check :
                    isChecked = a.isChecked();
                    if(isChecked) MusicPlayback.shake.start();
                    else MusicPlayback.shake.stop();
                    editor.putBoolean("Acc",isChecked).commit();
                    Log.d("Acc",""+isChecked);
                                    break;
            case R.id.visualiser :
                                  isChecked = this.v.isChecked();
                                  editor.putBoolean("Vis",isChecked).commit();
                break;

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
