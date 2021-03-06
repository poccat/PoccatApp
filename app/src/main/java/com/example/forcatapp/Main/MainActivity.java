package com.example.forcatapp.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
import com.example.forcatapp.util.SectionPagerAdapter;
import com.example.forcatapp.util.UniversalImageLoader;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUMBER = 2;
    private Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: 앱 시작");
        passPushTokenToServer();
        initImageLoader(); //이미지 로더 실행메소드
        setupBottomNavigationView();
        setupViewPager();
    }
    /*
    카메라, 홈, 메시지 탭을 만들기 위해 필요한 메소드
     */
    private void setupViewPager(){
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MypageFragment());
        adapter.addFragment(new QRFragment());
        adapter.addFragment(new NonFollowFragment());
        adapter.addFragment(new MainFragment());
        adapter.addFragment(new WriteFragment());
        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        
        //상단 탭 아이콘 설정
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_mypage);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_qr);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_star);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_cat);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_write);
        tabLayout.getTabAt(3).select();
    }
    //하단바 설정
    private void setupBottomNavigationView(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavBarHelper.setupBottomNavBarView(bottomNavigationViewEx);
        BottomNavBarHelper.enableNavigation(mContext, bottomNavigationViewEx );
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }
    
    //이미지 로더 실행메소드
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }
    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);
        Toast.makeText(getApplicationContext(),""+token,Toast.LENGTH_LONG);
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);
    }




    }