package com.example.forcatapp.Member;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.forcatapp.Main.MainActivity;
import com.example.forcatapp.R;
import com.example.forcatapp.util.OracleDBUpload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    static RequestQueue requestQueue;
    List<Map<String, Object>> memberInfo;
    private EditText etMem_id, etMem_pw;
    private Button signup;
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etMem_id = findViewById(R.id.et_memId);
        etMem_pw = findViewById(R.id.et_memPw);

        //파이어베이스 로그인 객체 생성
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //이전 로그인정보 삭제
             //오라클 세션 로그아웃 처리
                android.webkit.CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookies(null);
                cookieManager.flush();
//             //파이어베이스 로그아웃 처리
              mAuth.signOut();

        //회원가입 클릭 시
        signup = (Button)findViewById(R.id.loginActivity_buttn_signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        if (requestQueue == null) {
            // requestQueue 초기화
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        //로그인 버튼 클릭 시
        Button btn_login = findViewById(R.id.btnLogin);
        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //오라클 로그인 후 세션 처리
                String pmem_id = etMem_id.getText().toString();
                String pmem_pw = etMem_pw.getText().toString();
                OracleDBUpload oracleDBUpload = new OracleDBUpload("loginGetSession", LoginActivity.this);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mem_email", pmem_id);
                params.put("mem_pw", pmem_pw);
                oracleDBUpload.execute(params);
                Log.d(TAG, "onClick: params ===> " + params);

                //앱 로그인
                login(pmem_id, pmem_pw);
            }
        });

    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
        private void login(String pmem_id, String pmem_pw) {
            //---------------------------------------------------------------------- firebase 로그인 처리
            mAuth.signInWithEmailAndPassword(pmem_id, pmem_pw)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                //로그인 실패
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
            //---------------------------------------------------------------------- firebase 로그인 처리

            }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}