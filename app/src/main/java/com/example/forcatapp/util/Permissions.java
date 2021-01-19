package com.example.forcatapp.util;


import android.Manifest;

public class Permissions {

    public static final String[] PERMESSIONS ={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
    };
    public static final String CAMERA_PERMESSION = Manifest.permission.CAMERA;
    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
}
