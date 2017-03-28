package com.android.dvr.bean;

import java.util.ArrayList;

public class VideoBean {

    private String title;

    private ArrayList<VideoInfo> arrayList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<VideoInfo> getArrayList() {
        return arrayList;
    }

    public class VideoInfo {
        public boolean protective;
        public String  filePath;

        @Override
        public String toString() {
            return "VideoInfo{" +
                    "protective=" + protective +
                    ", filePath='" + filePath + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ", arrayList=" + arrayList +
                '}';
    }
}