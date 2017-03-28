package com.android.dvr.presenter;

import com.android.dvr.base.BasePresenter;
import com.android.dvr.constant.ModelHelper;
import com.android.dvr.contract.RecordContract;
import com.android.dvr.model.PhotoModel;
import com.android.dvr.model.VideoModel;
import com.android.dvr.mvp.IModel;
import com.android.dvr.service.RecordService;

import java.util.HashMap;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class RecordPresenter extends BasePresenter<RecordService> implements RecordContract.RecordPresenter {

    @Override
    public void startRecord() {
        ((VideoModel) getModel().get(ModelHelper.VIDEO)).startRecord();
    }

    @Override
    public void stopRecord() {
        ((VideoModel) getModel().get(ModelHelper.VIDEO)).stopRecord();
    }

    @Override
    public void hitSave() {
        ((VideoModel) getModel().get(ModelHelper.VIDEO)).setIsHit();
    }

    @Override
    public void captureVideo() {
        ((VideoModel) getModel().get(ModelHelper.VIDEO)).captureVideo();
    }

    @Override
    public void takePicture() {
        ((PhotoModel) getModel().get(ModelHelper.PHOTO)).takePicture();
    }

    @Override
    public void capturePicture() {
        ((PhotoModel) getModel().get(ModelHelper.PHOTO)).capturePicture();
    }

    @Override
    protected HashMap<String, IModel> getModel() {
        return loadModel(new VideoModel(), new PhotoModel());
    }

    @Override
    protected HashMap<String, IModel> loadModel(IModel... models) {
        HashMap<String, IModel> hashMap = new HashMap<>();
        hashMap.put(ModelHelper.VIDEO, models[0]);
        hashMap.put(ModelHelper.PHOTO, models[1]);
        return hashMap;
    }
}
