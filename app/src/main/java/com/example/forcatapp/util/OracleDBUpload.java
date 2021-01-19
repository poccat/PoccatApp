package com.example.forcatapp.util;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;

public class OracleDBUpload extends AsyncTask<Map<String, Object>, Integer, Integer> {
    private static final String TAG = "OracleDBUpload";
        public static String ip = "192.168.0.51"; // 자신의 IP주소

        @Override
        protected Integer doInBackground(Map<String, Object>... maps) { // 내가 전송하고 싶은 파라미터

            // Http 요청 준비 작업
            HttpClient.Builder http = new HttpClient.Builder
                    ("POST", "http://" + ip + ":9005/firstB/posting_write.foc"); //포트번호,서블릿주소

            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();

            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            Log.d(TAG, "doInBackground: statusCode" + statusCode);

            return statusCode;
        }
    }
