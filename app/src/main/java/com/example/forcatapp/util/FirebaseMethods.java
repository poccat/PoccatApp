package com.example.forcatapp.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.R;
import com.example.forcatapp.model.PhotoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference myRef;

    private Context mContext;
    private double mPhotoUploadProgress;

    public FirebaseMethods(Context context) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
    }

    public int getImageCount(DataSnapshot dataSnapshot) {
        int count = 0;
        for(DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .getChildren()){
            count++;
        }
        return count;
    }

    public void uploadNewPhoto(String photoType, String contents, int count, String imgUrl){
        FilePath filePath = new FilePath();

        //업로드된 이미지가 포스팅 이미지일때
        if(photoType.equals("newPhoto")){
            Log.d(TAG, "uploadNewPhoto: 새로운 이미지 업로드");
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //사진을 저장하는 위치
            StorageReference storageReference = mStorageReference.child(filePath.FIREBASE_IMG_STRING
                    + "/" + user_id + "/photo" + (count+1));
            //이미지 url을 비트맵으로 변환
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            //비트맵을 바이트로 변환
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            //업로드 실행 클래스
            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);

            //업로드 성공시
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> firebaseUrl = taskSnapshot.getStorage().getDownloadUrl();
                    Toast.makeText(mContext, "사진업로드 성공!", Toast.LENGTH_SHORT).show();

                    //새로운 사진을 firebase DB에도 추가
                    addPhotoToDatabase(contents, firebaseUrl);

                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            GpsTracker gpsTracker = new GpsTracker(mContext);
                            Double latitude = gpsTracker.getLatitude();
                            Double longitude = gpsTracker.getLongitude();

                            String downloadUrl = task.getResult().toString();
                            //오라클 DB에 포스팅 정보와 사진경로 업로드=====================================================================================
                            try{
                                OracleDBUpload oracleDBUpload = new OracleDBUpload();
                                Log.d(TAG, "onComplete: 다운로드 url ===> " + downloadUrl);
                                Map<String, Object> params = new HashMap<String, Object>();
                                params.put("mem_no", "11001");
                                params.put("post_cnt", "11001");
                                params.put("cat_no", "20002");
                                params.put("post_latitude", latitude);
                                params.put("post_longitude", longitude);
                                params.put("post_title", "test");
                                params.put("post_type", "1");
                                params.put("post_photo_pre", downloadUrl);
                                params.put("post_photo2", 1);
                                params.put("post_photo3", 1);
                                params.put("post_photo4", 1);
                                params.put("post_photo5", 1);

                                oracleDBUpload.execute(params);
                                Log.d(TAG, "onClick: params ===> " + params);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    // 메인으로 이동
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.putExtra("Context", "fromWrite");
                    mContext.startActivity(intent);
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: 사진 업로드 실패");
                    Log.d(TAG, "onFailure: " + e.toString());
                    Toast.makeText(mContext, "사진 업로드 실패!", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = ( 100 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());

                    if (progress -15 > mPhotoUploadProgress) {
                        Toast.makeText(mContext, "사진 업로드중... " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }
                    Log.d(TAG, "onProgress: 사진 업로드중... " + progress + "% 완료");
                }
            });

        }
        else if(photoType.equals("profile")){ //프로필 사진일 때

        }

    }

    //사진 정보를 firebase데이터베이스에 저장
    private void addPhotoToDatabase(String contents, Task<Uri> firebaseUrl) {
        Log.d(TAG, "addPhotoToDatabase: 사진정보 firebase db에 저장");

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String newPhotokey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        PhotoModel photo = new PhotoModel();
        photo.setCaption(contents);
        photo.setDate_created(getTimeStamp());
        photo.setImage_path(firebaseUrl.toString());
        photo.setUser_id(userID);
        photo.setPhoto_id(newPhotokey);

        //데이터에 업로드
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(userID).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotokey).setValue(photo);
    }

    private String getTimeStamp() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return sdf.format(new Date());
    }


}
