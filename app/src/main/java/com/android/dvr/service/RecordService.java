package com.android.dvr.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.android.dvr.view.DisplayActivity;
import com.android.dvr.constant.RecordHelper;
import com.android.dvr.mvp.IView;
import com.android.dvr.presenter.RecordPresenter;
import com.android.dvr.util.LogUtils;
import com.android.dvr.view.FloatWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by duanpeifeng on 2017/3/10 0010.
 */

public class RecordService extends Service implements IView {
    private RecordPresenter       mPresenter;
    private PowerManager.WakeLock mWakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecordBinder();
    }

    public class RecordBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        mPresenter = new RecordPresenter();
        mPresenter.attachView(this);

        // 获取锁，防止设备进入深度休眠
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, RecordService.class.getSimpleName());
        if (mWakeLock != null) {
            mWakeLock.acquire();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FloatWindow.getInstance().startPreviewByType(FloatWindow.TYPE_HIDE);
        return START_NOT_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestRecordEvent(String event) {
        LogUtils.e(getClass(), event);
        switch (event) {
            case RecordHelper.START_RECORD:
                mPresenter.startRecord();
                break;

            case RecordHelper.STOP_RECORD:
                mPresenter.stopRecord();
                break;

            case RecordHelper.HIT_SAVE:
                mPresenter.hitSave();
                break;

            case RecordHelper.CAPTURE_VIDEO:
                mPresenter.captureVideo();
                break;

            case RecordHelper.TAKE_PICTURE:
                mPresenter.takePicture();
                break;

            case RecordHelper.CAPTURE_PICTURE:
                mPresenter.capturePicture();
                break;

            case RecordHelper.OPEN_FILE:
                Intent intent = new Intent(this, DisplayActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
        EventBus.getDefault().unregister(this);
        FloatWindow.getInstance().stopPreview();
    }
}
