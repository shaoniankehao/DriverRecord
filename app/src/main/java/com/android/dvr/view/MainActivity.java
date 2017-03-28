package com.android.dvr.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.KeyEvent;

import com.android.dvr.R;
import com.android.dvr.base.BaseActivity;
import com.android.dvr.base.BasePresenter;
import com.android.dvr.service.RecordService;
import com.android.dvr.view.FloatWindow;

public class MainActivity extends BaseActivity {

    private ServiceConnection mServiceConnection;
    private RecordService     mRecordService;

    @Override
    protected int getRelayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mRecordService = ((RecordService.RecordBinder) iBinder).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mRecordService = null;
            }
        };
    }

    @Override
    protected void initEvent() {
        Intent intent = new Intent(this, RecordService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected BasePresenter loadPresenter() {
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatWindow.getInstance().startPreviewByType(FloatWindow.TYPE_FILL);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FloatWindow.getInstance().startPreviewByType(FloatWindow.TYPE_HIDE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
