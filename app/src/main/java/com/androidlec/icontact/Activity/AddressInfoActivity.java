package com.androidlec.icontact.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidlec.icontact.AddressAdapter;
import com.androidlec.icontact.Dto.AddressDto;
import com.androidlec.icontact.NetworkTask.ImageNetworkTask;
import com.androidlec.icontact.NetworkTask.NetworkTask;
import com.androidlec.icontact.NetworkTask.SelectNetworkTask;
import com.androidlec.icontact.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    ImageView pick_imageView1, btn_addressinfo_back;
    TextView tv_pick_aName, tv_pick_aMobile, tv_pick_aEmail,
            tv_pick_aCompany, tv_pick_aDepartment, tv_pick_aJob, tv_pick_aTel, tv_pick_aAddress, menu_update, btn_popup;

    private int seqno;
//    private AddressDB addressDB;
    private AddressDto addressDto;
    private ArrayList<AddressDto> data = null;
    private AddressAdapter adapter = null;
    private GoogleMap gMap;
    private Geocoder geocoder;

//    private String aName, aMobile, aEmail, aCompany, aDepartment, aJob, aTel, aAddress, aImage1;
    private String urlAddr;
//    private String centIP = "182.230.84.233";
    private String centIP = "192.168.0.113";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_address_info );
//        addressDB = new AddressDB( AddressInfoActivity.this );

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder( AddressInfoActivity.this );

        setWidgetId(); // 위젯 Id 등록
        setListener();
        getIntentDate(); // 리스트뷰에서 seqno 넘겨받기
        connectGetData();
//        setAddressInfo(); // 명함 상세정보 DB에서 받아오기
         // 모든 텍스트필드 채우기

        downloadImage();
    } //------ onCreate()

    // 옵션 메뉴 등록
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        menu.findItem(R.id.menu_update).setTitle(Html.fromHtml("<font color='#FFFFFF'>편집</font>"));
//        menu.findItem(R.id.menu_delete).setTitle(Html.fromHtml("<font color='#FF0000'>명함 삭제</font>"));
        return true;
    }

    // 옵션메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        Intent intent;
//        switch (id) {
//        }

        return super.onOptionsItemSelected(item);
    }
    // 지도정보 세팅
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        List<Address> list = null;
        LatLng latLng = null;
        String place = data.get( 0 ).getaAddress();
        try {
            list = geocoder.getFromLocationName
                    (place, // 지역 이름
                            1); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Status: ","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
//                Log.e("Status: ", "해당되는 주소 정보는 없습니다" );
            } else {
//                Log.v("Status: ", "해당되는 주소가 존재합니다." );
                Address addr = list.get(0);
                double lat = addr.getLatitude();
                double lon = addr.getLongitude();
                latLng = new LatLng(lat, lon);
//                Log.v("LNG", latLng.toString());
                Marker marker = googleMap.addMarker( new MarkerOptions()
                        .position( latLng )
                        .title(place)
                );
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(latLng,17) );
            }
        }
    }


    // 툴바 우측 옵션메뉴 클릭 리스너
    View.OnClickListener onMenuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu_contactinfo, popupMenu.getMenu());
            // 각 메뉴 커스텀 가능
            popupMenu.getMenu().findItem(R.id.menu_delete).setTitle(Html.fromHtml("<font color='#FF0000'>명함 삭제</font>"));
            // 팝업 메뉴 클릭 리스너
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    switch (id) {
                        case R.id.menu_delete:
                            deleteAddress();
                            break;
                        case R.id.btn_addressinfo_back:
                            onBackPressed();
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    };

    // 툴바 편집 버튼 클릭 리스너
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch(v.getId()) {
                case R.id.menu_update:
                    intent = new Intent( AddressInfoActivity.this, newContactActivity.class);
                    intent.putExtra("aSeqno", seqno );
                    intent.putExtra("aName", tv_pick_aName.getText());
                    intent.putExtra("aMobile", tv_pick_aMobile.getText());
                    intent.putExtra("aEmail", tv_pick_aEmail.getText());
                    intent.putExtra("aCompany", tv_pick_aCompany.getText());
                    intent.putExtra("aDepartment", tv_pick_aDepartment.getText());
                    intent.putExtra("aJob", tv_pick_aJob.getText());
                    intent.putExtra("aTel", tv_pick_aTel.getText());
                    intent.putExtra("aAddress", tv_pick_aAddress.getText());
                    intent.putExtra("aImage", data.get( 0 ).getaImage1() );
//                    intent.putExtra("aImage1", tv_pick_aName.getText());
                    startActivity(intent);
                    break;
            }
        }
    };

    // 수정할 데이터가 있으면 넘겨받는 메소드
    private void getIntentDate() {
        Intent intent = getIntent();
        seqno = intent.getIntExtra("aSeqno", 0);
    }

    // 위젯 Id 등록
    private void setWidgetId() {
//        Log.v( "Sel", "setWidgetId()" );
        tv_pick_aName = findViewById(R.id.tv_pick_aName);
        tv_pick_aMobile = findViewById(R.id.tv_pick_aMobile);
        tv_pick_aEmail = findViewById(R.id.tv_pick_aEmail);
        tv_pick_aCompany = findViewById(R.id.tv_pick_aCompany);
        tv_pick_aDepartment = findViewById(R.id.tv_pick_aDepartment);
        tv_pick_aJob = findViewById(R.id.tv_pick_aJob);
        tv_pick_aTel = findViewById(R.id.tv_pick_aTel);
        tv_pick_aAddress = findViewById(R.id.tv_pick_aAddress);
        pick_imageView1 = findViewById( R.id.add_imageView1 );
        menu_update = findViewById( R.id.menu_update );
        btn_popup = findViewById(R.id.btn_popup);
        btn_addressinfo_back = findViewById( R.id.btn_addressinfo_back );
//        tv_pick_Delete = findViewById( R.id.tv_pick_Delete );
    }

    // 리스너 등록
    private void setListener() {
//        tv_pick_Delete.setOnClickListener( onClickListener);
        menu_update.setOnClickListener( onClickListener );
        btn_popup.setOnClickListener( onMenuItemClickListener );
        btn_addressinfo_back.setOnClickListener( onClickListener );
    }

    // 모든 텍스트필드 채우기
    private void setAllTextView() {
//        Log.v( "Sel", data.get( 0 ).getaName());

        tv_pick_aName.setText( data.get( 0 ).getaName());
        tv_pick_aJob.setText( data.get( 0 ).getaJob());
        tv_pick_aMobile.setText( data.get( 0 ).getaMobile() );
        tv_pick_aEmail.setText( data.get( 0 ).getaEmail());
        tv_pick_aCompany.setText( data.get( 0 ).getaCompany() );
        tv_pick_aDepartment.setText( data.get( 0 ).getaDepartment() );
        tv_pick_aTel.setText( data.get( 0 ).getaTel() );
        tv_pick_aAddress.setText( data.get( 0 ).getaAddress() );
    }

