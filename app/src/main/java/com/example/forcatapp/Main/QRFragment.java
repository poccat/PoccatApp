package com.example.forcatapp.Main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.forcatapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/*
Activity안에 Fragment로 화면을 그린 경우에는
View view = getView();
createQR = view.findViewById(R.id.createQR);
액티비티내에 부분페이지로 프래그먼트를 사용했을때
프래그먼트에서 액티비티의 버튼이나 콤포넌트를 접근할 때 사용할것.
 */
public class QRFragment extends Fragment {
    private Button scanQR = null;
    String qrContent = null;//네이티브앱
    WebView wv_content = null;//네이티브앱-그안에 보여지는 콘텐츠는 웹앱이다.

    private static final String TAG = "MypageFragment";
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        scanQR = view.findViewById(R.id.scanQR);
        scanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.forSupportFragment(QRFragment.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        .setOrientationLocked(true)
                        .setBeepEnabled(false)
                        .setPrompt("QR코드에 맞추세요")
                        .initiateScan();
            }
        });

        return view;
    }//onCreateView

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result =
                IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: result " + result);
        if(result !=null){
            if(result.getContents() == null){//스캔한 결과가 없을 때
                Toast.makeText(getActivity(),"결과가 없습니다", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity()," 스캔 성공! "+result.getContents(), Toast.LENGTH_LONG).show();
                qrContent = result.getContents();
                wv_content = getView().findViewById(R.id.wv_qr);
                WebSettings webSettings = wv_content.getSettings();
                //자바스크립트를 사용할 수 있도록 설정하기
                webSettings.setJavaScriptEnabled(true);
                webSettings.setSupportZoom(true);//확대와 축소
                webSettings.setBuiltInZoomControls(true);//안드로이드에서 제공되는 줌 아이콘설정
                webSettings.setLoadsImagesAutomatically(true);//웹뷰가 앱에 등록된 이미지 리소스 인식
                // 화면 비율
                webSettings.setUseWideViewPort(true);       // wide viewport를 사용하도록 설정
                webSettings.setLoadWithOverviewMode(true);  // 컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정
                webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//웹뷰가 캐쉬를 사용하지 않도록 설정
                wv_content.setWebViewClient(new WebViewClient());
                wv_content.setWebChromeClient(new WebChromeClient());
                wv_content.loadUrl(qrContent);
            }

        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }
}
