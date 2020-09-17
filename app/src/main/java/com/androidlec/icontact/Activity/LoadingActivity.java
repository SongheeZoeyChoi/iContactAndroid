package com.androidlec.icontact.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.androidlec.icontact.Login.LoginActivity;
import com.androidlec.icontact.R;

public class LoadingActivity extends AppCompatActivity {

    public static Activity loadingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_loading );
        loadingActivity = LoadingActivity.this;

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable()  {
            public void run() {
                // 시간 지난 후 실행할 코딩
                startActivity( new Intent( getApplicationContext(), LoginActivity.class) );
                finish();
            }
        }, 2000);


    }//------onCreate()

}//------END