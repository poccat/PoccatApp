package com.example.forcatapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.forcatapp.Group.GroupListFragment;
import com.example.forcatapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class OracleDBUpload extends AsyncTask<Map<String, Object>, Integer, Integer> {
    private static final String TAG = "OracleDBUpload";
        public static String ip = "192.168.0.51"; // 자신의 IP주소
        public String portNum = ":9005";
    public String body;
    public ArrayList<Map<String, Object>> resultList;
    String requestCode; // postingUpload || getGroupList
    String servletUrl;
    ProgressDialog pdialog;
    Context mContext;
    private int status;
    String Result;


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
        protected Integer doInBackground(Map<String, Object>... maps) { // 내가 전송하고 싶은 파라미터
        // Http 요청 준비 작업
        if(requestCode.equals("postingUpload")){
            this.servletUrl = "/firstB/posting_write.foc";
        } else if (requestCode.equals("getGroupList")){
            this.servletUrl = "/member/group_mem_mygroup.foc";
        } else if (requestCode.equals("getGroupBoardList")){
            this.servletUrl = "/member/group_board_list.foc";
        }

        HttpClient.Builder http = new HttpClient.Builder
                ("POST", "http://" + ip + portNum + servletUrl); //포트번호,서블릿주소
            // Parameter 를 전송한다.
            http.addAllParameters(maps[0]);
            Log.d(TAG, "doInBackground: http.addAllParameters(maps[0])" + maps[0]);
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

            return statusCode;
        }

        public ArrayList<Map<String, Object>> jsonToArray (String result) {
            Log.d(TAG, "jsonToArray: result? " + result);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Map<String, Object>>>() {}.getType();
            ArrayList<Map<String, Object>> resultList = gson.fromJson(result, type);
            Log.d(TAG, "jsonToArray: "+resultList);

            this.resultList = resultList;
            return resultList;
    }

    protected void onPostExecute(Integer result) {
        pdialog.setProgress(0);
        pdialog.dismiss();
    }
    }
