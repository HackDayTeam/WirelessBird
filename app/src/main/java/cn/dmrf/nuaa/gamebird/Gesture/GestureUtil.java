package cn.dmrf.nuaa.gamebird.Gesture;

import android.Manifest;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

import cn.dmrf.nuaa.gamebird.R;


public class GestureUtil {


    private double[] Freqarrary = {17500, 17850, 18200, 18550, 18900, 19250, 19600, 19950, 20300, 20650};        //设置播放频率
    private int numfre = 8;


    private boolean flag = true;        //播放标志

    private AudioRecord audioRecord;    //录音对象

    private int recBufSize = 4400 * 2;            //定义录音片长度


    /**
     * 采样率（默认44100，每秒44100个点）
     */
    private int sampleRateInHz = 44100;

    /**
     * 编码率（默认ENCODING_PCM_16BIT）
     */
    private int encodingBitrate = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * 声道（默认单声道）
     */
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;        //立体道


    private Handler mHandler;


    public GestureUtil(Handler handler) {
        mHandler = handler;
        int minBufSize = AudioRecord.getMinBufferSize(
                sampleRateInHz, channelConfig,
                encodingBitrate);
        minBufSize = recBufSize;      //0.1s

        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, sampleRateInHz,
                channelConfig,
                encodingBitrate, minBufSize);
    }

    public void Play() {
        flag = true;
        new ThreadInstantPlay().start();        //播放
        try {
            Thread.currentThread().sleep(10);    //等待开始播放再录音
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        new ThreadInstantRecord().start();        //录音

    }

    public void Stop() {
        flag = false;
    }


    /**
     * 即时播放线程
     *
     * @author lisi
     */
    class ThreadInstantPlay extends Thread {
        @Override
        public void run() {
            FrequencyPlayer FPlay = new FrequencyPlayer(numfre, Freqarrary);
            FPlay.palyWaveZ();
            while (flag) {
            }
            FPlay.colseWaveZ();
        }
    }

    /**
     * 即时录音线程
     */
    class ThreadInstantRecord extends Thread {
        @Override
        public void run() {
            short[] bsRecord = new short[recBufSize];
            short[] bsRecordL = new short[recBufSize / 2];
            short[] bsRecordR = new short[recBufSize / 2];
            int n = 0;
            double totPhase = 0;
            double lastDist = 0;
            double lastDistR = 0;
            double NowPhase = 0;
            //---------------------------

            //--------------jni------------------------
            DemoNew();
            //=========================================

            while (flag == false) {
            }
            try {
                audioRecord.startRecording();
            } catch (IllegalStateException e) {
                // 录音开始失败
                e.printStackTrace();
                return;
            }
            Log.w("tip", "start");

            int Len;
            while (flag)//大循环
            {
                Len = audioRecord.read(bsRecord, 0, recBufSize);//读取录音

                for (int i = 0; i < Len; i++) {
                    bsRecordL[i / 2] = bsRecord[i++];
                    bsRecordR[i / 2] = bsRecord[i];
                    n = n % (44100 * 30);
                }


                long s = System.currentTimeMillis();
                // Log.w("t-s",String.valueOf(s));
                double[] di = new double[110];
                DemoL(bsRecordL, di);
                lastDist = di[110 - 1];

                long s1 = System.currentTimeMillis();
                //Log.w("t-m",String.valueOf(s1));

                DemoR(bsRecordR, di);
                lastDistR = di[110 - 1];
                long s2 = System.currentTimeMillis();
                //Log.w("t-e",String.valueOf(s2));


                NowPhase += totPhase / 2;
                while (NowPhase < 0) NowPhase += Math.PI * 2;
                while (NowPhase > Math.PI * 2) NowPhase -= Math.PI * 2;

                Message msg1 = new Message();
                msg1.what = 1;
                DecimalFormat df = new DecimalFormat("#.00");

                msg1.obj = (df.format(lastDist));
                mHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = 2;

                msg2.obj = (df.format(lastDistR));
                mHandler.sendMessage(msg2);


            }//while end

            audioRecord.stop();
            Log.w("tip", "stop");
//            try {
//                long a =System.currentTimeMillis();
//                saveToSDCard("001"+a+".txt",BIGDATA,n);
//                saveToSDCard("002"+a+".txt",BIGDATA2,n);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            Log.w("tip", "end");

        }


        /**
         * 存储方法
         *
         * @param filename
         * @param content
         * @param length
         * @throws Exception
         */
        public void saveToSDCard(String filename, short[] content, int length) throws Exception {
            ///storage/emulated/0
            File file = new File(Environment.getExternalStorageDirectory() + "/TEST", filename);
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "gb2312");
            for (int i = 0; i < length; i++) {
                writer.write(String.valueOf(content[i]));
                writer.write("\n");
            }
            writer.flush();
            writer.close();
            outStream.close();
        }

        public void saveToSDCard(String filename, double[] content, int length) throws Exception {
            File file = new File(Environment.getExternalStorageDirectory() + "/TEST", filename);
            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "gb2312");
            for (int i = 0; i < length; i++) {
                writer.write(String.valueOf(content[i]));
                writer.write("\n");
            }
            writer.flush();
            writer.close();
            outStream.close();
        }


    }


    //本地方法，由java调用

    public native void DemoNew();

    public native int DemoL(short[] Record, double[] DIST);

    public native int DemoR(short[] Record, double[] DIST);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


}
