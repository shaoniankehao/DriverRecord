package com.android.dvr.util;

import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;

import com.android.dvr.bean.SDCardBean;
import com.android.dvr.constant.ConfigHelper;
import com.mediatek.common.MediatekClassFactory;
import com.mediatek.common.storage.IStorageManagerEx;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class SDCardUtils {

    public static boolean isFirstFormatSdcard() {
        String sdCardId = SDCardBean.getInstance().getSDCardId();
        if (sdCardId != null && sdCardId.length() != 0) {
            return SDCardBean.getInstance().isExistSDCardId(sdCardId);
        }
        return false;
    }

    /**
     * 检测SD卡挂载状况
     */
    public static boolean checkSDCardMount() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (isSDCardExist()) {
            if (mExternalStorageAvailable && mExternalStorageWriteable) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * function : save space < 1024 * 1024 * 1024
     */
    public static boolean shouldDeleteLoopFile() {
        try {
            String m_path = ConfigHelper.SDCARD_SAVE_PATH;
            boolean bl = m_path.contains("sdcard");
            if (bl)
                m_path = "/mnt/sdcard";
            else
                m_path = "/mnt/ext_sd";
            StatFs stat = new StatFs(m_path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize <= ConfigHelper.SHOULD_DELETE_LOOP_FILE_SIZE;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isOpenCameraErrorDeletLoopFile() {
        try {
            String m_path = ConfigHelper.SDCARD_SAVE_PATH;
            boolean bl = m_path.contains("sdcard");
            long size = 1024 * 1024 * 1024;
            if (bl)
                m_path = "/mnt/sdcard";
            else
                m_path = "/mnt/ext_sd";
            StatFs stat = new StatFs(m_path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize <= size;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isNeedRestartApp() {
        try {
            String m_path = ConfigHelper.SDCARD_SAVE_PATH;
            boolean bl = m_path.contains("sdcard");
            long size = 128 * 1024 * 1024;
            if (bl)
                m_path = "/mnt/sdcard";
            else
                m_path = "/mnt/ext_sd";
            StatFs stat = new StatFs(m_path);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize <= size;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isSDCardExist() {
        IStorageManagerEx sm = MediatekClassFactory.createInstance(IStorageManagerEx.class);
        return sm.getSdSwapState();
    }

    public static void formatSDCard(FormatHint formatHint) {
        try {
            int Count = 0;
            LogUtils.e("formatSDCard", "formatSDCard");
            Method getService = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            Object service = getService.invoke(null, "mount");
            Class Stub = Class.forName("android.os.storage.IMountService$Stub");
            Method asInterface = Stub.getMethod("asInterface", Class.forName("android.os.IBinder"));
            Object mountServ = asInterface.invoke(null, service);
            Method unmountVolume = mountServ.getClass().getMethod("unmountVolume", String.class, boolean.class, boolean.class);
            LogUtils.e("formatSDCard", "path = " + Environment.getExternalStorageDirectory().getPath());
            unmountVolume.invoke(mountServ, "/storage/sdcard0", true, true);
            while (Environment.isExternalStorageRemovable() && Count < 30) {
                SystemClock.sleep(1000);
                ++Count;
            }
            Method formatVolume = mountServ.getClass().getMethod("formatVolume", String.class);
            Object ob = formatVolume.invoke(mountServ, "/storage/sdcard1");
            LogUtils.e("formatSDCard", " m格式化结果 = " + ob);
            SystemClock.sleep(2000);
            Method mountVolume = mountServ.getClass().getMethod("mountVolume", String.class);
            Object om = mountVolume.invoke(mountServ, "/storage/sdcard1");
            LogUtils.e("formatSDCard", "m挂载结果 ..= " + om);
            if ((Integer) om == 0 && (Integer) ob == 0) {
                formatHint.success();
            }
        } catch (InvocationTargetException e) {
            formatHint.failed();
            LogUtils.e("formatSDCard", "此处接收被调用方法内部未被捕获的异常");
            Throwable t = e.getTargetException();// 获取目标异常
            t.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface FormatHint {
        void success();

        void failed();
    }
}
