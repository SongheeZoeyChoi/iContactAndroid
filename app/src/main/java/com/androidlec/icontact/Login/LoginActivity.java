package com.androidlec.icontact.Login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.androidlec.icontact.Activity.MainSelectActivity;
import com.androidlec.icontact.CustomLoginProgressDialog;
import com.androidlec.icontact.CustomProgressDialog;
import com.androidlec.icontact.OneTimeActivity;
import com.androidlec.icontact.R;
import com.androidlec.icontact.STATICDATA;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.authcode.AuthorizationCode;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ApiRequest;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

public class LoginActivity extends OneTimeActivity {

    private Button btn_custom_login;
    private Button btn_custom_login_out;
    private Button btn_custom_unlink;
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;
    private CustomProgressDialog cProgressDialog;

    @Override
    protected void onResume() {
        super.onResume();
//        Log.v( "Status", "onResume()" );



    }

    private void setLoadingDialog() {
//        cProgressDialog = new CustomProgressDialog( LoginActivity.this );
//        cProgressDialog.setCancelable( false );
//        cProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//        cProgressDialog.setDialogMessage("로그인 중..");
//        cProgressDialog.getWindow().clearFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND);  // 다이어로그 보여줄시 뒤 흐림 제거
//        cProgressDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if( STATICDATA.LOGINSTATUS == 1 ) {
            setLoadingDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login);

        // 일회성 액티비티로 사용할 activity context를 담는다.
        actList.add(this);

        btn_custom_login = (Button) findViewById( R.id.btn_custom_login);
        btn_custom_login_out = (Button) findViewById( R.id.btn_custom_login_out);
        btn_custom_unlink = (Button) findViewById( R.id.btn_custom_unlink);

        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        btn_custom_unlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            @Override
                            public void onSuccess(Long result) {
                                Log.i("KAKAO_API", "연결 끊기 성공. id: " + result);
                            }
                        });
            }
        });

        btn_custom_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.open(AuthType.KAKAO_TALK, LoginActivity.this);
                final Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable()  {
                    public void run() {
                        if(STATICDATA.LOGINSTATUS == 1) {
                            startActivity( new Intent( LoginActivity.this, MainSelectActivity.class ) );
                            finish(); // 액티비티 종료
                        } else {
//                            Toast.makeText(LoginActivity.this, "로그인 중..", Toast.LENGTH_SHORT).show();
                            mHandler.postDelayed(this, 1500);
                        }
                    }
                }, 1500);
            }
        });

        btn_custom_login_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.i("KAKAO_API", "로그아웃 성공");
                            }
                        });
//                session.open(AuthType.KAKAO_ACCOUNT, LoginActivity.this);
            }
        });


        if (Session.getCurrentSession().checkAndImplicitOpen() && STATICDATA.LOGINCOUNT == 0) {
            // 액세스토큰 유효하거나 리프레시 토큰으로 액세스 토큰 갱신을 시도할 수 있는 경우
            startActivity( new Intent( LoginActivity.this, MainSelectActivity.class ) );
            finish(); // 액티비티 종료
        } else {
            // 무조건 재로그인을 시켜야 하는 경우
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        cProgressDialog.dismiss();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("KAKAO_API", "onDestroy");
        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}