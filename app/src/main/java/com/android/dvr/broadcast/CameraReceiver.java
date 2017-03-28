package com.android.dvr.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.PowerManager;

import com.android.dvr.view.FloatWindow;

/**
 * Created by duanpeifeng on 2017/3/27 0027.
 * <p>
 * 接受切换摄像头的广播
 */

public class CameraReceiver extends BroadcastReceiver {
    private static PowerManager.WakeLock mWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        if ("com.hxmid.ACTION.SWITCH_REAR_CAMERA".equals(action)) {
            Camera.getCameraInfo(FloatWindow.CAMERA_FRONT, cameraInfo);
            int cameraNum = Camera.getNumberOfCameras();
            if (cameraNum == 2 && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (!pm.isScreenOn()) {
                    mWakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP, "bright");
                    mWakeLock.acquire();
                }
                if (FloatWindow.getInstance().getIsRecord()) {
                    FloatWindow.getInstance().setRecord(false);
                }
                FloatWindow.getInstance().switchCamera(FloatWindow.CAMERA_REAR);
                if (FloatWindow.getInstance().getIsHide()) {
                    FloatWindow.getInstance().startPreviewByType(FloatWindow.TYPE_FILL);
                }
            }

        } else if ("com.hxmid.ACTION.SWITCH_FRONT_CAMERA".equals(action)) {
            Camera.getCameraInfo(FloatWindow.CAMERA_REAR, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                if (mWakeLock != null) {
                    mWakeLock.release();
                    mWakeLock = null;
                }
                if (!FloatWindow.getInstance().getIsHide()) {
                    FloatWindow.getInstance().startPreviewByType(FloatWindow.TYPE_HIDE);
                }
                FloatWindow.getInstance().switchCamera(FloatWindow.CAMERA_FRONT);
                FloatWindow.getInstance().setRecord(true);
            }
        }
    }
}
