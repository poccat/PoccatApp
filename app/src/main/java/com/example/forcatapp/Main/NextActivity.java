package com.example.forcatapp.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.forcatapp.R;
import com.example.forcatapp.util.FirebaseMethods;
import com.example.forcatapp.util.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    private GridView gridView;
    private EditText et_content;
    private Toolbar toolbar;
    private Context mContext;
    private String mAppend = "file://";
    private int imageCount = 0;
    private String imgURL;
    private Spinner catNameSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        mContext = NextActivity.this;
        mFirebaseMethods = new FirebaseMethods(NextActivity.this); //context 전달
        et_content = (EditText) findViewById(R.id.et_content) ;
        catNameSpinner = findViewById(R.id.spinner_cats);

        Log.d(TAG, "onCreateView: stared.");

        setupFirebaseAuth();

        //글쓰기 최종 이전버튼 이벤트 처리
        ImageView backArrow = findViewById(R.id.ic_backArrow_next);
        backArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 글쓰기 최종 창 뒤로가기");
                finish();
            }
        });
        //글쓰기 최종 다음버튼 이벤트처리
        TextView share = findViewById(R.id.tv_share);
        share.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 최종 글쓰기 창으로 이동");
                //firebase DB에 글과 사진 업로드 시작=====================================================================================
                mFirebaseMethods.uploadNewPhoto("newPhoto", et_content.getText().toString(), imageCount, imgURL);
            }
        });

        //고양이 이름 받아오기
        ArrayList<String> catNames = new ArrayList<>();
        Log.d(TAG, "init: directoryNames ===>" + catNames);
        
        //일단 상수
        catNames.add("야옹이1");
        catNames.add("여기에 없어요");
        
        //스피너와 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, catNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catNameSpinner.setAdapter(adapter);
        //스피너에서 선택한 고양이 받아오기
        catNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: 선택한 고양이 이름 :: " + catNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            setImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    //bitmap을 file로 변환
    private String createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        String filepath = image.getAbsolutePath();
        return filepath;
    }
    //인텐트에서 이미지를 받아 화면에 표시함
    private void setImage() throws IOException {
        Intent intent = getIntent();
        ImageView imageView = findViewById(R.id.imageShare);
        if(intent.getStringExtra(getString(R.string.selected_image))!=null) {
            imgURL = (intent.getStringExtra(getString(R.string.selected_image)));
            UniversalImageLoader.setImage(imgURL, imageView, null, mAppend);
            Log.d(TAG, "setImage: imgURL ===> " + imgURL);

        }

    }

      /*
    ------------------------------------ Firebase ---------------------------------------------
     */
    //사진 업로드를위한 데이터모델 생성

    //이미지에 관련정보 속성을 추가함 (글내용, 날짜, imageURL, photo_id, 태그..? , 업로더 아이디

    //업로더가 올렸던 사진의 수를 카운트

    //파이어베이스 스토리지에 이미지 파일을 저장하고 firebase database에도 이미지 주소를 저장함 (+ 오라클)

    //
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: 유저의 이미지 갯수: " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
