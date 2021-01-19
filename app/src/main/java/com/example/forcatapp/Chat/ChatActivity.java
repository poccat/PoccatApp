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


import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
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

    public void setTabLayoutImage(){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_people_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_friend);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
    }

    private void setupViewPager() {
        //뷰페이저의 페이지가 바뀔떄마다 프래그먼트를 갱신하기

       FragmentPagerAdapter adapter = new Adapter(getSupportFragmentManager());


        ViewPager viewPager = findViewById(R.id.chat_container);
        viewPager.setAdapter(adapter);


        tabLayout = findViewById(R.id.chat_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(1).select();
        setTabLayoutImage();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
                setTabLayoutImage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

   class Adapter extends FragmentPagerAdapter {
        public Adapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return new PeopleFragment();
            }
            else if(position==1){
                return new FriendFragment();
            }
            else{
                return new ChatFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
//        @Override
//        public int getItemPosition(Object object){
//            return POSITION_NONE;
//        }
    }
}