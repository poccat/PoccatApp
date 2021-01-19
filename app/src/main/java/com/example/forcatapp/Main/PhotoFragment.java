package com.example.forcatapp.Main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;
import com.example.forcatapp.util.Permissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";

    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int CAMERA_REQUEST_CODE = 5;
    private ImageView image;
    String mCurrentPhotoPath;
    private Uri photoUri;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragrament_photo, container, false);
        Button btnLaunchCamera = view.findViewById(R.id.btn_startCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 카메라 시작");//getCurrentTabNumber
                WriteFragment writeFragment = ((WriteFragment) PhotoFragment.this.getParentFragment());
                if (writeFragment.getCurrentTabNumber() == 1) {
                    //만약을 위해 카메라 권한이 있는지 확인
                    if (writeFragment.checkPermissions(Permissions.CAMERA_PERMESSION)) {
                        //카메라 실행 메소드
                        takePicAndDisplayIt(view);
                    } else {
                        //만약의 경우 카메라 권한이 없다면 글쓰기 페이지 재실행
                        Intent intent = new Intent(getActivity(), WriteFragment.class);
                        //인텐트 초기화
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
        return view;
    }

    // copied from the android development pages; just added a Toast to show the storage location
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: mCurrentPhotoPath ::" + mCurrentPhotoPath);
        return image;
    }

    public void takePicAndDisplayIt(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), imageFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent intent) {
        if (requestCode == CAMERA_REQUEST_CODE && resultcode == RESULT_OK) {
            Intent nextIntent = new Intent(getActivity(), NextActivity.class);
            nextIntent.putExtra(getString(R.string.selected_image), mCurrentPhotoPath);
            Log.d(TAG, "onActivityResult: 포스팅 마지막 화면으로 이동");
            startActivity(nextIntent);
            getActivity().finish();
        }
    }
}
