package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.forcatapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQrActivity extends AppCompatActivity {
    Button btn_exit = null;//네이비티브앱
    Button btn_move = null;//네이비티브앱
    String qrContent = null;//네이티브앱
    WebView wv_content = null;//네이티브앱-그안에 보여지는 콘텐츠는 웹앱이다.
    IntentIntegrator intentIntegrator = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        btn_move = findViewById(R.id.btn_move);
        btn_exit = findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wv_content.canGoBack()) wv_content.goBack();
                else finish();
            }
        });
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv_content = findViewById(R.id.wv_qr);
                WebSettings webSettings = wv_content.getSettings();
                //자바스크립트를 사용할 수 있도록 설정하기
                webSettings.setJavaScriptEnabled(true);
                webSettings.setSupportMultipleWindows(true);//멀티윈도우 사용
                webSettings.setSupportZoom(true);//확대와 축소
                webSettings.setBuiltInZoomControls(true);//안드로이드에서 제공되는 줌 아이콘설정
                webSettings.setLoadsImagesAutomatically(true);//웹뷰가 앱에 등록된 이미지 리소스 인식
                webSettings.setUseWideViewPort(true);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//웹뷰가 캐쉬를 사용하지 않도록 설정
                wv_content.setWebViewClient(new WebViewClient());
                wv_content.setWebChromeClient(new WebChromeClient());
                wv_content.loadUrl(qrContent);
            }
        });///////////////end of event
        intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setPrompt("QR코드에 맞추세요");
        intentIntegrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result !=null){
            if(result.getContents() == null){//스캔한 결과가 없을 때
                Toast.makeText(this,"Cancelled", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Scanned : "+result.getContents(), Toast.LENGTH_LONG).show();
                qrContent = result.getContents();
            }

        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}