//    // 명함 상세 조회 메소드
//    private void setAddressInfo() {
//        Log.v( "Status", "setAddressInfo()" );
//        SQLiteDatabase DB;
//        try {
//            DB = addressDB.getReadableDatabase();  //불러오는것
//            String query = "SELECT aName, aMobile, aEmail, aCompany, aDepartment, aJob, aTel, aAddress FROM addressBook WHERE aSeqno=" + seqno + ";";
//            Cursor cursor = DB.rawQuery(query, null);
//            if (cursor.moveToNext()){
//                String aName = cursor.getString(0);
//                String aMobile = cursor.getString(1);
//                String aEmail = cursor.getString(2);
//                String aCompany = cursor.getString(3);
//                String aDepartment = cursor.getString(4);
//                String aJob = cursor.getString(5);
//                String aTel = cursor.getString(6);
//                String aAddress = cursor.getString(7);
//                addressDto = new AddressDto(aName, aMobile, aEmail, aCompany, aDepartment, aJob, aTel, aAddress);
//            }
//            cursor.close();
//            addressDB.close();
//        }catch (Exception e){
//            e.printStackTrace();
//            Toast.makeText( AddressInfoActivity.this, "Select Error", Toast.LENGTH_SHORT).show();
//        }
//    }

//    // 명함 삭제 메소드
//    private void deleteAddress() {
//        SQLiteDatabase DB;
//        Intent intent;
//        try {
//            DB = addressDB.getWritableDatabase();
//            String query = "DELETE FROM addressBook WHERE aSeqno=" + seqno + ";";
//            DB.execSQL( query );
//            addressDB.close();
//            Toast.makeText( AddressInfoActivity.this, "삭제성공", Toast.LENGTH_SHORT ).show();
//            intent = new Intent( AddressInfoActivity.this, MainSelectActivity.class );
//            startActivity( intent );
//        }catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText( AddressInfoActivity.this, "Delete Error", Toast.LENGTH_SHORT ).show();
//        }
//    }

    // 명함 이미지 다운로드 내부저장소에 존재하면 가져오고 없으면 서버에서 가져온뒤 저장한다
    private void downloadImage() {
        pick_imageView1 = findViewById( R.id.pick_imageView1 );
        String filename = data.get( 0 ).getaImage1();
        try {
            String devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.androidlec.icontact/files/" + filename.trim() + ".jpg";
            File imgFile = new File( devicePath );

                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    pick_imageView1.setImageBitmap(myBitmap);
//                    Log.v( "DownloadImg", "inside-storage" );
                }else {
                    String urlAddr = "http://" + centIP + ":8080/icontact/" + filename + ".jpg";
//                    Log.v("urlAddr:",urlAddr);
                    ImageNetworkTask imageNetworkTask = new ImageNetworkTask( AddressInfoActivity.this, urlAddr, pick_imageView1 );
                    imageNetworkTask.execute( 100 );
//                    Log.v( "DownloadImg", "server-storage" );
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 명함 삭제 요청 메소드
    private void deleteAddress() {
        Intent intent;
        urlAddr = "";
        urlAddr = "http://"+centIP+":8080/addressProject/Address_query_delete.jsp?";  //아이피 받아서 넘기는 부분
        try {
            urlAddr = urlAddr + "aSeqno=" + seqno;
//            Log.v("TAG", String.valueOf(seqno));
//            Log.v("TAG", urlAddr);
            connetInsertData();
            intent = new Intent( AddressInfoActivity.this, MainSelectActivity.class );
            startActivity( intent );
        }catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( AddressInfoActivity.this, "Delete Error", Toast.LENGTH_SHORT ).show();
        }
    }

    // 명함 상세정보 요청 메소드
    private void connectGetData() {
        urlAddr = "http://" + centIP + ":8080/addressProject/Address_query_select.jsp?";
        urlAddr = urlAddr + "aSeqno=" + seqno;
        try{
            SelectNetworkTask selectNetworkTask = new SelectNetworkTask( AddressInfoActivity.this, urlAddr );
            Object obj = selectNetworkTask.execute( ).get( );
            data = (ArrayList<AddressDto>) obj;
            setAllTextView();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    // 명함 수정 요청 메소드
    private void connetInsertData(){
        try {                                                                       //어디에주는지
            NetworkTask networkTask = new NetworkTask(AddressInfoActivity.this, urlAddr);
            networkTask.execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}//------ END