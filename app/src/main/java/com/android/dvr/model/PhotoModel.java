package com.android.dvr.model;

import android.hardware.Camera;

import com.android.dvr.constant.ConfigHelper;
import com.android.dvr.contract.RecordContract;
import com.android.dvr.mvp.IModel;
import com.android.dvr.util.ImageUtils;
import com.android.dvr.util.LogUtils;
import com.android.dvr.view.FloatWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 * 拍照
 */

public class PhotoModel implements RecordContract.PhotoModel, IModel {
    private boolean mIsCapture;
    private File    mImageFile;

    private ExecutorService mExecutorService;

    public PhotoModel() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void takePicture() {
        initOutputFile();
        Camera camera = FloatWindow.getInstance().getCamera();
        try {
            camera.reconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.Parameters params = camera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setPictureSize(1920, 1080);
        camera.setParameters(params);
        camera.takePicture(mShutterCallback, null, mPictureCallback);
    }

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            LogUtils.e("PhotoModel", "onShutter");
        }
    };

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(mImageFile);
                fos.write(data);
                fos.flush();
                ImageUtils.addImage(mImageFile);
                if (!FloatWindow.getInstance().getIsRecord()) {
                    camera.stopPreview();
                }
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private void initOutputFile() {
        String fileName = getFormatTime();
        String datePath = fileName.substring(0, 4) + "-" + fileName.substring(4, 6) + "-" + fileName.substring(6, 8);
        String picturePath;
        if (mIsCapture) {
            picturePath = ConfigHelper.APP_SAVE_CAPTURE_PATH + datePath;
        } else {
            picturePath = ConfigHelper.SAVE_CAPTURE_PATH + datePath;
        }
        File pictureFile = new File(picturePath);
        if (!pictureFile.exists()) {
            pictureFile.mkdirs();
        }
        mImageFile = new File(pictureFile, fileName + ConfigHelper.IMAGE_SUFFIX);
    }

    @Override
    public void capturePicture() {
        mIsCapture = true;
        takePicture();
    }

    private String getFormatTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }
}
