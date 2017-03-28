package com.android.dvr.util;

import android.widget.Toast;

import com.android.dvr.ProApplication;


/**
 * Created by gaosheng on 2016/12/1.
 * 23:34
 * com.example.gaosheng.myapplication.utils
 */

public class ToastUtil {
    private static Toast toast;

    public static void setToast(String str) {

        if (toast == null) {
            toast = Toast.makeText(ProApplication.getContext(), str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }
}
