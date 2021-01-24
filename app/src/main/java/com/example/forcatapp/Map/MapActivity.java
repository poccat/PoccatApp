package com.example.forcatapp.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
import com.example.forcatapp.util.OracleDBUpload;
import com.example.forcatapp.util.UniversalImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private GoogleMap googleMap;
    private static final int ACTIVITY_NUMBER = 4;

    private Context mContext = MapActivity.this;

    List<Map<String, Object>> mapList;
    private String name;
    OracleDBUpload oracleDBUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //맵 정보 받아오기
        oracleDBUpload = new OracleDBUpload("getMapInfo", mContext);
        oracleDBUpload.execute();
        try {
            oracleDBUpload.get();
            this.mapList = oracleDBUpload.resultList;
            Log.d(TAG, "onCreate: mapList ===>" + mapList);

            mapFragment.getMapAsync(this);     //여기에 두기
            setupBottomNavigationView();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng currentLocation = new LatLng(LocationActivity.latitude, LocationActivity.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        MarkerOptions markerOptions = new MarkerOptions().position(currentLocation);
        Log.d(TAG, "onMapReady: mapList" + mapList);
        //리스트 크기만큼 고양이 마커 추가
        for (int i = 0; i < mapList.size(); i++) {
            Log.d(TAG, "create" + String.valueOf(i));
            double cat_latitude = (double) mapList.get(i).get("CAT_LATITUDE");
            double cat_longitude = (double) mapList.get(i).get("CAT_LONGITUDE");
            int cat_no = Integer.parseInt(String.valueOf(mapList.get(i).get("CAT_NO")));
            Log.d(TAG, "onMapReady: catname" + cat_no);
            //위치설정
            LatLng latLng = new LatLng(cat_latitude, cat_longitude);
            MarkerOptions marker_cat = new MarkerOptions();
            marker_cat.position(latLng);
            marker_cat.title(String.valueOf(cat_no));
            googleMap.addMarker(marker_cat);
        }
        // 다이얼로그
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "onMarkerClick: marker.getTitle()" + marker.getTitle());
                String marker_number = null;
                String cat_photo = null;
                String cat_cmt = null;
                String cat_name = null;

                for (int i = 0; i < mapList.size(); i++) {
                    Log.d(TAG, "onMarkerClick: (mapList.get(i).get(\"CAT_NO\")" + (mapList.get(i).get("CAT_NO")));
                    Log.d(TAG, "onMarkerClick: " + marker.getTitle());
                    if (mapList.get(i).get("CAT_NO").toString().equals(marker.getTitle())) {
                        Log.d(TAG, "onMarkerClick: equals? " + mapList.get(i).get("CAT_NO").toString().equals(marker.getTitle()));
                        marker_number = String.valueOf(mapList.indexOf(mapList.get(i)));
                        Log.d(TAG, "marker_number " + marker_number);

                        break;
                    }
                }
                final int marker_ID_number = Integer.parseInt(marker_number);
                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                Log.d(TAG, "marker clinic name = " + mapList.get(marker_ID_number).get("CAT_NAME"));

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("고양이 정보");

                LayoutInflater inflater
                        = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.fragment_dialog, null);

                cat_photo = (String) mapList.get(marker_ID_number).get("CAT_PHOTO");
                cat_cmt = (String) mapList.get(marker_ID_number).get("CAT_CMT");
                cat_name = (String) mapList.get(marker_ID_number).get("CAT_NAME");
                Log.d(TAG, "onMarkerClick: cat_photo ::" + cat_photo);
                Log.d(TAG, "onMarkerClick: cat_name ::" + cat_name);
                Log.d(TAG, "onMarkerClick: cat_cmt ::" + cat_cmt);

                TextView tv_cat_name = layout.findViewById(R.id.cat_name);
                TextView tv_cat_comment = layout.findViewById(R.id.tv_comment);
                tv_cat_name.setText(cat_name);
                tv_cat_comment.setText(cat_cmt);

                // UniversalImageLoader로 이미지 표시하기
                ImageView iv_catphoto = layout.findViewById(R.id.iv_cat_photo);
                String imageUrl = "192.168.0.51:9005/" + cat_photo;
                UniversalImageLoader.setImage(imageUrl, iv_catphoto, null, "http://");

                builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                Button btn_close = layout.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                Button btn_profile = layout.findViewById(R.id.btn_goProfile);
                btn_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //웹뷰로 고양이 프로필 열기

                    }
                });

                //new DialogFragment(cat_name, myUri, "고양이", cat_no, cat_cmt).show(getSupportFragmentManager(),"");
                return false;
            }
        });

        /*
        //리스트 크기만큼 급식소 마커 추가
        for(int i = 0 ; i < mapList.size(); i++) {
            //급식소
            double meal_longitude = (double) mapList.get(i).get("MEAL_LONGITUDE");
            double meal_latitude = (double) mapList.get(i).get("MEAL_LATITUDE");
            int meal_no = Integer.parseInt(String.valueOf(mapList.get(i).get("MEAL_NO")));
            String meal_photo = (String) mapList.get(i).get("MEAL_PHOTO");
            String meal_cmt = (String) mapList.get(i).get("MEAL_CMT");
            String meal_food = (String) mapList.get(i).get("MEAL_FOOD");
            String meal_water = (String) mapList.get(i).get("MEAL_WATER");
            String meal_date = (String) mapList.get(i).get("MEAL_DATE");

            //위치설정
            LatLng latLng2 = new LatLng(meal_latitude, meal_longitude);
            MarkerOptions marker_shelter = new MarkerOptions();
            marker_shelter.position(latLng2);
            googleMap.addMarker(marker_shelter);

            // 다이얼로그
            Uri myUri = Uri.parse("192.168.0.51:9005/" + meal_photo);
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    new DialogFragment("급식소 정보", myUri, "급식소 정보", meal_no, meal_cmt).show(getSupportFragmentManager(),"");
                    return false;
                }
            });
        }
        */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }

    }

    //하단바 설정
    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavBarHelper.setupBottomNavBarView(bottomNavigationViewEx);
        BottomNavBarHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }
}