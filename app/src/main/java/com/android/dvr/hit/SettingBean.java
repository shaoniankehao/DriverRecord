package com.android.dvr.hit;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/10 0010.
 */

public class SettingBean {

    // 撞击值改变
    public static int     hitChange    = 0;
    public static boolean hitAutoSave  = true;
    // true为后摄像头  false为前摄像头
    public static boolean backCamera   = true;
    // 10---60(0----10) old is 1
    public static int     cirVideoLong = 2;
    // 分为 1 2 3 4 代表精细 高 中 低
    public static int     videoQuality = 0;
    // max=20
    public static int     hitDelicacy  = 10;
    // 录制模式 0-循环录制  1-正常录制
    public static boolean recordMode   = false;
    // 是否后台录制 0-不支持后台录制  1-支持后台录制
    public static boolean bgRecord     = true;     //
    // 后台录制动画 0-不支持后台录制动画  1-支持后台录制动画
    public static boolean bgAnimation  = true;
    // 记录保存目录
    public static int     pathItem     = 0;
    public static boolean clickSave    = false;
    public static boolean asVoice      = true;
    // default is true
    public static boolean recordSound  = false;
    public static int     savingTime   = -1;
    public static boolean firstTime    = true;

    private static ArrayList<OnSettingChangeListener> settingListeners = new ArrayList<>();

    /**
     * 设置监听
     *
     * @param listener
     */
    public static void setSettingListener(OnSettingChangeListener listener) {
        if (null != listener) {
            settingListeners.add(listener);
        }
    }

    /**
     * 移除监听
     *
     * @param listener
     */
    public static void removeListener(OnSettingChangeListener listener) {
        settingListeners.remove(listener);
    }

    public static void changeListener(int type) {
        for (int i = 0; i < settingListeners.size(); i++) {
            OnSettingChangeListener listener = settingListeners.get(i);
            if (null != listener) {
                listener.onSettingChange(type);
            } else {
                settingListeners.remove(i);
                i--;
            }
        }
    }
}
