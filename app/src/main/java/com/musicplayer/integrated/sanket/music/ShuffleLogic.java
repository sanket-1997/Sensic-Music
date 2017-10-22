package com.musicplayer.integrated.sanket.music;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sanket on 29-06-2017.
 */

public class ShuffleLogic {
    private int size;
    private ArrayList<Integer> shuffleOrder;
    public ShuffleLogic(int size){
        this.size = size;
        shuffleOrder = new ArrayList<>();
    }

    public ShuffleLogic setupInOrder(){
        for(int  i = 0 ;i<size ; i++)
            shuffleOrder.add(i);

       return this;
    }
    public ShuffleLogic setArray(ArrayList<Integer> s){
        shuffleOrder = s;
        Log.e("shuffle order ","" +shuffleOrder.size());
        return this;
    }
    public  ShuffleLogic shuffle(){
        for(int i = 0 ; i <size ;i++){

            int random = i + (int)(Math.random()*(size-i));
            int temp = shuffleOrder.get(random);
            shuffleOrder.set(random , shuffleOrder.get(i));
            shuffleOrder.set(i,temp);

        }

        return  this;

    }
    public  ArrayList<Integer> getShuffleOrder(){return  shuffleOrder;}


}
