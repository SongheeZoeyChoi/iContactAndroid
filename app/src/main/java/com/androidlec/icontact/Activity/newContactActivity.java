/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androidlec.icontact.Activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.androidlec.icontact.ConnectFTP;
import com.androidlec.icontact.CustomProgressDialog;
import com.androidlec.icontact.Login.GlobalApplication;
import com.androidlec.icontact.NetworkTask.ImageNetworkTask;
import com.androidlec.icontact.NetworkTask.InsNetworkTask;
import com.androidlec.icontact.PackageManagerUtils;
import com.androidlec.icontact.PermissionUtils;
import com.androidlec.icontact.R;
import com.androidlec.icontact.STATICDATA;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class newContactActivity extends AppCompatActivity {
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDCt_F4pjPhTIK9iyx8oeh6J5oaYyhcxE8";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;


    private static final String TAG = newContactActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    private static int imageNo=0;
    private static int EditTextNo=0;
    private static int loadtext=2;
    private int sw;

    private int aSeqno;
    private String aName;
    private String aMobile;
    private String aEmail;
    private String aCompany;
    private String aDept;
    private String aJob;
    private String aTel;
    private String aAddress;
    private String aImage;
    private static String editText="";
//    private String centIP = "182.230.84.233";
    private String centIP = "192.168.0.113";

    private static String[] textArray=null;
    private TextView name,mobile,email,company,dept,job,telno,address;
    private ImageView image1,image2, btn_newcontact_back;
    private TextView confirm, edit_confirm, tv_newContact_Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.newcontact);
        getContactData();

        // ID등록
        btn_newcontact_back=findViewById(R.id.btn_newcontact_back);
        confirm=findViewById(R.id.add_confirm);
        edit_confirm=findViewById(R.id.edit_confirm);
        image1 =findViewById(R.id.insert_image1);
        image2 =findViewById(R.id.insert_image2);
        name=findViewById(R.id.et_name);
        mobile=findViewById(R.id.et_mobile);
        email=findViewById(R.id.et_email);
        company=findViewById(R.id.et_company);
        dept=findViewById(R.id.et_dept);
        job=findViewById(R.id.et_job);
        telno=findViewById(R.id.et_telno);
        address=findViewById(R.id.et_address);
        tv_newContact_Title=findViewById( R.id.tv_newContact_Title );

        // 클릭 리스너 등록
        btn_newcontact_back.setOnClickListener(click);
        confirm.setOnClickListener(click);
        edit_confirm.setOnClickListener(click);
        image1.setOnClickListener(click);
        image2.setOnClickListener(click);
        name.setOnClickListener(click);
        mobile.setOnClickListener(click);
        email.setOnClickListener(click);
        company.setOnClickListener(click);
        dept.setOnClickListener(click);
        job.setOnClickListener(click);
        telno.setOnClickListener(click);
        address.setOnClickListener(click);

        // 롱클릭 리스너 등록
        name.setOnLongClickListener(longClick);
        mobile.setOnLongClickListener(longClick);
        email.setOnLongClickListener(longClick);
        company.setOnLongClickListener(longClick);
        dept.setOnLongClickListener(longClick);
        job.setOnLongClickListener(longClick);
        telno.setOnLongClickListener(longClick);
        address.setOnLongClickListener(longClick);


        Log.v( "sw", String.valueOf( sw ) );
        if(sw == 0) { // 등록 화면
            confirm.setVisibility( View.VISIBLE );
            edit_confirm.setVisibility( View.GONE );
        }else { // 편집 화면
            downloadImage( aImage );
            tv_newContact_Title.setVisibility( View.INVISIBLE );
            confirm.setVisibility( View.GONE );
            edit_confirm.setVisibility( View.VISIBLE );
        }
        setEditText();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            AlertDialog.Builder builder = new AlertDialog.Builder(newContactActivity.this);

            confirm=findViewById(R.id.add_confirm);
            image1 =findViewById(R.id.insert_image1);
            image2 =findViewById(R.id.insert_image2);
            name=findViewById(R.id.et_name);
            mobile=findViewById(R.id.et_mobile);
            email=findViewById(R.id.et_email);
            company=findViewById(R.id.et_company);
            dept=findViewById(R.id.et_dept);
            job=findViewById(R.id.et_job);
            telno=findViewById(R.id.et_telno);
            address=findViewById(R.id.et_address);

            switch (view.getId()){

                case R.id.insert_image1:
                    builder
                            .setMessage(R.string.dialog_select_prompt)
                            .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                            .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
                    builder.create().show();
                    imageNo=1;
                    break;

                case R.id.insert_image2:
                    builder
                            .setMessage(R.string.dialog_select_prompt)
                            .setPositiveButton(R.string.dialog_select_gallery, (dialog, which) -> startGalleryChooser())
                            .setNegativeButton(R.string.dialog_select_camera, (dialog, which) -> startCamera());
                    builder.create().show();
                    imageNo=2;
                    break;

                case R.id.add_confirm:
                    if(name.getText().toString().equals("")){
                        Toast.makeText(newContactActivity.this, "이름을 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }else if((mobile.getText().toString().equals("")&&email.getText().toString().equals(""))){
                        Toast.makeText(newContactActivity.this, "휴대전화나 이메일을 입력하기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }else if(STATICDATA.FTPUPLOAD==0){
//                        Toast.makeText(newContactActivity.this,"이미지 업로드중&실패", Toast.LENGTH_LONG).show();
                    }else{
//                        Toast.makeText( newContactActivity.this,"이미지 업로드 성공",Toast.LENGTH_LONG ).show();
                        textArray=null;
                        inserContact();
                    }
                    break;

                case R.id.btn_newcontact_back:
                    onBackPressed();
                    break;

                case R.id.edit_confirm:
                    textArray=null;
                    updateAddress();
                    break;

                case R.id.et_name:
                    EditTextNo=1;
                    showEditDialog();
                    break;

                case R.id.et_mobile:
                    EditTextNo=2;
                    showEditDialog();
                    break;

                case R.id.et_email:
                    EditTextNo=3;
                    showEditDialog();
                    break;

                case R.id.et_company:
                    EditTextNo=4;
                    showEditDialog();
                    break;

                case R.id.et_dept:
                    EditTextNo=5;
                    showEditDialog();
                    break;

                case R.id.et_job:
                    EditTextNo=6;
                    showEditDialog();
                    break;

                case R.id.et_telno:
                    EditTextNo=7;
                    showEditDialog();
                    break;

                case R.id.et_address:
                    EditTextNo=8;
                    showEditDialog();
                    break;
            }
        }
    };

    View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            name=findViewById(R.id.et_name);
            mobile=findViewById(R.id.et_mobile);
            email=findViewById(R.id.et_email);
            company=findViewById(R.id.et_company);
            dept=findViewById(R.id.et_dept);
            job=findViewById(R.id.et_job);
            telno=findViewById(R.id.et_telno);
            address=findViewById(R.id.et_address);
            switch (view.getId()){

                case R.id.et_name:
                    EditTextNo=1;
                    editText=name.getText().toString();
                    showEditText();
                    break;

                case R.id.et_mobile:
                    EditTextNo=2;
                    editText=mobile.getText().toString();
                    showEditText();
                    break;

                case R.id.et_email:
                    EditTextNo=3;
                    editText=email.getText().toString();
                    showEditText();
                    break;

                case R.id.et_company:
                    EditTextNo=4;
                    editText=company.getText().toString();
                    showEditText();
                    break;

                case R.id.et_dept:
                    EditTextNo=5;
                    editText=dept.getText().toString();
                    showEditText();
                    break;

                case R.id.et_job:
                    EditTextNo=6;
                    editText=job.getText().toString();
                    showEditText();
                    break;

                case R.id.et_telno:
                    EditTextNo=7;
                    editText=telno.getText().toString();
                    showEditText();
                    break;

                case R.id.et_address:
                    EditTextNo=8;
                    editText=address.getText().toString();
                    showEditText();
                    break;

            }
            return false;
        }
    };

