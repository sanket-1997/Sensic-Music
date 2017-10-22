package com.musicplayer.integrated.sanket.music;

/**
 * Created by Sanket on 30-06-2017.
 */


public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.musicplayer.integrated.sanket.music.action.main";
        public static String INIT_ACTION = "com.musicplayer.integrated.sanket.music.action.init";
        public static String PREV_ACTION = "com.musicplayer.integrated.sanket.music.action.prev";
        public static String PLAY_ACTION = "com.musicplayer.integrated.sanket.music.action.play";
        public static String NEXT_ACTION = "com.musicplayer.integrated.sanket.music.action.next";
        public static String STARTFOREGROUND_ACTION = "com.musicplayer.integrated.sanket.music.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.musicplayer.integrated.sanket.music.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }


}
