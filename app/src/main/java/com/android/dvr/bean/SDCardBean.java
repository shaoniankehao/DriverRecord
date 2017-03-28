package com.android.dvr.bean;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.dvr.ProApplication;
import com.android.dvr.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class SDCardBean {

    private static SDCardBean instance;

    private SQLiteHelper helper;

    // 私有化方法保证单例
    private SDCardBean() {
        helper = new SQLiteHelper(ProApplication.getContext());
    }

    // 单例模式
    public static SDCardBean getInstance() {
        if (instance == null) {
            instance = new SDCardBean();
        }
        return instance;
    }

    // 根据SD卡的唯一ID查询数据库
    public boolean isExistSDCardId(String id) {
        SQLiteDatabase sd = helper.getReadableDatabase();
        if (sd != null) {
            Cursor cursor = sd.rawQuery("select * from sdcard_info where id = ?", new String[]{id});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    return true;
                }
            }
            cursor.close();
        }
        sd.close();
        return false;
    }

    // 将SD卡的唯一ID插入数据库
    public void insert(String id) {
        SQLiteDatabase sd = helper.getWritableDatabase();
        if (sd != null) {
            sd.execSQL("insert into sdcard_info(id) values(?)", new String[]{id});
        }
        sd.close();
    }

    // 拼接获取SD卡唯一ID的路径，用于适用不同的SD卡
    private String getSdCardPath() {
        String parentPath = "/sys/devices/platform/mtk-msdc.1/mmc_host/mmc1";
        String sdCardPath = null;
        File f = new File(parentPath);
        File[] files = f.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                LogUtils.e(getClass(), files[i].getName());
                if (files[i].getName().startsWith("mmc1:")) {
                    sdCardPath = parentPath + "/" + files[i].getName() + "/cid";
                    LogUtils.e(getClass(), sdCardPath);
                    break;
                }
            }
        }
        return sdCardPath;
    }

    // 获取SD卡的唯一ID
    public String getSDCardId() {
        String macSerial, cmd, str = "";
        try {
            if (getSdCardPath() != null && getSdCardPath().length() != 0) {
                cmd = "cat " + getSdCardPath();
                Process pp = Runtime.getRuntime().exec(cmd);
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                if (null != str) {
                    str = input.readLine();
                    LogUtils.e(getClass(), str + "<<<<物理地址");
                    if (str != null && str.length() != 0) {
                        // 去空格
                        macSerial = str.trim();
                        return macSerial;
                    }
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return null;
    }
}
