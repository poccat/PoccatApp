package com.example.forcatapp.Member;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.model.UserModel;
import com.example.forcatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText pw;
    private Button signup;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private String splash_background;
    private ImageView profile;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));

        profile = (ImageView)findViewById(R.id.signupActivity_imageview_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });
        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        pw = (EditText) findViewById(R.id.signupActivity_edittext_pw);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString() == null || name.getText().toString() == null || pw.getText().toString() == null || imageUri == null){
                    return;
                }
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),pw.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();//회원 일련번호
                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(name.getText().toString()).build();
                                task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        Task<Uri> imageUrl = task.getResult().getStorage().getDownloadUrl();
                                        if(imageUrl!=null) {
                                            while (!imageUrl.isComplete()) ;
                                            UserProfileChangeRequest userProfileChangeRequest2 = new UserProfileChangeRequest.Builder().setPhotoUri(imageUrl.getResult()).build();
                                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest2);
                                            UserModel userModel = new UserModel();
                                            userModel.userName = name.getText().toString();
                                            userModel.profileImageUrl = imageUrl.getResult().toString();
                                            userModel.uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(),pw.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                        @Override
                                                        public void onSuccess(AuthResult authResult) {
                                                            Intent intent = new Intent();
                                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                                       }
                                                    });
                                                }
                                            });

                                        }
                                    }
                                });
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.getData()); //가운데 뷰를 바꿈
            imageUri = data.getData();//이미지 경로 원본
        }
    }
}