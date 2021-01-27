package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;

public class NonFollowFragment extends Fragment {
    private static final String TAG = "FollowFragment";
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 5;
    WebView wv_web;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_nonfollow, container, false);
        wv_web = view.findViewById(R.id.wv_nonfollow);
        WebSettings webSettings = wv_web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //CSS 깨짐 방지-----------------------------
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //CSS 깨짐 방지-----------------------------
        String url = "http://192.168.0.51:9005/firstB/poccat.foc?all=all";

        wv_web.loadUrl(url);

        return view;
    }
}
