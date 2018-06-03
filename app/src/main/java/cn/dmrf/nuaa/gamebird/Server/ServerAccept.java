package cn.dmrf.nuaa.gamebird.Server;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cn.dmrf.nuaa.gamebird.R;

public class ServerAccept extends Activity {
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serveraccept);
        img= (ImageView) findViewById(R.id.img);
        new ImgServerAsyncTask(img).execute();
    }
    public class ImgServerAsyncTask extends AsyncTask<Void,Void,Bitmap>
    {
        ImageView mImageView;
        public ImgServerAsyncTask(ImageView imageView)
        {
            mImageView=imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap;
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(4545);
                Socket client=serverSocket.accept();
                InputStream inputstream=client.getInputStream();
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                while((len=inputstream.read(buf))!=-1)
                {
                    out.write(buf, 0, len);
                }
                bitmap= BitmapFactory.decodeByteArray(out.toByteArray(),0,out.toByteArray().length);
//              bitmap=BitmapFactory.decodeStream(inputstream);无法从流中获取图片，求大神解释。
                inputstream.close();
                client.close();
                serverSocket.close();
                return bitmap;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap==null)
            {
                //log
            }else
            {
                mImageView.setImageBitmap(bitmap);
            }
            super.onPostExecute(bitmap);
        }
    }
}