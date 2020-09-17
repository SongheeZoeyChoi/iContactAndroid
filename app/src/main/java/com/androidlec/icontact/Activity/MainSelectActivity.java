package com.androidlec.icontact.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androidlec.icontact.AddressAdapter;
import com.androidlec.icontact.CustomExitAlertDialog;
import com.androidlec.icontact.CustomMainProgressDialog;
import com.androidlec.icontact.CustomProgressDialog;
import com.androidlec.icontact.CustomUnlinkAlertDialog;
import com.androidlec.icontact.Dto.AddressDto;
import com.androidlec.icontact.Login.GlobalApplication;
import com.androidlec.icontact.Login.LoginActivity;
import com.androidlec.icontact.NetworkTask.SelectAllNetworkTask;
import com.androidlec.icontact.R;
import com.androidlec.icontact.STATICDATA;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;

import java.util.ArrayList;

public class MainSelectActivity extends AppCompatActivity {

    public static Activity mainSelectActivity;
    final static String TAG = "Status";
    //네비게이션 드로어
    private DrawerLayout drawerLayout;
    private View drawerView;

    private CustomExitAlertDialog cAlertDialog;
    private CustomProgressDialog cProgressDialog;
    private CustomUnlinkAlertDialog cUnlinkAlertDialog;
    private GlobalApplication globalApp;

    //검색
    TextView tv_main_search;
    TextView btn_search;
    EditText edit_search;
    Button btn_main_add, btn_main_listup;

    // 좌상단 유저 닉네임
    TextView tv_main_username;
    
    // 우상단 팝업 메뉴
    TextView btn_main_popup;

    //리스트 뷰
//    AddressDB addressDB;
    private ArrayList<AddressDto> data = null;
    private AddressAdapter adapter = null;
    private ListView listView = null;

//    SQLiteDatabase DB;

    String urlAddr;
//    String centIP = "182.230.84.233";
    String centIP = "192.168.0.113";

    @Override
    protected void onResume() {
        Log.v( TAG, "onResume()" );
        super.onResume();
        connectGetData();
    }

    @Override
    public void onBackPressed() {
        cAlertDialog = new CustomExitAlertDialog( MainSelectActivity.this );
        cAlertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
//        cAlertDialog.getWindow().clearFlags( WindowManager.LayoutParams.ALPHA_CHANGED);  // 다이어로그 보여줄시 뒤 흐림 제거
        cAlertDialog.show();
    }

    public void dismissExitDialog() {
        cAlertDialog.dismiss();
    }

    public void dismissUnlinkDialog() {
        cUnlinkAlertDialog.dismiss();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v( TAG, "onCreate()" );
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );

        mainSelectActivity = MainSelectActivity.this;
        LoadingActivity loadingActivity = (LoadingActivity) LoadingActivity.loadingActivity;
        loadingActivity.finish();

        // GlobalApplication 에 담긴 카카오 로그인 유저의 정보를 getter로 가져와 STATIC 데이터로 저장
        globalApp = GlobalApplication.getGlobalApplicationContext();
        STATICDATA.USERNAME = globalApp.getUserName();
        STATICDATA.USEREMAIL = globalApp.getUserEmail();
        STATICDATA.LOGINCOUNT = 1;

        tv_main_username = findViewById( R.id.tv_main_username );
        tv_main_username.setText( STATICDATA.USERNAME);

        // 메인 우측하단 작성버튼
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent( MainSelectActivity.this, newContactActivity.class);
//                startActivity(intent);
//            }
//        });

        showLoadingDialog();
        // 명함 리스트 전부 가져오기
        connectGetData();


        btn_main_popup = findViewById( R.id.btn_main_popup );
        btn_main_popup.setOnClickListener( onMenuItemClickListener );
                
        // 네비게이션 등록
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.drawer);
        // 네비게이션 드로어 리스너 등록
        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

