package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forcatapp.R;
import com.example.forcatapp.util.OracleDBUpload;
import com.example.forcatapp.util.SessionControl;

import java.util.List;

import cz.msebera.android.httpclient.cookie.Cookie;

public class MainFragment extends Fragment {
    private static final String TAG = "MainFragement";
    WebView wv_web;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        wv_web = view.findViewById(R.id.wv_main_photoList);
        WebSettings webSettings = wv_web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //CSS 깨짐 방지-----------------------------
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        WebView content = (WebView) view.findViewById(R.id.wv_main_photoList);
        content.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;}</style>"
                + "text/html", "text/html", "UTF-8", null);
        //CSS 깨짐 방지-----------------------------
        String url = "http://192.168.0.51:9005/firstB/poccat.foc";
        Intent intent = getActivity().getIntent(); /*데이터 수신*/

        //글쓰기 후 새로고침--------------------------------------------------------->
        Intent intent2 = getActivity().getIntent();
        if(intent2!=null && intent2.getStringExtra("Context")!=null){
            if(intent2.getStringExtra("Context").equals("fromWrite"));{
                WebView webView = view.findViewById(R.id.wv_main_photoList);
                webView.reload();
            }
        }
        //글쓰기 후 새로고침--------------------------------------------------------->

        // 세션 유지 ==============================================================>
        wv_web.setWebViewClient(new WebViewClient());
        CookieManager cookieManager = CookieManager.getInstance();

        //쿠키허용
        wv_web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(wv_web, true);

        String cookieString = SessionControl.getHttpclient().cookies;
        cookieManager.setCookie(url, cookieString);
        Log.d(TAG, "onCreateView: main cookieString" + cookieString);
        // 세션 유지 ==============================================================>

        wv_web.loadUrl(url);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침 소스
                wv_web.reload();
                Log.d(TAG, "onRefresh: 메인페이지 새로고침 ");
            }
        });

        //새로고침 완료되면 멈추기
        wv_web.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshLayout.setRefreshing(false);
            }

        });

        return view;
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
