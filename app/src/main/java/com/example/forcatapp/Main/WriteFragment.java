package com.example.forcatapp.Main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.forcatapp.R;
import com.example.forcatapp.util.Permissions;
import com.example.forcatapp.util.SectionPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class WriteFragment extends Fragment {
    private static final String TAG = "MessageFragment";

    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        //권한(카메라, 로컬저장소 사용, 위치정보)이 허용되어있는지 확인
        if(checkPermissionArray(Permissions.PERMESSIONS)){
            setupViewPager(view);
        } else{ //허용되어있지 않으면 여기서 허용
            verifyPermission(Permissions.PERMESSIONS);
        }

        return view;
    }

    /***
     * 현재 탭 번호를 리턴함
     * 0 = GalleryFragment
     * 1 = PhotoFragment
     * @return
     */
    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();
    }
    
    //하단 탭 설정
    private void setupViewPager(View view){
        Log.d(TAG, "setupViewPager: 글쓰기 페이지");
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new GalleryFragment());
        adapter.addFragment(new PhotoFragment());

        mViewPager = view.findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(R.string.gallery);
        tabLayout.getTabAt(1).setText(R.string.takePhoto);
        tabLayout.getTabAt(0).select();
    }

    public void verifyPermission(String[] permissions){
        Log.d(TAG, "verifyPermission: 권한 요청");
        //기기에 권한을 요청하는 메소드
        ActivityCompat.requestPermissions(
                WriteFragment.this.getActivity(),
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    /***
     * 여러 권한이 부여되어있는지 확인함
     * @param permissions
     * @return
     */
    public boolean checkPermissionArray(String[] permissions){
        Log.d(TAG, "checkPermissionArray: 여러 권한 확인중");
        for (int i = 0; i<permissions.length ; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                Log.d(TAG, "checkPermissionArray: 권한 없음!");
                return false;
            }
        }
        Log.d(TAG, "checkPermissionArray: 권한 확인됨");
        return true;
    }

    /***
     * 하나의 권한이 부여되어있는지 확인함
     * @param permisson
     * @return
     */
    public boolean checkPermissions(String permisson){
        int permissionRequest = ActivityCompat.checkSelfPermission(WriteFragment.this.getContext(), permisson);
        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: 권한 없음");
            return false;
        }
        else{
            return true;
        }
    }

}
