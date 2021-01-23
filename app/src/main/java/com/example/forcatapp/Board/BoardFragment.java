package com.example.forcatapp.Board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forcatapp.R;
import com.example.forcatapp.util.SessionControl;

import kr.co.bootpay.BootpayWebView;
import kr.co.bootpay.listener.EventListener;

public class BoardFragment extends Fragment {
    private static final String TAG = "BoardFragment";
    BootpayWebView wv_web;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_boardlist, container, false);

        //이전버튼 이벤트 처리
        ImageView backArrow = view.findViewById(R.id.common_back);
        backArrow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 게시판창 뒤로가기");
                getActivity().onBackPressed();
            }
        });

        wv_web = view.findViewById(R.id.wv_board_boardlist);
        WebSettings webSettings = wv_web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //CSS 깨짐 방지-----------------------------
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //CSS 깨짐 방지-----------------------------
        Intent intent = getActivity().getIntent(); /*데이터 수신*/
        //BoardActivity에서 전달한 번들 저장
        Bundle bundle = getArguments();
        //번들 안의 텍스트 불러오기
        String url = bundle.getString("url");
        int caseNum = bundle.getInt("caseNum");
        if(caseNum == 1){
            bootpay(wv_web, url);
        }

        // 세션 유지 ==============================================================>
        wv_web.setWebViewClient(new WebViewClient());
        CookieManager cookieManager = CookieManager.getInstance();

        //쿠키허용
        cookieManager.setAcceptCookie(true);
        wv_web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        cookieManager.setAcceptThirdPartyCookies(wv_web, true);

        String cookieString = SessionControl.getHttpclient().cookies;
        cookieManager.setCookie(url, cookieString);
        Log.d(TAG, "onCreateView: BoardFragment cookieString" + cookieString);
        // 세션 유지 ==============================================================>

        wv_web.loadUrl(url);
        wv_web.setWebViewClient(new WebViewClient());

        //아래로 스와이프해서 새로고침하기
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wv_web.reload();
            }
        });

        wv_web.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
            }

        });

        return view;
    }

    private void bootpay(BootpayWebView wv_web, String url) {
        wv_web.setOnResponseListener(new EventListener() {
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
        wv_web.loadUrl(url);
        }

    public void doJavascript(String script) {
        final String str = script;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wv_web.loadUrl("javascript:(function(){" + str + "})()");
            }
        });
    }

    class MyViewClient extends WebViewClient {
        //웹뷰 내에서 웹 페이지를 불러올때 사용함.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            super.shouldOverrideUrlLoading(view,url);
            view.loadUrl(url);
            return true;
        }
    }

}
