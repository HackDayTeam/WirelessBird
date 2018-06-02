package cn.dmrf.nuaa.gamebird.Bird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.IOException;

import cn.dmrf.nuaa.gamebird.Gesture.GestureUtil;
import cn.dmrf.nuaa.gamebird.R;

public class GameBirdActivity extends Activity {
    public static GameBirdActivity instance;
    private LinearLayout gameView;
    private GestureUtil gestureUtil;
    public static double predis1 = 0.0;
    public static double predis2 = 0.0;
    private GameBirdSurfaceView gameBirdSurfaceView;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        //设置圆环角度
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (msg.obj.toString().equals("start")) {
                        gestureUtil.Play();
                    } else if (msg.obj.toString().equals("stop")) {
                        gestureUtil.Stop();
                    }
                    break;
                case 1:
                    updatestate(msg.obj.toString(), 0);
                    Log.e("gesture_ids",msg.obj.toString());
                    //msg.obj.toString()
                    break;
                case 2:
                    updatestate(msg.obj.toString(), 1);
                   // Log.i("gesture_ids",msg.obj.toString());
                    //msg.obj.toString()
                    break;
            }
        }
    };


    private void updatestate(String dis, int which) {
        double now_dis = Double.valueOf(dis);

        switch (which) {
            case 0://L
                if (now_dis - predis1 > 0) {
                    gameBirdSurfaceView.down();
                } else if (now_dis - predis1 < 0) {
                    gameBirdSurfaceView.up();
                }
                predis1 = now_dis;

                break;
            case 1://R
                if (now_dis - predis2 > 0) {
                    gameBirdSurfaceView.down();
                } else if (now_dis - predis2 < 0) {
                    gameBirdSurfaceView.up();
                }
                predis2 = now_dis;
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        instance = this;

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.content_view);
        gestureUtil = new GestureUtil(mHandler);

        //game
        gameView = (LinearLayout) this.findViewById(R.id.game_view);
        gameBirdSurfaceView = new GameBirdSurfaceView(this, mHandler);
        gameView.addView(gameBirdSurfaceView);
    }

    public void showMessage(int level) {

        saveSettingData(level);

        Intent intent = new Intent(this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

    }

    public static final String GameBirdSettingsFile = "GameBird_Settings";
    public static final String Settings_LevelLast = "LevelLast";
    public static final String Settings_LevelTop = "LevelTop";

    private void saveSettingData(int level) {

        SharedPreferences gb_settings = getSharedPreferences(
                GameBirdSettingsFile, 0);

        gb_settings.edit().putInt(Settings_LevelLast, level).commit();

        int top = gb_settings.getInt(Settings_LevelTop, 0);

        if (level > top) {
            gb_settings.edit().putInt(Settings_LevelTop, level).commit();
        }

    }
}