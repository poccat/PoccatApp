package com.example.forcatapp.util;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    /***
     * 폴더를 찾고 폴더안의 모든 *폴더*의 목록을 List로 리턴함
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for(int i = 0; i<listfiles.length ; i++){
            if(listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }
        return pathArray;
    }
    /***
     * 폴더를 찾고 폴더안의 모든 *파일*의 목록을 List로 리턴함
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        if(listfiles != null){
            for(int i = 0; i < listfiles.length; i++){
                if(listfiles[i].isFile()){
                    pathArray.add(listfiles[i].getAbsolutePath());
                }
            }
        }
        return pathArray;
    }

}
