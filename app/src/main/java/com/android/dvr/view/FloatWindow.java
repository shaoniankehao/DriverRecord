package com.android.dvr.view;

import android.content.Context;
import android.hardware.Camera;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.android.dvr.ProApplication;
import com.android.dvr.R;
import com.android.dvr.constant.RecordHelper;
import com.android.dvr.hit.OnHitListener;
import com.android.dvr.hit.SettingBean;
import com.android.dvr.util.LogUtils;
import com.android.dvr.util.SpUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by duanpeifeng on 2017/3/10 0010.
 */

public class FloatWindow implements SurfaceHolder.Callback, OnHitListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    /**
     * 全屏显示
     */
    public static final int TYPE_FILL = 0x101;
    /**
     * 悬浮窗口
     */
    public static final int TYPE_WRAP = 0x102;
    /**
     * 隐藏窗口
     */
    public static final int TYPE_HIDE = 0x103;

    /**
     * 前摄
     */
    public static final int CAMERA_FRONT = 0;
    /**
     * 后拉
     */
    public static final int CAMERA_REAR  = 1;

    private SurfaceView    mSurfaceView;
    private Chronometer    chr_time;
    private RelativeLayout mRlButton;
    private RelativeLayout rl_line;
    private CheckBox       cb_voice;
    private CheckBox       cb_record;
    private View           mRootView;

    private WindowManager              mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private Camera  mCamera;
    private int     mScreenWidth;
    private int     mScreenHeight;
    private long    mLastHitTime;
    private boolean mIsHide;

    private static FloatWindow instance;

    public static FloatWindow getInstance() {
        if (instance == null) {
            synchronized (FloatWindow.class) {
                if (instance == null) {
                    instance = new FloatWindow();
                }
            }
        }
        return instance;
    }

    private FloatWindow() {
        initView();
        initFloatWindow();
    }

    private void initView() {
        mRootView = LayoutInflater.from(ProApplication.getContext()).inflate(R.layout.floatwindow, null);
        mSurfaceView = (SurfaceView) mRootView.findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
        mRlButton = (RelativeLayout) mRootView.findViewById(R.id.rl_button);
        chr_time = (Chronometer) mRootView.findViewById(R.id.chr_time);
        rl_line = (RelativeLayout) mRootView.findViewById(R.id.rl_line);
        mRootView.findViewById(R.id.iv_picture).setOnClickListener(this);
        mRootView.findViewById(R.id.iv_file).setOnClickListener(this);
        cb_voice = ((CheckBox) mRootView.findViewById(R.id.cb_voice));
        cb_voice.setChecked((Boolean) SpUtils.get("IsRecordVoice", false));
        cb_voice.setOnCheckedChangeListener(this);
        cb_record = ((CheckBox) mRootView.findViewById(R.id.cb_record));
        cb_record.setOnCheckedChangeListener(this);
    }

    private void initFloatWindow() {
        mWindowManager = (WindowManager) ProApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels + 2;
        mScreenHeight = dm.heightPixels;
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
    }

    public void startPreviewByType(int type) {
        switch (type) {
            case TYPE_FILL:
                mLayoutParams.width = mScreenWidth;
                mLayoutParams.height = mScreenHeight;
                mIsHide = false;
                break;

            case TYPE_WRAP:

                break;

            case TYPE_HIDE:
                mLayoutParams.width = 1;
                mLayoutParams.height = 1;
                mIsHide = true;
                break;
        }

        if (mRootView.getParent() == null) {
            mWindowManager.addView(mRootView, mLayoutParams);
        } else {
            mWindowManager.updateViewLayout(mRootView, mLayoutParams);
        }
    }

    public void stopPreview() {
        if (mRootView.getParent() != null) {
            mWindowManager.removeView(mRootView);
        }
    }

    public Camera getCamera() {
        if (mCamera == null) {
            synchronized (FloatWindow.class) {
                if (mCamera == null) {
                    mCamera = Camera.open();
                }
            }
        }
        return mCamera;
    }

    public boolean getIsRecordVoice() {
        return cb_voice.isChecked();
    }

    public boolean getIsRecord() {
        return cb_record.isChecked();
    }

    public void setRecord(boolean record) {
        cb_record.setChecked(record);
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void switchCamera(int cameraId) {
        closeCamera();
        try {
            mCamera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        updateLayout(cameraId);
    }

    private void updateLayout(int cameraId) {
        if (cameraId == 0) {
            rl_line.setVisibility(View.GONE);
            mRlButton.setVisibility(View.VISIBLE);
        } else if (cameraId == 1) {
            mRlButton.setVisibility(View.GONE);
            rl_line.setVisibility(View.VISIBLE);
        }
    }

    public boolean getIsHide() {
        return mIsHide;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtils.e(getClass(), "surfaceCreated()");
        if (mCamera == null) {
            mCamera = Camera.open();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtils.e(getClass(), "surfaceChanged()");
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtils.e(getClass(), "surfaceDestroyed()");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void OnHit() {
        if (System.currentTimeMillis() - mLastHitTime < 3000) {
            return;
        }
        mLastHitTime = System.currentTimeMillis();

        boolean hitAutoSave = SettingBean.hitAutoSave;
        if (hitAutoSave && cb_record.isChecked()) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    EventBus.getDefault().post(RecordHelper.HIT_SAVE);
                }
            }, 3000);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_picture:
                EventBus.getDefault().post(RecordHelper.TAKE_PICTURE);
                break;
            case R.id.iv_file:
                EventBus.getDefault().post(RecordHelper.OPEN_FILE);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_voice:
                SpUtils.put("IsRecordVoice", isChecked);
                break;
            case R.id.cb_record:
                if (isChecked) {
                    startRecordTime();
                    EventBus.getDefault().post(RecordHelper.START_RECORD);
                } else {
                    stopRecordTime();
                    EventBus.getDefault().post(RecordHelper.STOP_RECORD);
                }
                break;
        }
    }

    private void startRecordTime() {
        chr_time.setBase(SystemClock.elapsedRealtime());
        chr_time.start();
    }

    private void stopRecordTime() {
        chr_time.stop();
        chr_time.setBase(SystemClock.elapsedRealtime());
    }
}
