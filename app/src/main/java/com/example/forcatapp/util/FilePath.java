package com.example.forcatapp.util;

import android.os.Environment;

import java.io.File;

public class FilePath {
                            //기본 저장 경로를 반환함 == "/storage/emulated/0/pictures"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String DOWNLOAD = ROOT_DIR + "/Download";
    public String PICTURES = ROOT_DIR + "/Pictures";
    //외부저장소
    public String SAVED_PHOTO = "/storage/emulated/0/Android/data/com.example.forcatapp/files/Pictures";
    public String FIREBASE_IMG_STRING = "photos_/users/";
}
