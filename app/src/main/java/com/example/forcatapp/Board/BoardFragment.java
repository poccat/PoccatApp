package com.example.forcatapp.Board;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.forcatapp.R;
import com.example.forcatapp.util.SessionControl;

import java.net.URISyntaxException;

import kr.co.bootpay.BootpayWebView;
import kr.co.bootpay.listener.EventListener;

public class BoardFragment extends Fragment implements WebAppBridgeInterface {
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

        //결제관련 url 컨트롤
        wv_web.setWebViewClient(new BWebviewClient());
        wv_web.setWebChromeClient(new BChromeClient());
        wv_web.addJavascriptInterface(new WebAppBridge(this), "Android");

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

        //아래로 스와이프해서 새로고침하기
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                wv_web.reload();
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

    @Override
    public void error(String data) {
        Log.d(TAG, "error: " + data);
    }

    @Override
    public void close(String data) {
        Log.d(TAG, "close: " + data);
    }

    @Override
    public void cancel(String data) {
        Log.d(TAG, "cancel: " + data);
    }

    @Override
    public void ready(String data) {
        Log.d(TAG, "ready: " + data);
    }

    @Override
    public void confirm(String data) {
        Log.d(TAG, "confirm: " + data);
        boolean iWantPay = true;
        if(iWantPay == true) {
            doJavascript("BootPay.transactionConfirm( " + data + ");");
        } else {
            doJavascript("BootPay.removePaymentWindow();");
        }
    }

    @Override
    public void done(String data) {
        System.out.println(data);
    }

    private class BWebviewClient extends WebViewClient {
        private boolean isLoaded = false;

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished: ");
            super.onPageFinished(view, url);
            if(isLoaded) return;
            isLoaded = true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: ");
            Intent intent = parse(url);
            if (isIntent(url)) {
                if (isExistInfo(intent, view.getContext()) || isExistPackage(intent, view.getContext()))
                    return start(intent, view.getContext());
                else
                    gotoMarket(intent, view.getContext());
            } else if (isMarket(url)) {
                return start(intent, view.getContext());
            }
            return url.contains("https://bootpaymark");
        }

        private Intent parse(String url) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                Log.d(TAG, "parse: url ===> " + url);
                if(intent.getPackage() == null) {
                    if (url == null) return intent;
                    if (url.startsWith("shinhan-sr-ansimclick")) intent.setPackage("com.shcard.smartpay");
                    else if (url.startsWith("kftc-bankpay")) intent.setPackage("com.kftc.bankpay");
                    else if (url.startsWith("ispmobile")) intent.setPackage("kvp.jjy.MispAndroid320");
                    else if (url.startsWith("hdcardappcardansimclick")) intent.setPackage("com.hyundaicard.appcard");
                    else if (url.startsWith("kb-acp")) intent.setPackage("com.kbcard.kbkookmincard");
                    else if (url.startsWith("mpocket.online.ansimclick")) intent.setPackage("kr.co.samsungcard.mpocket");
                    else if (url.startsWith("lotteappcard")) intent.setPackage("com.lcacApp");
                    else if (url.startsWith("cloudpay")) intent.setPackage("com.hanaskcard.paycla");
                    else if (url.startsWith("nhappvardansimclick")) intent.setPackage("nh.smart.nhallonepay");
                    else if (url.startsWith("citispay")) intent.setPackage("kr.co.citibank.citimobile");
                    else if (url.startsWith("kakaotalk")) intent.setPackage("com.kakao.talk");
                    else if (url.startsWith("kakaopay")) intent.setPackage("com.kakao.talk");
                }
                return intent;
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return null;
            }
        }

        private Boolean isIntent(String url) {
            return url.matches("^intent:?\\w*://\\S+$");
        }

        private Boolean isMarket(String url) {
            return url.matches("^market://\\S+$");
        }

        private Boolean isExistInfo(Intent intent, Context context) {
            try {
                return intent != null && context.getPackageManager().getPackageInfo(intent.getPackage(), PackageManager.GET_ACTIVITIES) != null;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }

        private Boolean isExistPackage(Intent intent, Context context) {
            return intent != null && context.getPackageManager().getLaunchIntentForPackage(intent.getPackage()) != null;
        }

        private boolean start(Intent intent, Context context) {
            context.startActivity(intent);
            return true;
        }

        private boolean gotoMarket(Intent intent, Context context) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + intent.getPackage())));
            return true;
        }
    }


    private class BChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setCancelable(true)
                    .create()
                    .show();
            return true;
        }
    }

}
