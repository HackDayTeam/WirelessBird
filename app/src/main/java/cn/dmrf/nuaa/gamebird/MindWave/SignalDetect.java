package cn.dmrf.nuaa.gamebird.MindWave;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.BodyDataType;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import cn.dmrf.nuaa.gamebird.MainActivity;

/**
 * Created by Administrator on 2018/6/2.
 */

public class SignalDetect {

    private static final String TAG = SignalDetect.class.getSimpleName();
    private TgStreamReader tgStreamReader;

    private BluetoothAdapter mBluetoothAdapter;

    private int badPacketCount = 0;

    public int initDevice(Context c) {
        int result = -1;
        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Log.i(TAG, "wrong: can't detect device");
                Toast.makeText(
                        c,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
//                        finish();
                result = 0;
//                return 0;
            } else {
                Toast.makeText(c, "Success!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "success.");
//                return 1;

                result = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "error:" + e.getMessage());
            return 2;
        }

        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tgStreamReader = new TgStreamReader(mBluetoothAdapter,callback);
        // (2) Demo of setGetDataTimeOutTime, the default time is 5s, please call it before connect() of connectAndStart()
        tgStreamReader.setGetDataTimeOutTime(6);
        // (3) Demo of startLog, you will get more sdk log by logcat if you call this function

        return result;
    }

    public void connect() {
        badPacketCount = 0;

        // (5) demo of isBTConnected
        if(tgStreamReader != null && tgStreamReader.isBTConnected()){

            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // (4) Demo of  using connect() and start() to replace connectAndStart(),
        // please call start() when the state is changed to STATE_CONNECTED
        tgStreamReader.connect();
//				tgStreamReader.connectAndStart();
    }

    // (7) demo of TgStreamHandler
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    Log.i(TAG, "Connecting...");
//                    showToast("Connecting...", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    Log.i(TAG, "Connected!");
                    tgStreamReader.start();
//                    showToast("Connected", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working

                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it
                    tgStreamReader.startRecordRawData();

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    tgStreamReader.stopRecordRawData();

//                    showToast("Get data time out!", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.

                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    break;
            }
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_STATE;
            msg.arg1 = connectionStates;
            LinkDetectedHandler.sendMessage(msg);
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e(TAG,"onRecordFail: " +flag);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
            badPacketCount ++;
            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = MSG_UPDATE_BAD_PACKET;
            msg.arg1 = badPacketCount;
            LinkDetectedHandler.sendMessage(msg);

        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.

            Message msg = LinkDetectedHandler.obtainMessage();
            msg.what = datatype;
            msg.arg1 = data;
            msg.obj = obj;
            LinkDetectedHandler.sendMessage(msg);
            //Log.i(TAG,"onDataReceived");
        }

    };

    private int attention = 0;

    public int getAttention() {
        return attention;
    }

    private boolean isPressing = false;
    private static final int MSG_UPDATE_BAD_PACKET = 1001;
    private static final int MSG_UPDATE_STATE = 1002;

    private Handler LinkDetectedHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // (8) demo of BodyDataType
            switch (msg.what) {
                case BodyDataType.CODE_POOR_SIGNAL:
                    int poorSignal = msg.arg1;
                    Log.d(TAG, "poorSignal:" + poorSignal);
//                    tv_ps.setText(""+msg.arg1);

                    if (poorSignal == 200) {
                        isPressing = true;
                    }
                    if (poorSignal == 0) {
                        if (isPressing) {
                            isPressing = false;
                        }
                    }

                    break;
                case MindDataType.CODE_ATTENTION:
                    Log.d(TAG, "attention:" + msg.arg1);
                    attention = msg.arg1;
//                    tv_hr.setText("" + msg.arg1);
                    break;
                case MSG_UPDATE_BAD_PACKET:
                    Log.d(TAG, "badPacket:" + msg.arg1);
//                    tv_badpacket.setText("" + msg.arg1);

                    break;
                case MSG_UPDATE_STATE:
                    Log.d(TAG, "data:" + msg.arg1);
//                    tv_connection.setText(""+msg.arg1);
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

//    public void showToast(Context c, final String msg,final int timeStyle){
//
//        c.runOnUiThread(new Runnable()
//        {
//            public void run()
//            {
//                Toast.makeText(getApplicationContext(), msg, timeStyle).show();
//            }
//
//        });
//    }
}
