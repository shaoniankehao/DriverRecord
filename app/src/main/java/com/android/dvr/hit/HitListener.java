package com.android.dvr.hit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.android.dvr.ProApplication;

/**
 * Created by duanpeifeng on 2017/3/10 0010.
 */

public class HitListener implements OnSettingChangeListener, SensorEventListener {

    private static HitListener   mInstance;
    private        OnHitListener mListener;
    private float[] values = new float[3];

    private boolean isFirst   = true;
    private int     level     = 6;
    // 临界值
    private float   threshold = 0;
    // 一个标准单位
    private float   unitValue = 9.8f / 25;
    // 最小单位
    private float   minValue  = 9.8f * 28 / 10;

    private HitListener() {
        SettingBean.setSettingListener(this);
        SensorManager sensorMgr = (SensorManager) ProApplication.getContext().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        setLevelAndThreshold();
    }

    public static HitListener getInstance() {
        if (null == mInstance) {
            synchronized (HitListener.class) {
                if (null == mInstance) {
                    mInstance = new HitListener();
                }
            }
        }
        return mInstance;
    }

    private void setLevelAndThreshold() {
        level = SettingBean.hitDelicacy;
        threshold = (30 - level) * unitValue + minValue;
        if (level == 0) {
            threshold = 40;
        }
    }

    public void setListener(OnHitListener listener) {
        mListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];

        // 撞击不保存
        if (!SettingBean.hitAutoSave) {
            values[0] = x;
            values[1] = y;
            values[2] = z;
            isFirst = false;
        } else {
            if (isFirst) {
                values[0] = x;
                values[1] = y;
                values[2] = z;
                isFirst = false;
            } else {
                if (Math.abs(x - values[0]) > threshold || Math.abs(y - values[1]) > threshold || Math.abs(z - values[2]) > threshold) {
                    if (null != mListener) {
                        mListener.OnHit();
                        Log.d("leo", "on hit g = x = " + x + " y = " + y + " z = " + z);
                    }
                }
                values[0] = x;
                values[1] = y;
                values[2] = z;
            }
        }
    }

    @Override
    public void onSettingChange(int type) {
        setLevelAndThreshold();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
