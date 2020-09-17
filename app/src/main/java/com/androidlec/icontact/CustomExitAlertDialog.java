package com.androidlec.icontact;

import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.androidlec.icontact.Activity.MainSelectActivity;

public class CustomExitAlertDialog extends Dialog {
    TextView tv_message;
    Button exit_btn, tohome_btn;
    public CustomExitAlertDialog(Context context) {
        super(context);

        requestWindowFeature( Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.alert_exit_dialog ); // 다이얼로그에 박을 레이아웃

        tv_message = findViewById(R.id.tv_progress_message);
        exit_btn = findViewById( R.id.btn_exit );
        tohome_btn = findViewById( R.id.btn_tohome );

        exit_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainSelectActivity mainSelectActivity = (MainSelectActivity) MainSelectActivity.mainSelectActivity;
                mainSelectActivity.finish();
            }
        } );

        tohome_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog(context);
            }
        } );
    }

    // 다이어로그 메시지 설정
    public void setDialogMessage(String message) {
        tv_message.setText(message);
    }

    public void setContentView(Layout layout) {
        setContentView( layout );
    }

    public void dismissDialog(Context context) {
        MainSelectActivity mainSelectActivity = (MainSelectActivity) context;
        mainSelectActivity.dismissExitDialog();
    }
}
