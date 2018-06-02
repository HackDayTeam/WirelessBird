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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;


import java.io.IOException;

import cn.dmrf.nuaa.gamebird.Gesture.GestureWindow;
import cn.dmrf.nuaa.gamebird.Gesture.GlobalBean;
import cn.dmrf.nuaa.gamebird.Gesture.TensorFlowUtil;
import cn.dmrf.nuaa.gamebird.Gesture.VerifyPermission;
import cn.dmrf.nuaa.gamebird.MindWave.SignalDetect;
import cn.dmrf.nuaa.gamebird.R;

public class LoadingActivity extends Activity {

    private Button btn_play;
    private Button btn_stop;
    private TextView tv1;
    private TextView tv2;
    private int flag_num=0;
    public static double predis1 = 0.0;
    private GestureWindow gestureWindow;
private GlobalBean globalBean;
    public static int status;
    public static SignalDetect sd = new SignalDetect();
    public static int attention=0;



    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        //设置圆环角度
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    tv1.setText(msg.obj.toString());
//                   if (gestureWindow.Judge(msg.obj.toString())){
//                       tv1.setVisibility(View.VISIBLE);
//                       tv1.setText(msg.obj.toString());
//                       flag_num++;
//                   }else {
//                       //tv1.setVisibility(View.GONE);
//                   }
            }
        }
    };


    private void updatestate(String dis, int which) {
        double now_dis = Double.valueOf(dis);

        switch (which) {
            case 0://L
                if (now_dis - predis1 > 3) {
                    tv1.setVisibility(View.VISIBLE);
                   tv1.setText("TaiChi");

                }else {
                    tv1.setVisibility(View.GONE);
                }
                predis1 = now_dis;
                break;
            case 1://R

                break;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            VerifyPermission verifyPermission = new VerifyPermission(LoadingActivity.this);
            verifyPermission.RequestPermission();
        }


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        status = sd.initDevice(LoadingActivity.this);
        sd.connect();
        gestureWindow=new GestureWindow(0.2f);


        //GestureTest();
        TaiChi();



    }


    private void TaiChi() {
        setContentView(R.layout.loading);
        final TextView gameStart = (TextView) findViewById(R.id.game_start);




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
        });
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


        setContentView(R.layout.gesture_test_layout);
        globalBean=new GlobalBean(LoadingActivity.this);
        globalBean.btnPlayRecord = (Button) findViewById(R.id.btnplayrecord);
        globalBean.btnStopRecord = (Button) findViewById(R.id.btnstoprecord);
        globalBean.tvDist = (TextView) findViewById(R.id.textView1);
        globalBean.tvDist2 = (TextView) findViewById(R.id.textView2);
        globalBean.flag_small = (ImageView) findViewById(R.id.flag_small);
        globalBean.tensorFlowUtil=new TensorFlowUtil(getAssets(),"abc_gesture_cnn.pb");
        try {
            globalBean.Init();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
