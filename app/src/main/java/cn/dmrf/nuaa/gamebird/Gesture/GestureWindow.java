package cn.dmrf.nuaa.gamebird.Gesture;

import android.util.Log;

import java.util.ArrayList;

public class GestureWindow {
    private float window_width;
    private ArrayList<Double> window_value;
    private int window_len;
    private int window_size;

    public GestureWindow(float window_width) {
        this.window_width = window_width;
        window_value=new ArrayList<>();
        window_len=0;
        window_size=(int) (window_width*110);
    }


    public boolean Judge(String cur_val){
        double cur_dis = Double.valueOf(cur_val);
        Log.e("gesture_judge",window_len+"");

        if (window_len==window_size){
            window_value.remove(0);
            window_value.add(cur_dis);
            if (window_value.get(window_len)-window_value.get(0)<-2){
             //   window_value.clear();
               // window_len=0;
                return true;
            }else {
                return false;
            }
        }


        if (window_len<window_size-1){
            window_value.add(cur_dis);
            window_len++;
            if (cur_dis-window_value.get(0)<-2){
                return true;
            }else {
                return false;
            }

        }
    return false;

    }

}
