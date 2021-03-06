package cn.dmrf.nuaa.gamebird.Bird;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.HashMap;

import cn.dmrf.nuaa.gamebird.R;

public class SoundPlayer {

    private MediaPlayer mediaPlayer;
    private float speed = 1.f;

    @SuppressLint("UseSparseArrays")
    public SoundPlayer(Context context) {
       mediaPlayer= MediaPlayer.create(context, R.raw.bacmic);

    }

    public float getSpeed() {
        return speed;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playSound() {

       // mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
    }

    public void stopSound(){
        mediaPlayer.stop();
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
