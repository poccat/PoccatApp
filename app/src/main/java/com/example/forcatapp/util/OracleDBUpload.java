package com.example.forcatapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.forcatapp.Group.GroupListFragment;
import com.example.forcatapp.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class OracleDBUpload extends AsyncTask<Map<String, Object>, Integer, Integer> {
    private static final String TAG = "OracleDBUpload";
        public static String ip = "192.168.0.51"; // 자신의 IP주소
        public String portNum = ":9005";
    public String body;
    public List<Map<String, Object>> resultList;
    String requestCode; // postingUpload || getGroupList
    String servletUrl;
    ProgressDialog pdialog;
    Context mContext;
    private int status;
    String Result;
    HttpClient.Builder http;
    String cookies;


    public OracleDBUpload(String requestCode, Context context) {
        this.requestCode = requestCode;
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        pdialog = new ProgressDialog(mContext);				//프로그래스 다이얼로그 정의
        pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);	//프로그래스 다이얼로그 바 스타일로 정의
        pdialog.setMessage("콘텐츠 확인 중 입니다...");			//바의 메시지 출력
        pdialog.show();						//다이얼로그 출력
        super.onPreExecute();
    }
    protected void onProgressUpdate(Integer ... values) {
        pdialog.setProgress(values[0].intValue());
    }

    @Override
        protected Integer doInBackground(Map<String, Object>... maps) {
        // Http 요청 준비 작업
        switch(requestCode) {
            case "postingUpload":
                this.servletUrl = "/firstB/posting_write.foc";
                break;
            case "getGroupList":
                this.servletUrl = "/member/group_mem_mygroup.foc";
                break;
            case "getGroupBoardList":
                this.servletUrl = "/member/group_board_list.foc";
                break;
            case "loginGetSession":
                this.servletUrl = "/member/member_login.foc";
                break;
            case "getMapInfo":
                this.servletUrl = "/cat/cat_map.foc";
                break;
        }
            http = new HttpClient.Builder
                ("POST", "http://" + ip + portNum + servletUrl); //포트번호,서블릿주소
            // Parameter 를 전송한다.
            if(maps.length > 0){
                http.addAllParameters(maps[0]);
                Log.d(TAG, "doInBackground: http.addAllParameters(maps[0])" + maps[0]);
            }

            //Http 요청 전송
            HttpClient post = http.create();
            post.request();

            // 응답 상태코드 가져오기
            int statusCode = post.getHttpStatusCode();
            Log.d(TAG, "doInBackground: statusCode" + statusCode);

            // 응답 본문 가져오기
            String body = post.getBody();
            Log.d(TAG, "doInBackground: body===>" + body);
            if(body!=null){
                jsonToArray(body);
            }

            //쿠키 가져오기
            SessionControl.setHttpclient(post);
            setCookies(post.cookies);

            return statusCode;
        }

        public List<Map<String, Object>> jsonToArray (String result) {
            Log.d(TAG, "jsonToArray: result? " + result);
            Type type = new TypeToken<ArrayList<Map<String, Object>>>() {}.getType();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Map.class, new MapDeserializer())
                    .setDateFormat("yyyy-MM-dd HH:mm:ss").serializeNulls().create();

            List<Map<String, Object>> resultList = gson.fromJson(result, type);
            Log.d(TAG, "jsonToArray: "+resultList);

            this.resultList = resultList;
            return resultList;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    protected void onPostExecute(Integer result) {
        pdialog.setProgress(0);
        pdialog.dismiss();
    }
    }
