package cn.dmrf.nuaa.gamebird.client;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.Socket;
//
//import cn.dmrf.nuaa.gamebird.R;
//
///**
// * Created by Wjyyy on 2018/6/3.
// */
//
////public class wifiiiclient {
////}
//
//
//        import android.os.Bundle;
//        import android.support.v7.app.AppCompatActivity;
//        import android.support.v7.widget.Toolbar;
//        import android.view.View;
//        import android.widget.EditText;
//        import android.widget.Toast;
//
//        import java.io.IOException;
//        import java.io.OutputStream;
//        import java.net.Socket;
//
//public class MainActivity extends AppCompatActivity {
//
//    private EditText editText_ip,editText_data;
//    private OutputStream outputStream = null;
//    private Socket socket = null;
//    private String ip;
//    private String data;
//    private boolean socketStatus = false;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        editText_ip = (EditText) findViewById(R.id.et_ip);
//        editText_data = (EditText) findViewById(R.id.et_data);
//
//    }
//
//
//    public void connect(View view){
//
//        ip = editText_ip.getText().toString();
//        if(ip == null){
//            Toast.makeText(MainActivity.this,"please input Server IP",Toast.LENGTH_SHORT).show();
//        }
//
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//
//                if (!socketStatus) {
//
//                    try {
//                        socket = new Socket(ip,8000);
//                        if(socket == null){
//                        }else {
//                            socketStatus = true;
//                        }
//                        outputStream = socket.getOutputStream();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        };
//        thread.start();
//
//    }
//
//
//    public void send(View view){
//
//        data = editText_data.getText().toString();
//        if(data == null){
//            Toast.makeText(MainActivity.this,"please input Sending Data",Toast.LENGTH_SHORT).show();
//        }else {
//            //在后面加上 '\0' ,是为了在服务端方便我们去解析；
//            data = data + '\0';
//        }
//
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                if(socketStatus){
//                    try {
//                        outputStream.write(data.getBytes());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        };
//        thread.start();
//
//    }
//
//    /*当客户端界面返回时，关闭相应的socket资源*/
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        /*关闭相应的资源*/
//        try {
//            outputStream.close();
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}