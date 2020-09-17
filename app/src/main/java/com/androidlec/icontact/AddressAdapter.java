package com.androidlec.icontact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidlec.icontact.Activity.AddressInfoActivity;
import com.androidlec.icontact.Dto.AddressDto;
import com.androidlec.icontact.NetworkTask.ImageNetworkTask;

import java.io.File;
import java.util.ArrayList;

public class AddressAdapter extends BaseAdapter {

    private Context mContext = null;
    private int layout = 0;
    private ArrayList<AddressDto> data = null;
    private ArrayList<String> filenames = null;
    private ArrayList<ImageView> viewIds = null;
    private LayoutInflater inflater = null;
    ImageView iv_selected_contact;
    String centIP;

    public AddressAdapter(Context mContext, int layout, ArrayList<AddressDto> data, String centIP) {
        this.mContext = mContext;
        this.layout = layout;
        this.data = data;
        this.centIP = centIP;
        this.filenames = new ArrayList<>();
        this.viewIds = new ArrayList<>();
        this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }



    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position).getaName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = inflater.inflate(this.layout, parent, false);
        }

//        Log.v("MemberAdapter name", data.get(position).getaName());
//        Log.v("MemberAdapter job", data.get(position).getaJob());
//        Log.v("MemberAdapter company", data.get(position).getaCompany());

        TextView tv_selected_name = convertView.findViewById(R.id.tv_selected_name);
//        TextView tv_selected_job_dept = convertView.findViewById(R.id.tv_selected_job_dept);
        TextView tv_selected_company = convertView.findViewById(R.id.tv_selected_company);
        TextView tv_selected_tel = convertView.findViewById(R.id.tv_selected_tel);

        tv_selected_name.setText(data.get(position).getaName());
//        tv_selected_job_dept.setText(data.get(position).getaJob() + " / " + data.get(position).getaDepartment());
        tv_selected_company.setText(data.get(position).getaCompany());
        tv_selected_tel.setText(data.get(position).getaTel());

        return convertView;
    }

//    public void setContactImages() {
//        for(int step=1; step<filenames.size()+1; step++) {
//            ImageView iv_selected_contact = viewIds.get( step );
//            if( filenames.get( step ).equals( "" ) != true ) {
//                try {
//                    String devicePath = Environment.getDataDirectory().getAbsolutePath() + "/data/com.androidlec.icontact/files/" + filenames.get( step ).trim() + ".jpg";
//                    File imgFile = new File( devicePath );
//
//                    if (imgFile.exists()) {
//                        Bitmap myBitmap = BitmapFactory.decodeFile( imgFile.getAbsolutePath() );
//                        iv_selected_contact.setImageBitmap( myBitmap );
////                    Log.v( "DownloadImg", "inside-storage" );
//                    } else {
//                        String urlAddr = "http://" + centIP + ":8080/icontact/" + filenames.get( step ) + ".jpg";
////                    Log.v("urlAddr:",urlAddr);
//                        ImageNetworkTask imageNetworkTask = new ImageNetworkTask( mContext, urlAddr, iv_selected_contact );
//                        imageNetworkTask.execute( 100 );
////                    Log.v( "DownloadImg", "server-storage" );
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else {
//                iv_selected_contact.setImageResource( R.drawable.contact_default );
//            }
//        }
//
//
//    }


}//--------------
