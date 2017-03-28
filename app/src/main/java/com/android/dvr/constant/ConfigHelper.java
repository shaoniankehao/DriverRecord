package com.android.dvr.constant;

import android.os.Environment;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public interface ConfigHelper {
    // 应用保存总路径
    String SDCARD_PATH         = Environment.getExternalStorageDirectory().getAbsolutePath();
    String SDCARD_SAVE_PATH    = SDCARD_PATH + "/DVR";
    // 照片保存路径
    String SAVE_CAPTURE_PATH   = SDCARD_SAVE_PATH + "/" + "picture/";
    // 录制保存路径
    String SAVE_RECORD_PATH    = SDCARD_SAVE_PATH + "/" + "video/";
    // 加锁文件
    String SAVE_PROTECTED_PATH = SDCARD_SAVE_PATH + "/" + "protected/";
    // 常规录制保存视频时长
    int    RECORD_DURATION     = 2 * 60 * 1000;

    // 第三方应用路径
    String APP_SAVE_PATH         = "/app";
    // 第三方应用照片保存路径
    String APP_SAVE_CAPTURE_PATH = SDCARD_PATH + APP_SAVE_PATH + "/" + "picture/";
    // 第三方应用视频保存路径
    String APP_SAVE_VIDEO_PATH   = SDCARD_PATH + APP_SAVE_PATH + "/" + "video/";
    // 第三方应用视频录制视频时长
    int    APP_RECORD_DURATION   = 7 * 1000;

    /*
     * 录像相关画质参数 0 - audioBitRate 1 - audioChannels 2 - audioCodec 3 -
     * audioSampleRate 4 - duration 5 - fileFormat 6 - quality 7 - videoBitRate
     * 8 - videoCodec 9 - videoFrameHeight 10 - videoFrameRate 11 -
     * videoFrameWidth
     */
    int[][] VIDEO_PROFILES               = {
            {128000, 2, 3, 16000, 30, 1, 3, 10000000, 3, 480, 30, 864},
            {28500, 1, 2, 16000, 60, 1, 2, 2000000, 2, 288, 30, 352},
            {12200, 1, 1, 8000, 30, 1, 0, 1000000, 2, 144, 30, 176},
            {12200, 1, 1, 8000, 30, 1, 0, 1000000, 2, 144, 30, 176}};
    // 视频保存文件扩展名
    String  VIDEO_SUFFIX                 = ".mp4";
    // 拍照保存文件扩展名
    String  IMAGE_SUFFIX                 = ".jpg";
    // SDCard可用空间临界值 ，小于此值将进行提示
    long    SHOULD_DELETE_LOOP_FILE_SIZE = 1024 * 1024 * 1024;
}
