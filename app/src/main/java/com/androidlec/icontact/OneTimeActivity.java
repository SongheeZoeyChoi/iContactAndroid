package com.androidlec.icontact;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OneTimeActivity extends AppCompatActivity {
    public static ArrayList<Activity> actList = new ArrayList<>();

    public void actFinish() {
        for(int i=0; i<actList.size(); i++) {
            actList.get(i).finish();
        }
        actList.clear();
    }

}
