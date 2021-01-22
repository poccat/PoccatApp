package com.example.forcatapp.Board;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.forcatapp.R;

import kr.co.bootpay.BootpayWebView;
import kr.co.bootpay.listener.EventListener;

public class WebAppActivity extends Activity {
    private static final String TAG = "WebAppActivity";
    BootpayWebView webview;
    final String url = "https://{개발하신 웹 페이지 주소}";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        webview = findViewById(R.id.webview);
        webview.setOnResponseListener(new EventListener() {
            @Override
            public void onError(String data) {
                Log.d(TAG, "onError: " + data);
            }

            @Override
            public void onCancel(String data) {
                Log.d(TAG, "onCancel: " + data);
            }

            @Override
            public void onClose(String data) {
                Log.d(TAG, "onClose: " + data);
            }

            @Override
            public void onReady(String data) {
                Log.d(TAG, "onReady: " + data);
            }

            @Override
            public void onConfirm(String data) {
                boolean iWantPay = true;
                if(iWantPay == true) { // 재고가 있을 경우
                    doJavascript("BootPay.transactionConfirm( " + data + ");");
                } else {
                    doJavascript("BootPay.removePaymentWindow();");
                }
            }
//===========================================[[ 결제 완료 ]]=================================================
            @Override
            public void onDone(String data) {
                Log.d(TAG, "onDone: " + data);

            }
        });
        webview.loadUrl(url);
    }

    void doJavascript(String script) {
        final String str = script;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webview.loadUrl("javascript:(function(){" + str + "})()");
            }
        });
    }
}