package com.android.dvr.model;

import android.hardware.Camera;
import android.media.MediaRecorder;

import com.android.dvr.constant.ConfigHelper;
import com.android.dvr.contract.RecordContract;
import com.android.dvr.hit.SettingBean;
import com.android.dvr.mvp.IModel;
import com.android.dvr.util.SDCardUtils;
import com.android.dvr.util.VideoUtils;
import com.android.dvr.view.FloatWindow;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 * 录像
 */

public class VideoModel implements RecordContract.VideoModel, IModel {

    private static volatile MediaRecorder mMediaRecorder;

    private static File mVideoTempFile;
    private static File mVideoFile;
    private static File mProtectedVideoFile;
    private static long mLoopDuration;

    private Camera  mCamera;
    private Timer   mRecordTimer;
    private boolean mCanDelete;
    private boolean mIsCapture;

    private ExecutorService mExecutorService;
    private Runnable        mCleanDataRunnable;
    private Runnable        mRecordVideoRunnable;
    private TimerTask       mTimerTask;

    public VideoModel() {
        mCamera = FloatWindow.getInstance().getCamera();

        initThreadTask();
    }

    private void initThreadTask() {
        mRecordTimer = new Timer();
        mExecutorService = Executors.newCachedThreadPool();

        mCleanDataRunnable = new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                long stopTime, duration = 0L;
                do {
                    VideoUtils.deleteOldestVideo();
                    stopTime = System.currentTimeMillis();
                    if (stopTime > startTime) {
                        duration = stopTime - startTime;
                    }
                } while (isDataClean() && duration < (ConfigHelper.RECORD_DURATION - 1000));
            }
        };

        mRecordVideoRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    mCamera.unlock();
                    initMediaRecord();
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    mLoopDuration = System.currentTimeMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                stopRecord();
            }
        };
    }

    private void initMediaRecord() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        int videoQuality = SettingBean.videoQuality;
        int[][] videoProfiles = ConfigHelper.VIDEO_PROFILES;
        int audioBitRate = videoProfiles[videoQuality][0];
        int videoBitRate = videoProfiles[videoQuality][7];
        if (FloatWindow.getInstance().getIsRecordVoice()) {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setAudioEncodingBitRate(audioBitRate);
        }
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoFrameRate(20);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(videoBitRate);
        mMediaRecorder.setVideoSize(864, 480);
        String videoTempFilePath = getOutputMediaFile();
        mMediaRecorder.setOutputFile(videoTempFilePath);
    }

    @Override
    public void startRecord() {
        if (isDataClean()) {
            mExecutorService.execute(mCleanDataRunnable);
        }
        mExecutorService.execute(mRecordVideoRunnable);

        autoStopRecord();
    }

    private void autoStopRecord() {
        int delayTime;
        if (mIsCapture) {
            delayTime = ConfigHelper.APP_RECORD_DURATION;
        } else {
            delayTime = ConfigHelper.RECORD_DURATION;
        }
        mRecordTimer.schedule(mTimerTask, delayTime);
    }

    @Override
    public void stopRecord() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();
        mTimerTask.cancel();

        mLoopDuration = System.currentTimeMillis() - mLoopDuration;
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                saveVideo();
            }
        });

        if (mIsCapture) {
            mIsCapture = false;
        }

        if (mCanDelete) {
            mCanDelete = false;
        }

        if (FloatWindow.getInstance().getIsRecord()) {
            startRecord();
        }
    }

    @Override
    public void setIsHit() {
        mCanDelete = true;
        if (!FloatWindow.getInstance().getIsRecord()) {
            //设备休眠时碰撞抓拍
            mIsCapture = true;
            startRecord();
        }
    }

    @Override
    public void captureVideo() {
        mIsCapture = true;
        if (FloatWindow.getInstance().getIsRecord()) {
            stopRecord();
        } else {
            startRecord();
        }
    }

    private void saveVideo() {
        if (mVideoTempFile != null && mVideoTempFile.exists()) {
            mVideoTempFile.renameTo(mVideoFile);
        }

        if (mCanDelete) {
            moveFile(mVideoFile.getAbsolutePath(), mProtectedVideoFile.getAbsolutePath());
        }
    }

    private boolean moveFile(String srcFileName, String destDirName) {
        File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile())
            return false;
        File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File protectedFile = new File(destDirName + File.separator + srcFile.getName());
        VideoUtils.addVideo(mLoopDuration, protectedFile, false);

        return srcFile.renameTo(protectedFile);
    }

    /**
     * 是否需要清理数据
     */
    private boolean isDataClean() {
        if (SDCardUtils.shouldDeleteLoopFile()) {
            if (VideoUtils.haveOldestVideo()) {
                return true;
            }
        }
        return false;
    }

    private String getOutputMediaFile() {
        String fileName = getFormatTime();
        String datePath = fileName.substring(0, 4) + "-" + fileName.substring(4, 6) + "-" + fileName.substring(6, 8);
        String recordPath;
        if (mIsCapture && !mCanDelete) {
            //第三方应用抓拍路径，!mCanDelete表示排除未录像时的碰撞抓拍
            recordPath = ConfigHelper.APP_SAVE_VIDEO_PATH + datePath;
        } else {
            recordPath = ConfigHelper.SAVE_RECORD_PATH + datePath;
        }
        File recordFile = new File(recordPath);
        if (!recordFile.exists()) {
            recordFile.mkdirs();
        }
        mProtectedVideoFile = new File(ConfigHelper.SAVE_PROTECTED_PATH + datePath);

        mVideoTempFile = new File(recordPath, "b" + ConfigHelper.VIDEO_SUFFIX + ".tmp");
        if (mVideoTempFile.exists() || isExistIllegalVideoTmpFile()) {
            mVideoTempFile.delete();
        }
        mVideoFile = new File(recordPath, fileName + ConfigHelper.VIDEO_SUFFIX);
        return mVideoTempFile.getAbsolutePath();
    }

    /**
     * 删除日期为2010-01-01、2013-01-01的文件
     */
    private boolean isExistIllegalVideoTmpFile() {
        String[] IllegalRecordPath = new String[2];
        IllegalRecordPath[0] = ConfigHelper.SAVE_RECORD_PATH + "2010-01-01";
        IllegalRecordPath[1] = ConfigHelper.SAVE_RECORD_PATH + "2013-01-01";

        File[] IllegalVideoTmpFile = new File[IllegalRecordPath.length];
        for (int i = 0; i < IllegalRecordPath.length; i++) {
            IllegalVideoTmpFile[i] = new File(IllegalRecordPath[i], "b" + ConfigHelper.VIDEO_SUFFIX + ".tmp");
            if (IllegalVideoTmpFile[i].exists()) {
                IllegalVideoTmpFile[i].delete();
                return true;
            }
        }
        return false;
    }


    private String getFormatTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }
}
