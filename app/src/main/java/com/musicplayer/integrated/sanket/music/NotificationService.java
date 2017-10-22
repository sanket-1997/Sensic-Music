package com.musicplayer.integrated.sanket.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.blurry.Blurry;

public class NotificationService extends Service {


private  RemoteViews views;
private     RemoteViews bigViews;
    private  Notification status;
private  PendingIntent pendingIntent;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();


        // Using RemoteViews to bind custom layouts into Notification
        views = new RemoteViews(getPackageName(), R.layout.custom_notification_small);
        bigViews = new RemoteViews(getPackageName(), R.layout.custom_notification_big);
       set();
        Log.d("Noti","Negative");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
           setup(intent);
            return START_STICKY;
        }
private  void  setup(Intent intent){
      if(intent!=null) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();
            Log.d("Noti","Positive");

        }
        if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
            int save = MusicPlayback.getPlayPrevMediaIndex();
            MusicPlayback.startMediaPlayback(save);
            updateMedia();


//                Log.i(LOG_TAG, "Clicked Previous");
        }
        if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {

            try{
                if (MusicPlayback.getPlayingStatus()) {
                    MusicPlayback.pauseMediaPlayback();
                }
                else
                {
                    if (MusicPlayback.getPrepared()) {
                        MusicPlayback.resumeMediaPlayback();
                    }
                    else
                    {   stopService(new Intent(getApplicationContext(), MusicPlayback.class));
                        stopService(new Intent(getApplicationContext(),EqualizerService.class));
                        stopForeground(true);
                        stopSelf();
                        Process.killProcess(Process.myPid());

                    }

                }

                updateMedia();


            }catch (Exception e){
                stopService(new Intent(getApplicationContext(), MusicPlayback.class));
                stopService(new Intent(getApplicationContext(),EqualizerService.class));
                stopForeground(true);
                stopSelf();
               sendBroadcast(new Intent("close"));

            }

            Log.i(LOG_TAG, "Clicked Play");
        }
        if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
            int save = MusicPlayback.getPlayNextMediaIndex();
            MusicPlayback.startMediaPlayback(save);
            updateMedia();


            Log.i(LOG_TAG, "Clicked Next");
        }
        if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            MusicPlayback.stopMediaPlayback();
            stopService(new Intent(getApplicationContext(), MusicPlayback.class));
            stopService(new Intent(getApplicationContext(),EqualizerService.class));
            stopForeground(true);
            stopSelf();
            Process.killProcess(Process.myPid());



        }
    }
}

    private final String LOG_TAG = "NotificationService";

    private void set(){
        Intent notificationIntent = new Intent(this, ActivityMainFullPlayer.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, NotificationService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, NotificationService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.imageView_notification_play_small, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.imageView_notification_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.imageView_notification_next_small, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.imageView_notification_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.imageView_notification_prev_small, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.imageView_notification_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.imageView_notification_close_small, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.imageView_notification_close, pcloseIntent);



        status = new Notification.Builder(this).setPriority(Notification.PRIORITY_HIGH).setOngoing(true)
                .build();
    }

    private void showNotification() {

        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.small_music_notification;
        status.contentIntent = pendingIntent;
        updateMedia();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }


    public void updateMedia()
    {

        bigViews.setImageViewResource(R.id.imageView_notification_close,R.drawable.notification_close);
        bigViews.setImageViewResource(R.id.imageView_notification_prev,R.drawable.notification_prev);
        bigViews.setImageViewResource(R.id.imageView_notification_next,R.drawable.notification_next);
         views.setImageViewResource(R.id.imageView_notification_close_small,R.drawable.notification_close);
        views.setImageViewResource(R.id.imageView_notification_prev_small,R.drawable.notification_prev);
       views.setImageViewResource(R.id.imageView_notification_next_small,R.drawable.notification_next);

        //Set Album Art
        sendBroadcast(new Intent("myaction"));

        if (MusicPlayback.allTracks.get(MusicPlayback.songPosition).getAlbumArt() != null) {
            views.setImageViewUri(R.id.imageView_notification_album_art_small, Uri.parse(MusicPlayback.allTracks.get(MusicPlayback.songPosition).getAlbumArt()));
            bigViews.setImageViewUri(R.id.imageView_notification_album_art, Uri.parse(MusicPlayback.allTracks.get(MusicPlayback.songPosition).getAlbumArt()));


         }
            else {
            views.setImageViewResource(R.id.imageView_notification_album_art_small,R.drawable.default_album_art_track);
            bigViews.setImageViewResource(R.id.imageView_notification_album_art,R.drawable.default_album_art_track);
        }

        if(MusicPlayback.getPlayingStatus()) {
            views.setImageViewResource(R.id.imageView_notification_play_small, R.drawable.notification_pause);
            bigViews.setImageViewResource(R.id.imageView_notification_play, R.drawable.notification_pause);

        }
        else {
            views.setImageViewResource(R.id.imageView_notification_play_small, R.drawable.notification_play);
            bigViews.setImageViewResource(R.id.imageView_notification_play, R.drawable.notification_play);

        }


            views.setTextViewText(R.id.textView_notification_song_name_small, MusicPlayback.allTracks.get(MusicPlayback.songPosition).getTitle());
            bigViews.setTextViewText(R.id.textView_notification_song_name, MusicPlayback.allTracks.get(MusicPlayback.songPosition).getTitle());
        bigViews.setTextViewText(R.id.textView_notification_artist_name, MusicPlayback.allTracks.get(MusicPlayback.songPosition).getArtist());

    }


}
