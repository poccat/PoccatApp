package com.example.forcatapp.Board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.forcatapp.Chat.ChatActivity;
import com.example.forcatapp.Group.GroupActivity;
import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.Map.LocationActivity;
import com.example.forcatapp.R;
import com.example.forcatapp.util.BottomNavBarHelper;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BoardActivity extends AppCompatActivity {
    private static final String TAG = "BoardActivity";
    private static final int ACTIVITY_NUMBER = 1;
    private Context mContext = BoardActivity.this;
    static final String[] LIST_MENU = {"공지사항", "후원게시판", "나눔게시판", "입양후기", "봉사게시판"} ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU) ;

        ListView listview = (ListView) findViewById(R.id.lv_board) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get TextView's Text.
                String strText = (String) parent.getItemAtPosition(position);

                // 리스트뷰에서 선택한 게시판을 받아온다
                // 웹뷰 프래그먼트로 넘어가 해당 게시판페이지를 보여준다
                // 그러기위해서 switch-case로 웹뷰 주소를 설정한 후 넘긴다
                String url = "";
                int caseNum = 0;
                switch (position){
                    case 0:  //공지사항
                        url = "http://192.168.0.51:9005/secondB/common_board_list.foc?com_b_type=0";
                        break;
                    case 1: //후원게시판
                        url = "http://192.168.0.51:9005/secondB/donation_noti_list.foc";
                        caseNum = 1;
                        break;
                    case 2:  //나눔게시판
                        url = "http://192.168.0.51:9005/secondB/common_board_list.foc?com_b_type=3";
                        break;
                    case 3:  //입양후기
                        url = "http://192.168.0.51:9005/secondB/common_board_list.foc?com_b_type=2";
                        break;
                    case 4:  //봉사게시판
                        url = "http://192.168.0.51:9005/secondB/common_board_list.foc?com_b_type=1";
                        break;
                }

                //fragment 생성
                BoardFragment boardFragment = new BoardFragment();
                //가운데 레이아웃 웹뷰로 바꿔치기
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.relLayout2_middle, boardFragment)
                        //뒤로가기에 필요
                        .addToBackStack("boardList")
                        .commit();
                //번들객체 생성, text값 저장
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                bundle.putInt("caseNum", caseNum);
                // 번들 전달
                boardFragment.setArguments(bundle);
            }
        }) ;

        setupBottomNavigationView();
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