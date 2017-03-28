package com.android.dvr.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.dvr.constant.RecordHelper;
import com.android.dvr.service.RecordService;
import com.android.dvr.util.SDCardUtils;
import com.android.dvr.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class SDCardReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        // 监听卸载sdcard
        if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
            EventBus.getDefault().post(RecordHelper.STOP_RECORD);

            // 监听安装sdcard
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
            if (SDCardUtils.isFirstFormatSdcard()) {
                formatSDCard(context);
            } else {
                Intent recordService = new Intent(context, RecordService.class);
                context.startService(recordService);
                EventBus.getDefault().post(RecordHelper.START_RECORD);
            }
        }
    }

    private void formatSDCard(Context context) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(context)
                .content("正在格式化SD卡...")
                .progress(true, 10)
                .cancelable(true)
                .showListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        SDCardUtils.formatSDCard(new SDCardUtils.FormatHint() {
                            @Override
                            public void success() {
                                dialog.dismiss();
                                EventBus.getDefault().post(RecordHelper.START_RECORD);
                            }

                            @Override
                            public void failed() {
                                dialog.dismiss();
                                ToastUtil.setToast("SD卡格式化失败，请手动格式SD卡");
                            }
                        });
                    }
                })
                .build();
        materialDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        materialDialog.show();
    }
}
