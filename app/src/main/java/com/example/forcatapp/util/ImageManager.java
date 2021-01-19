package com.example.forcatapp.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {
    private static final String TAG = "ImageManager";
    
    // 선택한 이미지를 bitmap 형태로 변환해주는 메소드
    public static Bitmap getBitmap(String imgUrl){
        File imageFile = new File(imgUrl);
        FileInputStream file = null;
        Bitmap bitmap = null;

        try {
            file = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try{
                file.close();
            } catch(IOException e) {
            e.printStackTrace();
            }
        }
        return bitmap;
    }

    // bitmap 형태를 byte로 변환해주는 매소드

    /**
     *
     * @param bm
     * @param quality  0~100
     * @return
     */
    public static byte[] getBytesFromBitmap(Bitmap bm, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

}
