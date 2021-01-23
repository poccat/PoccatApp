package com.example.forcatapp.util;

import android.util.Log;
import android.webkit.CookieManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


public class HttpClient {
        private static final String TAG = "HttpClient";
        private static final String WWW_FORM = "application/x-www-form-urlencoded";

        private int httpStatusCode;
        private String body;
        public String cookies;

        public int getHttpStatusCode() {
            return httpStatusCode;
        }

        public String getBody() {
            return body;
        }

        private Builder builder;

        private void setBuilder(Builder builder) {
            this.builder = builder;
        }

        public void request() {
            String COOKIES_HEADER = "Set-Cookie";

            HttpURLConnection conn = getConnection();
            setHeader(conn);
            setBody(conn);

            //세션처리를 위한 쿠키매니저======================================
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
            if(cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                    String cookieString = cookieName + "=" + cookieValue;

                    CookieManager.getInstance().setCookie(builder.url, cookieString);
                    cookies = CookieManager.getInstance().getCookie(builder.url);
                    Log.d(TAG, "request: CookieManager===>" + cookies);
                }
            }
            //세션처리를 위한 쿠키매니저======================================

            httpStatusCode = getStatusCode(conn);
            body = readStream(conn);
            conn.disconnect();
        }

        private HttpURLConnection getConnection() {
            try {
                URL url = new URL(builder.getUrl());
                return (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void setHeader(HttpURLConnection connection) {
            setContentType(connection);
            setRequestMethod(connection);

            connection.setConnectTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
        }

        private void setContentType(HttpURLConnection connection) {
            connection.setRequestProperty("Content-Type", WWW_FORM);
        }

        private void setRequestMethod(HttpURLConnection connection) {
            try {
                connection.setRequestMethod(builder.getMethod());
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
        }

        private void setBody(HttpURLConnection connection) {
            String parameter = builder.getParameters();
            if ( parameter != null && parameter.length() > 0 ) {
                OutputStream outputStream = null;
                try {
                    outputStream = connection.getOutputStream();
                    outputStream.write(parameter.getBytes("UTF-8"));
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if ( outputStream != null )
                            outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        private int getStatusCode(HttpURLConnection connection) {
            try {
                return connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return -10;
        }

        private String readStream(HttpURLConnection connection) {
            String result = "";
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ( (line = reader.readLine()) != null ) {
                    result += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(reader != null)
                        reader.close();
                } catch (IOException e) {}
            }

            return result;
        }

        public static class Builder {

            private Map<String, Object> parameters;
            private String method;
            private String url;

            public String getMethod() {
                return method;
            }

            public String getUrl() {
                return url;
            }

            public Builder(String method, String url) {
                if(method == null) {
                    method = "GET";
                }
                this.method = method;
                this.url = url;
                this.parameters = new HashMap<String, Object>();
            }

            public void addOrReplace(String key, String value) {
                this.parameters.put(key, value);
            }

            public void addAllParameters(Map<String, Object> param) {
                this.parameters = param;
                Log.d(TAG, "addAllParameters: param ===> " + param);
            }

            public String getParameters() {
                return generateParameters();
            }

            public Object getParameter(String key) {
                return this.parameters.get(key);
            }

            private String generateParameters() {
                StringBuffer parameters = new StringBuffer();

                //url 처리-----------------------------------------------------------
                String downloadurl = "";
                String token = "";
                if(this.parameters.get("post_photo_pre") != null){
                    String post_photo_pre = this.parameters.get("post_photo_pre").toString();
                    StringTokenizer st = new StringTokenizer(post_photo_pre, "&");
                    downloadurl = st.nextToken();
                    token = st.nextToken();
                    Log.d(TAG, "generateParameters: downloadurl==>" + downloadurl + ",  token ===> " + token);
                    this.parameters.remove("post_photo_pre");
                }

                Iterator<String> keys = getKeys();

                String key = "";
                while ( keys.hasNext() ) {
                    key = keys.next();
                    parameters.append(key);
                    parameters.append("=");
                    parameters.append(this.parameters.get(key));
                    parameters.append("&");
                }

                if(downloadurl.length() > 1){
                    parameters.append("post_photo1");
                    parameters.append("=");
                    parameters.append(downloadurl);
                }

                String params = parameters.toString();
                /*
                if ( params.length() > 0 ) {
                    params = params.substring( 0, params.length() - 1 );
                }
                 */
                Log.d(TAG, "generateParameters: parameters.toString()===> " + params);
                return params;
            }

            private Iterator<String> getKeys() {
                return this.parameters.keySet().iterator();
            }

            public HttpClient create() {
                HttpClient client = new HttpClient();
                client.setBuilder(this);
                return client;
            }
        }
    }
