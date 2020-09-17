package com.androidlec.icontact.NetworkTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import com.androidlec.icontact.CustomProgressDialog;
import com.androidlec.icontact.Dto.AddressDto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SelectNetworkTask extends AsyncTask<Integer, String, Object> {

    Context context;
    String mAddr;
    ArrayList<AddressDto> address;
    CustomProgressDialog cSelectProgressDialog;

    public SelectNetworkTask(Context context, String mAddr) {
        this.context = context;
        this.mAddr = mAddr;
        this.address = new ArrayList<AddressDto>(  );
    }

    @Override
    protected void onPreExecute() {
        cSelectProgressDialog = new CustomProgressDialog( context );
        cSelectProgressDialog.setCancelable( false );
        cSelectProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        cSelectProgressDialog.setDialogMessage("명함을 불러오는 중..");
        cSelectProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate( values );
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(Object o) {
        cSelectProgressDialog.dismiss();
    }

    @Override
    protected Object doInBackground(Integer... integers) {
        StringBuffer stringBuffer = new StringBuffer(  );
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try{
            URL url = new URL( mAddr );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout( 3000 );
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader( inputStream );
                bufferedReader = new BufferedReader( inputStreamReader );

                while(true) {
                    String strline = bufferedReader.readLine();
                    if(strline == null) break;
                    stringBuffer.append( strline + "\n" );
                }

                Parser(stringBuffer.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(bufferedReader != null) bufferedReader.close();
                if(inputStreamReader != null) inputStreamReader.close();
                if(inputStream != null) inputStream.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return address;
    }

    private void Parser(String s) {
        try{
            JSONObject jsonObject = new JSONObject( s );
            JSONArray jsonArray = new JSONArray( jsonObject.getString( "address_info" ) );

            for(int step=0; step<jsonArray.length(); step++) {
                JSONObject jsonObject_student = (JSONObject) jsonArray.get( step );
                int aSeqno = jsonObject_student.getInt( "aSeqno" );
                String aName = jsonObject_student.getString( "aName" );
                String aJob = jsonObject_student.getString( "aJob" );
                String aMobile = jsonObject_student.getString( "aMobile" );
                String aEmail = jsonObject_student.getString( "aEmail" );
                String aDepartment = jsonObject_student.getString( "aDepartment" );
                String aCompany = jsonObject_student.getString( "aCompany" );
                String aTel = jsonObject_student.getString( "aTel" );
                String aAddress = jsonObject_student.getString( "aAddress" );
                String aImage1 = jsonObject_student.getString( "aImage1" );
                AddressDto addressDto = new AddressDto(aSeqno, aName, aMobile, aEmail, aJob, aDepartment, aCompany, aTel, aAddress, aImage1);
                address.add( addressDto );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
