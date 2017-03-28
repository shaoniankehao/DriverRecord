package com.android.dvr.presenter;

import com.android.dvr.base.BasePresenter;
import com.android.dvr.bean.PhotoBean;
import com.android.dvr.constant.ModelHelper;
import com.android.dvr.contract.PhotoFragmentContract;
import com.android.dvr.model.PhotoFragmentModel;
import com.android.dvr.mvp.IModel;
import com.android.dvr.view.PhotoFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class PhotoFragmentPresenter extends BasePresenter<PhotoFragment> implements PhotoFragmentContract.PhotoFragmentPresenter {

    @Override
    public void loadPhotos() {
        ((PhotoFragmentModel) getModel().get(ModelHelper.PHOTOFRAGMENT)).loadPhotos(new PhotoFragmentModel.HintInfo() {
            @Override
            public void infoSuccess(ArrayList<PhotoBean> arrayList) {
                getView().showPhotos(arrayList);
            }
        });
    }

    @Override
    protected HashMap<String, IModel> getModel() {
        return loadModel(new PhotoFragmentModel());
    }

    @Override
    protected HashMap<String, IModel> loadModel(IModel... models) {
        HashMap<String, IModel> hashMap = new HashMap<>();
        hashMap.put(ModelHelper.PHOTOFRAGMENT, models[0]);
        return hashMap;
    }

}
