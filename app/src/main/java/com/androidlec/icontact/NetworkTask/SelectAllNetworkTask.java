package com.androidlec.icontact.NetworkTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;

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

public class SelectAllNetworkTask extends AsyncTask<Integer, String, Object> {

    Context context;
    String mAddr;
//    CustomProgressDialog cProgressDialog;
    ArrayList<AddressDto> address;
    public SelectAllNetworkTask(Context context, String mAddr) {
        this.context = context;
        this.mAddr = mAddr;
        this.address = new ArrayList<AddressDto>(  );
    }

    @Override
    protected void onPreExecute() {
//        cProgressDialog = new CustomProgressDialog( context );
//        cProgressDialog.setCancelable( false );
//        cProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//        cProgressDialog.setDialogMessage("목록 불러오는 중..");
//        cProgressDialog.show();
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
//        cProgressDialog.dismiss();
    }

    @Override
    protected Object doInBackground(Integer... integers) {
//        Log.v("Status: ", "doInBackground( )" );
        StringBuffer stringBuffer = new StringBuffer(  );
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try{
            URL url = new URL( mAddr );
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout( 3000 );
            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                Log.v("Status: ", "HTTP_OK( )" );
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
//        Log.v("Status: ", "Parser( )" );
        try{
            JSONObject jsonObject = new JSONObject( s );
            JSONArray jsonArray = new JSONArray( jsonObject.getString( "address_info" ) );

            for(int step=0; step<jsonArray.length(); step++) {
                JSONObject jsonObject_contact = (JSONObject) jsonArray.get( step );
                int aSeqno = jsonObject_contact.getInt( "aSeqno" );
                String aName = jsonObject_contact.getString( "aName" );
                String aJob = jsonObject_contact.getString( "aJob" );
                String aDepartment = jsonObject_contact.getString( "aDepartment" );
                String aCompany = jsonObject_contact.getString( "aCompany" );
                String aImage1 = jsonObject_contact.getString( "aImage1" );
                String aTel = jsonObject_contact.getString( "aTel" );
                aTel = setTelFormat(aTel);
                AddressDto addressDto = new AddressDto(aSeqno, aName, aJob, aDepartment, aCompany, aImage1, aTel);
                address.add( addressDto );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String setTelFormat(String aTel) {
        int seoul = 0;
        if ( aTel.substring(1,1).equals( 2 ) ) {
            seoul = 1;
        }
        if(aTel.length() == 8) {
            aTel = "Tel." + aTel.substring( 0, 4 ) + "-" + aTel.substring( 4 );
        }
        else if(aTel.length() == 9) {
            aTel = "Tel." + aTel.substring( 0, 2 ) + "-" + aTel.substring( 2,5 ) + "-" + aTel.substring( 5 );
        }
        else if(aTel.length() == 10 && seoul == 1) {
            aTel = "Tel." + aTel.substring( 0, 2 ) + "-" + aTel.substring( 2,6 ) + "-" + aTel.substring( 6 );
        }
        else if(aTel.length() == 10 && seoul == 0) {
            aTel = "Tel." + aTel.substring( 0, 3 ) + "-" + aTel.substring( 3,6 ) + "-" + aTel.substring( 6 );
        }
        else if(aTel.length() == 11) {
            aTel = "Tel." + aTel.substring( 0, 3 ) + "-" + aTel.substring( 3,7 ) + "-" + aTel.substring( 7 );
        }
        return aTel;
        // 양식
        //xxxx-xxxx
        //xx-xxx-xxxx
        //xx-xxxx-xxxx
        //xxx-xxx-xxxx
        //xxx-xxxx-xxxx
    }

}
