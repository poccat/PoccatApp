package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forcatapp.R;

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
//        String mem_no = intent.getExtras().getString("mem_no"); /*String형*/
//        String mem_name = intent.getExtras().getString("mem_name"); /*String형*/

        //?mem_no=11001
 //       wv_web.loadUrl(url+"?="+mem_no);
        Intent intent2 = getActivity().getIntent();
        if(intent2!=null && intent2.getStringExtra("Context")!=null){
            if(intent2.getStringExtra("Context").equals("fromWrite"));{
                WebView webView = view.findViewById(R.id.wv_main_photoList);
                webView.reload();
            }
        }
        wv_web.loadUrl(url);
        wv_web.setWebViewClient(new WebViewClient());

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //새로고침 소스
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
