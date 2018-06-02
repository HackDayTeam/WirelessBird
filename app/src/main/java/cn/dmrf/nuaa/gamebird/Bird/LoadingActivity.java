package cn.dmrf.nuaa.gamebird.Bird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;


import cn.dmrf.nuaa.gamebird.Gesture.GestureUtil;
import cn.dmrf.nuaa.gamebird.Gesture.VerifyPermission;
import cn.dmrf.nuaa.gamebird.R;

public class LoadingActivity extends Activity {

    private GestureUtil gestureUtil;
    private Button btn_play;
    private Button btn_stop;
    private TextView tv1;
    private TextView tv2;



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        //设置圆环角度
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv1.setText(msg.obj.toString());
                    break;
                case 2:
                    tv2.setText(msg.obj.toString());
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            VerifyPermission verifyPermission = new VerifyPermission(LoadingActivity.this);
            verifyPermission.RequestPermission();
        }


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //setContentView(R.layout.loading);
        setContentView(R.layout.gesture_test_layout);
        GestureTest();
/*
        final TextView gameStart = (TextView) findViewById(R.id.game_start);

        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setAdListener(new AdListener() {

            @Override
            public void onReceiveAd(Ad arg0) {
                gameStart.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPresentScreen(Ad arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onLeaveApplication(Ad arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDismissScreen(Ad arg0) {
                // TODO Auto-generated method stub

            }
        });

        View gameMessage = findViewById(R.id.GameMessage);

        int[] data = getSettingData();

        TextView levelMessage = (TextView) findViewById(R.id.level_Message);

        levelMessage.setText("SCORE: " + data[0] + "\nBEST: " + data[1]);

        gameMessage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(LoadingActivity.this, GameBirdActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(intent);

                finish();
            }
        });*/

    }


    private int[] getSettingData() {

        SharedPreferences gb_settings = getSharedPreferences(GameBirdActivity.GameBirdSettingsFile, 0);

        int last = gb_settings.getInt(GameBirdActivity.Settings_LevelLast, 0);
        int top = gb_settings.getInt(GameBirdActivity.Settings_LevelTop, 0);

        return new int[]{last, top};
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            try {
                GameBirdActivity.instance.finish();
            } catch (Exception e) {
            }
            finish();
            System.exit(0);

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void GestureTest() {
        gestureUtil = new GestureUtil(mHandler);
        btn_play=findViewById(R.id.btnplayrecord);
        btn_stop=findViewById(R.id.btnstoprecord);
        tv1=findViewById(R.id.textView1);
        tv2=findViewById(R.id.textView2);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureUtil.Play();
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gestureUtil.Stop();
            }
        });

    }
}
