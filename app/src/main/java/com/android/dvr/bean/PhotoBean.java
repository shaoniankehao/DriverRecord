package com.android.dvr.bean;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/20 0020.
 */

public class PhotoBean {
    String            title;
    ArrayList<String> arrayList = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getArrayList() {
        return arrayList;
    }

    @Override
    public String toString() {
        return "PhotoBean{" +
                "title='" + title + '\'' +
                ", arrayList=" + arrayList +
                '}';
    }
}
