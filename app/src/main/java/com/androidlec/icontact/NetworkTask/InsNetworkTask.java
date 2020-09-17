package com.androidlec.icontact.NetworkTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class InsNetworkTask extends AsyncTask<Integer, String, Void> {

    Context context;
    String mAddr;
    ProgressDialog progressDialog;

    //Constructor 를 만들어놔야 new 로 해서 쓸 수 있음
    public InsNetworkTask(Context context, String mAddr) {
        this.context = context;
        this.mAddr = mAddr;
    }

    /////////////// Start ProgressDialog ///////////////

    @Override
    protected void onPreExecute() { //실행전
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Dialog");
        progressDialog.setMessage("Insert.......");
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) { //끝났을떄
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    /////////////// End ProgressDialog /////////////////



    //////////////////////백그라운드//////////////////////
    @Override
    protected Void doInBackground(Integer... integers) {
//        Log.v( "Test", "doIn(insert)" );
        try {
            URL url = new URL(mAddr); //mAddr을가지고나감
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
//                Log.v( "Test", "HTTP_OK" );
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
