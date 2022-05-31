package com.example.lostandfound;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.UUID;

public class AddAdvertActivity extends AppCompatActivity{

    public String type;
    public EditText et_name;
    public Button get_locate;
    private DatabaseHelper dbHelper;
    public RadioGroup radioGroup;
    public RadioButton lost_button;
    public RadioButton btn_found;
    public LocationClient mLocationClient;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public EditText et_phone_number;
    public EditText et_description;
    public EditText et_date;
    public TextView text_location;
    public Button save_button;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_advert);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ActionBar actionBar = getSupportActionBar();
        if(!(actionBar==null)){
            actionBar.hide();
        }

        mLocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); 
        option.setCoorType("wgs84");
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                Log.e("TAG", "onActivityResult: " + data.getStringExtra("addr"));
                text_location.setText(data.getStringExtra("addr"));
            }
        });

        et_name = findViewById(R.id.et_name);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_description = findViewById(R.id.et_description);
        et_date = findViewById(R.id.et_date);
        text_location = findViewById(R.id.text_location);
        save_button = findViewById(R.id.save_button);
        get_locate = findViewById(R.id.get_locate);
        radioGroup = findViewById(R.id.radioGroup);
        lost_button = findViewById(R.id.lost_button);
        btn_found = findViewById(R.id.btn_found);

        dbHelper = new DatabaseHelper(this,"LocalDatabase.db",null,1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == lost_button.getId()){
                    type = lost_button.getText().toString();
                }
                if(i == btn_found.getId()){
                    type = btn_found.getText().toString();
                }
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                String u_id = UUID.randomUUID().toString();
                String name = et_name.getText().toString();
                String phone = et_phone_number.getText().toString();
                String description = et_description.getText().toString();
                String date = et_date.getText().toString();
                String location = text_location.getText().toString();
                contentValues.put("u_id",u_id);
                contentValues.put("name",name);
                contentValues.put("phone",phone);
                contentValues.put("description",description);
                contentValues.put("date",date);
                contentValues.put("location",location);
                contentValues.put("type",type);
                db.insert("Item",null,contentValues);
                Toast.makeText(AddAdvertActivity.this,"Add successfully",Toast.LENGTH_SHORT).show();
                finish();

            }
        });

        get_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationClient.start();
            }
        });

        text_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddAdvertActivity.this,SugActivity.class);
                activityResultLauncher.launch(i);
            }
        });
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null )
            {
                return;
            }
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            GeoCoder geoCoder = GeoCoder.newInstance();
            ReverseGeoCodeOption options = new ReverseGeoCodeOption().location(ll);
            geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                @Override
                public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                }

                @Override
                public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

                    String addr1 = reverseGeoCodeResult.getAddress();
                    String addr2 = addr1.substring(3,addr1.length());
                    String addr3 = addr2.substring(0,3) + "-" + addr2.substring(3,addr2.length());
                    text_location.setText(addr3);
                }
            });
            geoCoder.reverseGeoCode(options);


        }
    }

}