///////////////////////////////////////////////   07-11 YH

    private void getContactData() {
        Intent intent = getIntent();
        aSeqno = intent.getIntExtra("aSeqno",-1);
        aName = intent.getStringExtra("aName");
        aMobile = intent.getStringExtra("aMobile");
        aEmail = intent.getStringExtra("aEmail");
        aCompany = intent.getStringExtra("aCompany");
        aDept = intent.getStringExtra("aDept");
        aJob = intent.getStringExtra("aJob =");
        aTel = intent.getStringExtra("aTel");
        aAddress = intent.getStringExtra("aAddress");
        aImage = intent.getStringExtra("aImage");
        if(aSeqno != -1) {
            sw = 1;
        } else {
            sw = 0;
        }

    }

    private void setEditText() {
        name.setText(aName);
        mobile.setText(aMobile);
        email.setText(aEmail);
        company.setText(aCompany);
        dept.setText(aDept);
        job.setText(aJob);
        telno.setText(aTel);
        address.setText(aAddress);
    }
///////////////////////////////////////////////

    private void showEditText(){
        AlertDialog.Builder builder = new AlertDialog.Builder(newContactActivity.this);
        LinearLayout layout =(LinearLayout) View.inflate(newContactActivity.this,R.layout.edit_dialog,null);
        EditText editText_input = layout.findViewById(R.id.input_data);

        name=findViewById(R.id.et_name);
        mobile=findViewById(R.id.et_mobile);
        email=findViewById(R.id.et_email);
        company=findViewById(R.id.et_company);
        dept=findViewById(R.id.et_dept);
        job=findViewById(R.id.et_job);
        telno=findViewById(R.id.et_telno);
        address=findViewById(R.id.et_address);

        editText_input.setText(editText);

        builder
                .setView(layout)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String string = editText_input.getText().toString();

                        switch(EditTextNo){
                            case 1:
                                name.setText(string);
                                break;
                            case 2:
                                mobile.setText(string.replaceAll("[^0-9]", ""));
                                break;
                            case 3:
                                email.setText(string);
                                break;
                            case 4:
                                company.setText(string);
                                break;
                            case 5:
                                dept.setText(string);
                                break;
                            case 6:
                                job.setText(string);
                                break;
                            case 7:
                                telno.setText(string.replaceAll("[^0-9]", ""));
                                break;
                            case 8:
                                address.setText(string);
                                break;

                        }
                    }
                })
                .show();

    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(newContactActivity.this);
        name=findViewById(R.id.et_name);
        mobile=findViewById(R.id.et_mobile);
        email=findViewById(R.id.et_email);
        company=findViewById(R.id.et_company);
        dept=findViewById(R.id.et_dept);
        job=findViewById(R.id.et_job);
        telno=findViewById(R.id.et_telno);
        address=findViewById(R.id.et_address);

        switch (loadtext){
            case 0:
                Toast.makeText(newContactActivity.this,"텍스트가 로드중입니다.", Toast.LENGTH_LONG).show();
                break;
            case 1:
                builder
                        .setItems(textArray, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (EditTextNo){
                                    case 1:
                                        name.setText(textArray[i]);
                                        break;
                                    case 2:
                                        String strPhone = textArray[i];
                                        String phone= strPhone.replaceAll("[^0-9]", "");
                                        mobile.setText(phone);
                                        break;
                                    case 3:
                                        email.setText(textArray[i]);
                                        break;
                                    case 4:
                                        company.setText(textArray[i]);
                                        break;
                                    case 5:
                                        dept.setText(textArray[i]);
                                        break;
                                    case 6:
                                        job.setText(textArray[i]);
                                        break;
                                    case 7:
                                        String strTel = textArray[i];
                                        Log.v("string",strTel);
                                        String tel= strTel.replaceAll("[^0-9]", "");
                                        Log.v("string",tel);
                                        telno.setText(tel);
                                        break;
                                    case 8:
                                        address.setText(textArray[i]);
                                        break;
                                }

                            }
                        })
                        .show();
                break;
            case 2:
                showEditText();
                break;
        }
    }

    public void startGalleryChooser() {
        if (PermissionUtils.requestPermission(this, GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction( Intent.ACTION_GET_CONTENT);
            startActivityForResult( Intent.createChooser(intent, "사진을 선택하세요."),
                    GALLERY_IMAGE_REQUEST);
        }
    }

    public void startCamera() {
        if (PermissionUtils.requestPermission(
                this,
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra( MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(photoUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
                    startCamera();
                }
                break;
            case GALLERY_PERMISSIONS_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
                    startGalleryChooser();
                }
                break;
        }
    }

    // 명함 이미지 다운로드 내부저장소에 존재하면 가져오고 없으면 서버에서 가져온뒤 저장한다
    private void downloadImage(String fileName){
        Log.v( "Status", "downloadImage()" );
        image1 = findViewById( R.id.insert_image1 );
        try {
            String devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.androidlec.icontact/files/" + fileName.trim() + ".jpg";
            File imgFile = new File( devicePath );
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    image1.setImageBitmap(myBitmap);
                    Log.v( "DownloadImg", "inside-storage" );
                }else {
                    String urlAddr = "http://" + centIP + ":8080/icontact/" + fileName + ".jpg";
                    ImageNetworkTask imageNetworkTask = new ImageNetworkTask( newContactActivity.this, urlAddr, image1 );
                    imageNetworkTask.execute( 100 );
                    Log.v( "DownloadImg", "server-storage" );
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void uploadImage(Uri uri) {

        loadtext=0;
        ConnectFTP connectFTP = new ConnectFTP(newContactActivity.this, centIP, "ftpuser", "1234", 21, uri);
        connectFTP.execute();

        switch (imageNo){
            case 1:
                if (uri != null) {
                    try {
                        // scale the image to save on bandwidth
                        Bitmap bitmap =
                                scaleBitmapDown(
                                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                        MAX_DIMENSION);

                        Log.v("uri",uri.toString());


                        callCloudVision(bitmap);
                        image1.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        Log.d(TAG, "Image picking failed because " + e.getMessage());
                        Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "Image picker gave us a null image.");
                    Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
                }
                break;
            case 2:
                if (uri !=null) {
                    try {
                        Bitmap bitmap = scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                MAX_DIMENSION);
                        image2.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress( Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("TEXT_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<newContactActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;
        private  CustomProgressDialog cProgressDialog;
        private Context context;

        LableDetectionTask(newContactActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
            this.context = activity;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        @Override
        protected void onPreExecute() {
            cProgressDialog = new CustomProgressDialog( context );
            cProgressDialog.setCancelable( false );
            cProgressDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
            cProgressDialog.setDialogMessage( "명함을 스캔하고 있습니다" );
            cProgressDialog.show();

            // 업로드중 다이어로그 메시지 변경
            final Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable()  {
                int count = 0;
                public void run() {
                    count++;
                    if(count == 1)
                        cProgressDialog.setDialogMessage("텍스트를 읽는중..");
                    else if(count == 2)
                        cProgressDialog.setDialogMessage("잠시만 기다려주세요");
                    mHandler.postDelayed(this, 2000);
                }
            }, 2000);

        }

        protected void onPostExecute(String result) {
            newContactActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                loadtext=1;
                cProgressDialog.dismiss();
                Toast.makeText(activity, "텍스트를 모두 읽어왔습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations();
        if (labels != null) {
            textArray = labels.get(0).getDescription().split("\n");
        } else {
            Log.v("text","nothing");
        }
        return null;
    }

    private void inserContact(){
        name=findViewById(R.id.et_name);
        mobile=findViewById(R.id.et_mobile);
        email=findViewById(R.id.et_email);
        company=findViewById(R.id.et_company);
        dept=findViewById(R.id.et_dept);
        job=findViewById(R.id.et_job);
        telno=findViewById(R.id.et_telno);
        address=findViewById(R.id.et_address);

        String strName = name.getText().toString();
        String strMobile = mobile.getText().toString();
        String strEmail = email.getText().toString();
        String strCompany = company.getText().toString();
        String strDept = dept.getText().toString();
        String strJob = job.getText().toString();
        String strTelno = telno.getText().toString();
        String strAddress = address.getText().toString();


        String urlAddr = "";
        // GlobalApplication 에서 kakao email을 가져옴.
        GlobalApplication globalApplication = GlobalApplication.getGlobalApplicationContext();
        String kemail = globalApplication.getUserEmail();

        String imageName = STATICDATA.FILENAME;

        urlAddr = "http://" + centIP + ":8080/addressProject/Address_query_insert.jsp?"; // //JSP 의 겟방식(=?.이런거) 으로 url주소 구성 하면 이따가 requestGetparameter이런걸로 받음

        urlAddr = urlAddr + "name=" + strName + "&mobile=" + strMobile + "&email=" + strEmail  //주소에 ? 있는지 확인하고 / 겟방식에서는 스페이스 없어야함 // 처음이후에는& 으로 붙여줌
                + "&company=" + strCompany + "&department=" + strDept + "&job=" + strJob + "&tel=" + strTelno + "&address=" + strAddress + "&image1=" + imageName + "&kemail=" + kemail;

        Log.v("url",urlAddr);
        ///////////여기까지 jsp에서 구동할 겟방식 다 만듦

        connectInsertData(urlAddr); //메소드만듦
        Toast.makeText(newContactActivity.this, kemail + "의 주소록에" + name + "님이 입력되었습니다.", Toast.LENGTH_SHORT).show();

        STATICDATA.FTPUPLOAD=0;
    }

    private void updateAddress() {
        Intent intent;
        String urlAddr;

        String aName = name.getText().toString();
        String aMoblie = mobile.getText().toString();
        String aEmail = email.getText().toString();
        String aCompany = company.getText().toString();
        String aDepartment = dept.getText().toString();
        String aJob = job.getText().toString();
        String aTel = telno.getText().toString();
        String aAddress = address.getText().toString();

        String imageName = STATICDATA.FILENAME;

        urlAddr = "http://" + centIP + ":8080/addressProject/Address_query_update.jsp?";  //아이피 받아서 넘기는 부분
        urlAddr = urlAddr + "aName=" + aName + "&aMoblie=" + aMoblie + "&aEmail=" + aEmail + "&aCompany=" + aCompany + "&aDepartment=" +
                aDepartment + "&aJob=" + aJob + "&aTel=" + aTel + "&aAddress=" + aAddress + "&aImage=" +imageName+ "&aSeqno=" + aSeqno;
        Log.v( "URL", urlAddr );
        try {
            connectInsertData(urlAddr);
            intent = new Intent( newContactActivity.this, MainSelectActivity.class );
            startActivity( intent );
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText( newContactActivity.this, "Update Error", Toast.LENGTH_SHORT ).show();
        }
    }

    private void connectInsertData(String urlAddr){
        try {
            InsNetworkTask insNetworkTask = new InsNetworkTask(newContactActivity.this, urlAddr); // 생성자를 만들거야 라고 정의해서쓰고 만들고오면 에러없어짐 /
            insNetworkTask.execute().get();
            startActivity( new Intent( newContactActivity.this, MainSelectActivity.class ) );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
