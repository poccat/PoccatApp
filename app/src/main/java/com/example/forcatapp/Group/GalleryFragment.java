package com.example.forcatapp.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.Main.NextActivity;
import com.example.forcatapp.R;
import com.example.forcatapp.util.FilePath;
import com.example.forcatapp.util.FileSearch;
import com.example.forcatapp.util.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;

    //위젯
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    
    //전역변수선언
    private ArrayList<String> directories;
    private String mAppend = "file://";
    private String mSelectedImage;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = view.findViewById(R.id.galleryImageView);
        gridView = view.findViewById(R.id.gallerygridView);
        directorySpinner = view.findViewById(R.id.spinner_direc);
        mProgressBar = view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: 갤러리 오픈");
        
        //갤러리 닫기버튼 이벤트 처리
        ImageView writeClose = view.findViewById(R.id.iv_close);
        writeClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 갤러리 닫기");
                getActivity().finish();
            }
        });
        //갤러리 다음버튼 이벤트처리
        TextView nextScreen = view.findViewById(R.id.tv_next);
        nextScreen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 최종 글쓰기 창으로 이동");
                //mSelectedImage <- 선택한 이미지
                Intent intent = new Intent(getActivity(), NextActivity.class);
                intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                startActivity(intent);
            }
        });

        //하단 이미지 연결메소드 호출
        init();

        return view;
    }

    private void init(){
        FilePath filePaths = new FilePath();
        //이미지 있는 폴더들 체크
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);
        directories.add(filePaths.DOWNLOAD);

        //전체 폴더 경로에서 마지막 폴더 이름만 뽑아내기 /storage/emulated/0/pictures -> /pictures
        ArrayList<String> directoryNames = new ArrayList<>();
        Log.d(TAG, "init: directoryNames ===>" + directoryNames);
        for(int i = 0; i < directories.size(); i++){
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }


        //스피너와 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);
        //스피너에서 선택한 폴더 받아오기
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: 갤러리에서 폴더 위치 선택 :: " + directories.get(position));
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: 선택된 폴더 :: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        Log.d(TAG, "setupGridView: imgURLs :: " + imgURLs);

        //그리드 가로길이 설정
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        // 그리드에 이미지 넣기 위해 그리드 아답터 사용
        GridImageAdapter gridImageAdapter = new GridImageAdapter(getActivity(), R.layout.layout_gird_imageview, mAppend, imgURLs);
        gridView.setAdapter(gridImageAdapter);
        
        // 상단 이미지뷰에 첫번째 이미지 설정
        if(imgURLs!=null && imgURLs.size()>0){
            if(imgURLs.get(0)!=null){ //이미지가 하나라도 있다면...
                setImage(imgURLs.get(0), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(0);
            }
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }


    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
