package com.example.forcatapp.Group;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class GroupActivity extends AppCompatActivity {
    private static final int ACTIVITY_NUMBER = 0;
    private Context mContext = GroupActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setupBottomNavigationView();
        getSupportFragmentManager().beginTransaction().replace(R.id.group_center,new GroupListFragment()).commit();
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

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.group_center,fragment).commit();
    }
}