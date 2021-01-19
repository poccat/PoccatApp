package com.example.forcatapp.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.forcatapp.Board.BoardActivity;
import com.example.forcatapp.Chat.ChatActivity;
import com.example.forcatapp.Group.GroupActivity;
import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.Map.LocationActivity;
import com.example.forcatapp.Map.MapActivity;
import com.example.forcatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavBarHelper {
    private static final String TAG = "BottomNavBarHelper";

    public static void setupBottomNavBarView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavBarView:");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_group:  //ACTIVITY_NUMBER = 0
                        Intent intent1 = new Intent(context, GroupActivity.class);
                        context.startActivity(intent1);
                        break;
                    case R.id.ic_board: //ACTIVITY_NUMBER = 1
                        Intent intent2 = new Intent(context, BoardActivity.class);
                        context.startActivity(intent2);
                        break;
                    case R.id.ic_cat:  //ACTIVITY_NUMBER = 2
                        Intent intent0 = new Intent(context, MainActivity.class);
                        //외부 클래스 객체에서 이동하기 때문에, 어디서부터 인텐트가 이동하는지 알 수 있도록 context가 필요하다.
                        context.startActivity(intent0);
                        break;
                    case R.id.ic_chat:  //ACTIVITY_NUMBER = 3
                        Intent intent3 = new Intent(context, ChatActivity.class);
                        context.startActivity(intent3);
                        break;
                    case R.id.ic_map:  //ACTIVITY_NUMBER = 4
                        Intent intent4 = new Intent(context, LocationActivity.class);
                        context.startActivity(intent4);
                        break;
                }

                return false;
            }
        });
    }
}
