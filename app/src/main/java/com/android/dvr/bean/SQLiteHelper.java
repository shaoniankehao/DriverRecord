package com.android.dvr.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    //创建数据库
    public SQLiteHelper(Context context) {
        super(context, "info.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table sdcard_info(id integer primary key autoincrement,id varchar(32))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
