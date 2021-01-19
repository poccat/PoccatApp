package com.example.forcatapp.Chat;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;


import com.example.forcatapp.Main.FollowFragment;
import com.example.forcatapp.Main.MainFragment;
import com.example.forcatapp.Main.WriteFragment;
import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
import com.example.forcatapp.util.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ChatActivity extends AppCompatActivity {

    private FragmentStateAdapter pagerAdapter;

    private TabLayout tabLayout;

    private static final int ACTIVITY_NUMBER = 3;

    private Context mContext = ChatActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupBottomNavigationView();
        setupViewPager();
    }


    private void setupViewPager() {
        //뷰페이저의 페이지가 바뀔떄마다 프래그먼트를 갱신하기

        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PeopleFragment());
        adapter.addFragment(new FriendFragment());
        adapter.addFragment(new ChatFragment());


        ViewPager viewPager = findViewById(R.id.chat_container);
        viewPager.setAdapter(adapter);


        tabLayout = findViewById(R.id.chat_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_people_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_friend);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
        tabLayout.getTabAt(1).select();

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

}