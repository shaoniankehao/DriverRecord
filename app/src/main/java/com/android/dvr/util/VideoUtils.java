package com.android.dvr.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Video;

import com.android.dvr.ProApplication;
import com.android.dvr.constant.ConfigHelper;

import java.io.File;

/**
 * Created by duanpeifeng on 2017/3/14 0014.
 */

public class VideoUtils {
    private static final Uri STORAGE_URI = Video.Media.EXTERNAL_CONTENT_URI;

    private static final String WHERE_OLDEST = Video.Media.DATA + " like '" +
            ConfigHelper.SAVE_RECORD_PATH + "%";

    private static final String[] COLUMNS = new String[]{
            Video.Media.DATA, Video.Media._ID, Video.Media.TITLE, Video.Media.MIME_TYPE,
            Video.Media.DISPLAY_NAME, Video.Media.SIZE, Video.Media.DURATION, Video.Media.TAGS
    };

    private static ContentResolver contentResolver = ProApplication.getContext().getContentResolver();

    public static Uri addVideo(long duration, File file, boolean canDelete) {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        long size = file.length();

        ContentValues values = new ContentValues(6);
        values.put(Video.Media.DISPLAY_NAME, fileName);
        LogUtils.e("VideoInfo", "fileName：" + fileName);
        values.put(Video.Media.DURATION, duration);
        LogUtils.e("VideoInfo", "duration：" + duration);
        values.put(Video.Media.MIME_TYPE, "video/mp4");
        values.put(Video.Media.DATA, filePath);
        LogUtils.e("VideoInfo", "filePath：" + filePath);
        values.put(Video.Media.SIZE, size);
        LogUtils.e("VideoInfo", "size：" + size);
        values.put(Video.Media.TAGS, canDelete ? "true" : "false");
        LogUtils.e("VideoInfo", "canDelete：" + canDelete);

        return contentResolver.insert(STORAGE_URI, values);
    }

    private static void deleteVideoFile(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            f.delete();
        } else {

        }
    }

    public static void deleteOldestVideo() {
        Cursor cursor = contentResolver.query(STORAGE_URI, COLUMNS, WHERE_OLDEST, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Video.Media._ID));
            Uri uri = ContentUris.withAppendedId(STORAGE_URI, id);
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(Video.Media.DATA));
            if (fileName.length() != 0 && fileName != null) {
                deleteVideoFile(fileName);
            }

            deleteVideo(uri);
            int index = fileName.indexOf(".mp4");
            if (index >= 0) {
                deleteXmlFile(fileName.substring(0, index) + ".xml");
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static boolean haveOldestVideo() {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(STORAGE_URI, COLUMNS, WHERE_OLDEST, null, null);

            if (cursor != null && cursor.getCount() >= 1) {
                cursor.close();
                cursor = null;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public static void deleteXmlFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteVideo(Uri deleteUri) {
        if (deleteUri != null) {
            contentResolver.delete(deleteUri, null, null);
        }
    }
}
