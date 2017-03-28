package com.android.dvr.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import com.android.dvr.ProApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class ImageUtils {
    private static Uri STORAGE_URI = Media.EXTERNAL_CONTENT_URI;

    private static ContentResolver contentResolver = ProApplication.getContext().getContentResolver();

    public static Uri addImage(File file) {
        String fileName = file.getName();
        String filePath = file.getAbsolutePath();
        int degree = getExifOrientation(filePath);
        long size = file.length();

        ContentValues values = new ContentValues(5);

        values.put(Media.DISPLAY_NAME, fileName);
        LogUtils.e("ImageInfo", "fileName" + fileName);
        values.put(Media.MIME_TYPE, "image/jpeg");
        values.put(Media.ORIENTATION, degree);
        values.put(Media.DATA, filePath);
        LogUtils.e("ImageInfo", "filePath" + filePath);
        values.put(Media.SIZE, size);
        LogUtils.e("ImageInfo", "size" + size);

        return contentResolver.insert(STORAGE_URI, values);
    }

    private static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }
}
