package cn.dmrf.nuaa.gamebird;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.dmrf.nuaa.gamebird.Bird.LoadingActivity;
import cn.dmrf.nuaa.gamebird.MindWave.SignalDetect;

public class MainActivity extends AppCompatActivity {

    //Test
    private Button btn_attention;
    private TextView tv_attention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_test_layout);

        //MindWave Test
        btn_attention = (Button) findViewById(R.id.btn_attention);
        tv_attention = (TextView) findViewById(R.id.tv_attention);

        btn_attention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MindWaveTest();
            }
        });
    }


    private void MindWaveTest() {
        int attention = 0;


        SignalDetect sd = new SignalDetect();
        int status = sd.initDevice(MainActivity.this);
        sd.connect();
        switch (status) {
            case 1:
                attention = sd.getAttention();
                break;
            case 2:
                Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                break;
            case -1:
                Toast.makeText(this, "Unknown Error!", Toast.LENGTH_LONG);
                break;
            case 0:
                attention = 0;
                break;
            default:
                break;
        }
        tv_attention.setText("" + attention);
    }
}
