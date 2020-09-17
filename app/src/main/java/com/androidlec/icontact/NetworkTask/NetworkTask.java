package com.androidlec.icontact.NetworkTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<Integer, String, Void> {

    Context context;
    String mAddr;
    ProgressDialog progressDialog;

    public NetworkTask(Context context, String mAddr) {
        this.context = context;
        this.mAddr = mAddr;
    }


    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Dialog");
        progressDialog.setMessage("명함 삭제중");
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }








    @Override
    protected Void doInBackground(Integer... integers) {

        try {
            URL url = new URL(mAddr);   //네트워크타고 mAddr을 가지고 가는것
//            Log.v("TAG", mAddr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            //아무것도 안적어도 OK문을 실행했다는뜻         //InsertActvity에있는 urlAddr를 실행했다는뜻
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return null;
    }







}
