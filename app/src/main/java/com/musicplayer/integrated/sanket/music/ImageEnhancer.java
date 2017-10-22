package com.musicplayer.integrated.sanket.music;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;

import android.os.Debug;
import android.util.Log;

import java.text.DecimalFormat;



/**
 * Created by Sanket on 29-06-2017.
 */

public class ImageEnhancer {




    public static Bitmap getConvertedImage(String filename , int size){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(filename,options);
        options.inSampleSize=getPreferredSize(options.outHeight,options.outWidth,size);
        options.inJustDecodeBounds=false;
        logHeap();
        return   BitmapFactory.decodeFile(filename,options);
    }

    public static  int getPreferredSize(int h , int w , int s){
        int size = 1;

        while(h/size>s || w/size>s)
            size*=2;

        return  size;
    }

    public static Bitmap getAdjustedOpacity(Bitmap bitmap , int opacity ){
        logHeap();

        return adjustOpacity(bitmap,opacity);
    }


    private static Bitmap adjustOpacity(Bitmap bitmap, int opacity)
    {
        Bitmap mutableBitmap = bitmap.isMutable()
                ? bitmap
                : bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        int colour = (opacity & 0xFF) << 24;
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    public static void logHeap() {
        Double allocated = new Double(Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("tag", "debug. =================================");
        Log.d("tag", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        Log.d("tag", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }




}
