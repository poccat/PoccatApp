package com.example.forcatapp.util;

import java.util.List;

public class SessionControl {
    static public HttpClient httpclient = null;

    public static HttpClient getHttpclient() {
        if (httpclient == null) {
            HttpClient http = new HttpClient();
            SessionControl.setHttpclient(http);
        }
        return httpclient;
    }

    public static void setHttpclient(HttpClient httpclient) {
        SessionControl.httpclient = httpclient;
    }
}
