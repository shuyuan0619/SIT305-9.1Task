package com.example.lostandfound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    public MapView mMapView;
    public BaiduMap mBaiduMap;
    public Button location_button_b;
    public LocationClient mLocationClient;
    public boolean location_flag = true;

    private DatabaseHelper dbHelper;
    public List<ItemBean> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SDKInitializer.setAgreePrivacy(getApplicationContext(),true);
        SDKInitializer.initialize(getApplicationContext());
        SDKInitializer.setCoordType(CoordType.wgs84);
        mLocationClient.setAgreePrivacy(true);
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_map);
        mMapView = (MapView)findViewById(R.id.bmapView);
        location_button_b = findViewById(R.id.location_button_b);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); 
        option.setCoorType("wgs84"); 
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        MyLocationListener myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        dbHelper = new DatabaseHelper(this,"LocalDatabase.db",null,1);
        getData();

        location_button_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationClient.start();
            }
        });
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MapActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (! permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this,permissions,1);
        } else {
            iniiView();
        }


    }

    public void iniiView(){
        GeoCoder geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                LatLng ll = geoCodeResult.getLocation();
                LatLng point = new LatLng(ll.latitude, ll.longitude);
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_loc);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                mBaiduMap.addOverlay(option);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        for(int i=0;i<list.size();i++){
            String addr = list.get(i).location;
            String[] detailAddr = addr.split("-");
            GeoCodeOption geoCodeOption = new GeoCodeOption().address(addr).city(detailAddr[0]);

            geoCoder.geocode(geoCodeOption);
        }




    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mMapView == null){
                return;
            }
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            Log.e("TAG", "onReceiveLocation: "+location.getLatitude());
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (location_flag) {
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(update);
                location_flag=false;
            }

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
            MyLocationConfiguration.LocationMode locationMode = MyLocationConfiguration.LocationMode.NORMAL;
            MyLocationConfiguration configuration = new MyLocationConfiguration(
                    locationMode,true,bitmapDescriptor
            );
            mBaiduMap.setMyLocationConfiguration(configuration);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
    }

    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        Log.e("TAG", "onDestroy: "+"??????" );
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this,"you have to give app the access to use",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    iniiView();
                } else {
                    Toast.makeText(this,"something wrong",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }



    @SuppressLint("Range")
    public void getData(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Item",null,null, null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                ItemBean bean = new ItemBean();
                bean.u_id =cursor.getString(cursor.getColumnIndex("u_id"));
                bean.name =cursor.getString(cursor.getColumnIndex("name"));
                bean.phone =cursor.getString(cursor.getColumnIndex("phone"));
                bean.description =cursor.getString(cursor.getColumnIndex("description"));
                bean.date =cursor.getString(cursor.getColumnIndex("date"));
                bean.location =cursor.getString(cursor.getColumnIndex("location"));
                bean.type =cursor.getString(cursor.getColumnIndex("type"));
                list.add(bean);
            }while(cursor.moveToNext());
        }


    }
}