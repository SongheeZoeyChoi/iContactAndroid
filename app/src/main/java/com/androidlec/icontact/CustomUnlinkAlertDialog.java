package com.androidlec.icontact;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidlec.icontact.Activity.MainSelectActivity;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

public class CustomUnlinkAlertDialog extends Dialog {
    TextView tv_message;
    Button unlink_btn, link_btn;
    public CustomUnlinkAlertDialog(Context context) {
        super(context);
        requestWindowFeature( Window.FEATURE_NO_TITLE); // 지저분한(?) 다이얼 로그 제목을 날림
        setContentView(R.layout.alert_unlink_dialog ); // 다이얼로그에 박을 레이아웃

        tv_message = findViewById(R.id.tv_progress_message);
        unlink_btn = findViewById( R.id.btn_unlink );
        link_btn = findViewById( R.id.btn_link );

        unlink_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog alertDialog = new AlertDialog.Builder(context)
//                        .setMessage( "탈퇴 처리중입니다." )
//                        .setCancelable( false )
//                        .show();
                // 연결끊기
                UserManagement.getInstance()
                        .requestUnlink(new UnLinkResponseCallback() {
                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                            }

                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Log.e("KAKAO_API", "연결 끊기 실패: " + errorResult);

                            }
                            // 로그아웃
                            @Override
                            public void onSuccess(Long result) {
                                Log.i("KAKAO_API", "연결 끊기 성공. id: " + result);
                                STATICDATA.USERSTATUS = 4;
                                UserManagement.getInstance()
                                        .requestLogout(new LogoutResponseCallback() {

                                            @Override
                                            public void onCompleteLogout() {
                                                Log.i("KAKAO_API", "로그아웃 성공");
                                            }
                                        });
                            }
                        });
//                alertDialog.dismiss();
                dismissDialog(context);
            }
        } );

        link_btn.setOnClickListener( new View.OnClickListener() {
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
        mainSelectActivity.dismissUnlinkDialog();
    }
}