//        addressDB = new AddressDB( MainSelectActivity.this);

        //검색 텍스트 클릭
        tv_main_search = findViewById(R.id.tv_main_search);
        tv_main_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        // 검색 버튼 등록 / 리스너 등록
        btn_search = findViewById(R.id.btn_drawer_search);
        btn_search.setOnClickListener(onClickListener);
        edit_search = findViewById(R.id.edit_drawer_search);

        // 우측하단 명함 작성 버튼 / 하단 스크롤 업 버튼(invisible)
        btn_main_add = findViewById( R.id.btn_main_add );
        btn_main_listup = findViewById( R.id.btn_main_listup );
        btn_main_add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( MainSelectActivity.this, newContactActivity.class);
                startActivity(intent);
            }
        } );
        btn_main_listup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.smoothScrollToPosition(0);
            }
        } );

        listView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }
            // 일정 이상 스크롤시 하단의 스크롤버튼 VISIBLE
            @Override
            public void onScroll(AbsListView absListView, int firstitem, int count, int total) {
                if (firstitem > 15)
                    btn_main_listup.setVisibility( View.VISIBLE );
                else
                    btn_main_listup.setVisibility( View.INVISIBLE );
            }
        } );

        // 리스트 아이템 리스너등록
        listView.setOnItemClickListener(onItemClickListener);


    }//------ onCreate

    // 모든 명함 데이터 DB로부터 불러옴
    private void connectGetData() {
        edit_search = findViewById(R.id.edit_drawer_search);
        btn_search = findViewById(R.id.btn_drawer_search);
        String aName = edit_search.getText().toString();

        urlAddr = "http://" + centIP + ":8080/addressProject/Address_query_all.jsp?";
        urlAddr = urlAddr + "aName=" + aName;
        try{
            SelectAllNetworkTask selectAllNetworkTask = new SelectAllNetworkTask( MainSelectActivity.this, urlAddr );
            Object obj = selectAllNetworkTask.execute( ).get( );
            data = (ArrayList<AddressDto>) obj;
            adapter = new AddressAdapter( MainSelectActivity.this, R.layout.custom_layout, data, centIP );
            listView = findViewById( R.id.lv_selected );
            listView.setAdapter( adapter );
            cProgressDialog.dismiss();
            listView.setOnItemClickListener( onItemClickListener);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showUnlinkDialog() {
        cUnlinkAlertDialog = new CustomUnlinkAlertDialog( MainSelectActivity.this );
        cUnlinkAlertDialog.setCancelable( false );
        cUnlinkAlertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        cUnlinkAlertDialog.show();
    }

    private void showLoadingDialog() {
        cProgressDialog = new CustomProgressDialog( MainSelectActivity.this );
        cProgressDialog.setCancelable( false );
        cProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        cProgressDialog.show();
    }

    // 툴바 우측 옵션메뉴 클릭 리스너
    View.OnClickListener onMenuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
            // 각 메뉴 커스텀 가능
            popupMenu.getMenu().findItem(R.id.menu_unlink).setTitle( Html.fromHtml("<font color='#FF0000'>회원탈퇴</font>"));
            // 팝업 메뉴 클릭 리스너
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.menu_logout:
                            Toast.makeText( MainSelectActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT ).show();
                            startActivity( new Intent( MainSelectActivity.this, LoadingActivity.class) );
                            finish();
                            break;
                        case R.id.menu_unlink:
                            showUnlinkDialog();
                            final Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()  {
                                public void run() {
                                    if(STATICDATA.USERSTATUS == 4) {
                                        STATICDATA.LOGINCOUNT = 0;
                                        STATICDATA.USERSTATUS = 0;
                                        cUnlinkAlertDialog.dismiss();
                                        Toast.makeText( MainSelectActivity.this, "탈퇴 처리 되었습니다.", Toast.LENGTH_SHORT ).show();
                                        startActivity( new Intent( MainSelectActivity.this, LoadingActivity.class) );
                                        finish();
                                    }
                                }
                            }, 1000);

                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    // 상세 페이지 (수정&삭제 가능)
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        Intent intent;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            intent = new Intent( MainSelectActivity.this, AddressInfoActivity.class);
            intent.putExtra("aSeqno", data.get(position).getaSeqno());
            intent.putExtra("aName", data.get(position).getaName());
            intent.putExtra("aMobile", data.get(position).getaMobile());
            intent.putExtra("aEmail", data.get(position).getaEmail());
            intent.putExtra("aCompany", data.get(position).getaCompany());
            intent.putExtra("aDepartment", data.get(position).getaDepartment());
            intent.putExtra("aJob", data.get(position).getaJob());
            intent.putExtra("aTel", data.get(position).getaTel());
            intent.putExtra("aAddress", data.get(position).getaAddress());
            intent.putExtra("aImage1", data.get(position).getaImage1());
            startActivity(intent);
        }
    };

    // 검색 버튼 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            connectGetData();
            drawerLayout.closeDrawers();

        }
    };

    // 네비게이션 드로어 커스텀
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { //슬라이드했을떄 호출
        }
        @Override
        public void onDrawerOpened(@NonNull View drawerView) { //오픈됐을떄
            edit_search.setText(null);
        }
        @Override
        public void onDrawerClosed(@NonNull View drawerView) { // 닫혔을떄
        }
        @Override
        public void onDrawerStateChanged(int newState) { // 바꼈을떄
            //키보드 숨김
            if (drawerView != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }
        }
    };

    //리스트
//    public void selectAllAddressList(){
//        edit_search = findViewById(R.id.edit_drawer_search);
//        btn_search = findViewById(R.id.btn_drawer_search);
//        String Name = edit_search.getText().toString();
//        try {
//            DB = addressDB.getReadableDatabase();  //불러오는것
//            String query = "select aSeqno, aName, aJob, aDepartment, aCompany, aImage1 from addressBook where aName like '%" + Name + "%';";
//            Cursor cursor = DB.rawQuery(query, null);
////            Log.v("TAG1", Name);
//            data = new ArrayList<>();
//            while (cursor.moveToNext()){
//                int aSeqno = cursor.getInt( 0 );
//                String aName = cursor.getString(1);
//                String aJob = cursor.getString(2);
//                String aDepartment = cursor.getString(3);
//                String aCompany = cursor.getString(4);
//                String aImage1 = cursor.getString(5);
////                Log.v("TAG", aName);
//                data.add(new AddressDto(aSeqno, aName, aJob, aDepartment, aCompany, aImage1));
//            }
//            cursor.close();
//            addressDB.close();
//        }catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText( MainSelectActivity.this, "Select Error", Toast.LENGTH_SHORT).show();
//        }
//    }

    //리스트 뷰 리셋
//    private void setListView() {
//        adapter = new AddressAdapter( MainSelectActivity.this, R.layout.custom_layout, data, centIP);
//        listView = findViewById(R.id.lv_selected);
//        listView.setAdapter(adapter);
//    }


}//------END