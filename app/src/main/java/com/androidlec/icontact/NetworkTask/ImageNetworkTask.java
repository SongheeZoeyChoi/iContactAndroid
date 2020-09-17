package com.androidlec.icontact.NetworkTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.androidlec.icontact.CustomProgressDialog;
import com.androidlec.icontact.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageNetworkTask extends AsyncTask<Integer, String, Integer> {

    final static String TAG = "NetworkTask";

    Context context = null;
    String mAddr = null;
    CustomProgressDialog cProgressDialog = null;
    String devicePath;
    ImageView imageView;

    public ImageNetworkTask(Context context, String mAddr, ImageView imageView) {
        this.context = context;
        this.mAddr = mAddr;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
//        Log.v(TAG, "onPreExecute()");
        cProgressDialog = new CustomProgressDialog( context );
        cProgressDialog.setCancelable( false );
        cProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        cProgressDialog.setDialogMessage("이미지 불러오는 중..");
        cProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate( values );
    }

    @Override
    protected void onPostExecute(Integer integer) {
//        Log.v(TAG, "onPostExecute()");
//        Log.v(TAG, "devicePath : " + devicePath);
        Bitmap bitmap = BitmapFactory.decodeFile( devicePath );
        imageView.setImageBitmap( bitmap );
        cProgressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
//        Log.v(TAG, "doInBackground()");
        //File Name 찾기
        int index = mAddr.lastIndexOf( "/" );
        String imgName = mAddr.substring( index + 1 );
        devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.androidlec.icontact/files/" + imgName.trim();
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            URL url = new URL( mAddr );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout( 5000 );
            // data 를 len만큼 불러온다. MainActivity 30.line 참고
            int len = httpURLConnection.getContentLength();
            byte[] bs = new byte[len];

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                fileOutputStream = context.openFileOutput( imgName, 0 );

                while(true) {
                    int i = inputStream.read(bs);
                    if(i<0) break;
                    // off:0  -> append
                    fileOutputStream.write( bs,0,i );
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(fileOutputStream != null) fileOutputStream.close();
                if(inputStream != null) inputStream.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
