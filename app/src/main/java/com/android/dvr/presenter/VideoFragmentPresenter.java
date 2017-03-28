package com.android.dvr.presenter;

import com.android.dvr.base.BasePresenter;
import com.android.dvr.bean.VideoBean;
import com.android.dvr.constant.ModelHelper;
import com.android.dvr.contract.VideoFragmentContract;
import com.android.dvr.model.VideoFragmentModel;
import com.android.dvr.mvp.IModel;
import com.android.dvr.view.VideoFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class VideoFragmentPresenter extends BasePresenter<VideoFragment> implements VideoFragmentContract.VideoFragmentPresenter{

    @Override
    public void loadVideos() {
        ((VideoFragmentModel)getModel().get(ModelHelper.VIDEOFRAGMENT)).loadVideos(new VideoFragmentModel.HintInfo() {
            @Override
            public void infoSuccess(ArrayList<VideoBean> arrayList) {
                getView().showVideos(arrayList);
            }
        });
    }

    @Override
    protected HashMap<String, IModel> getModel() {
        return loadModel(new VideoFragmentModel());
    }

    @Override
    protected HashMap<String, IModel> loadModel(IModel... models) {
        HashMap<String,IModel> hashMap = new HashMap<>();
        hashMap.put(ModelHelper.VIDEOFRAGMENT, models[0]);
        return hashMap;
    }

}
