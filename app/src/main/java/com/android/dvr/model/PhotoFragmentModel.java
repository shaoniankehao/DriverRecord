package com.android.dvr.model;

import android.support.annotation.Nullable;

import com.android.dvr.bean.PhotoBean;
import com.android.dvr.constant.ConfigHelper;
import com.android.dvr.mvp.IModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class PhotoFragmentModel implements IModel {

    public void loadPhotos(final HintInfo hintInfo) {
        if (hintInfo == null) {
            throw new RuntimeException("hintInfo不能为空");
        }

        Observable.create(new ObservableOnSubscribe<ArrayList<PhotoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<PhotoBean>> e) throws Exception {
                e.onNext(getPhotoFromFile(ConfigHelper.SAVE_CAPTURE_PATH));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<PhotoBean>>() {
                    @Override
                    public void accept(ArrayList<PhotoBean> arrayList) throws Exception {
                        hintInfo.infoSuccess(arrayList);
                    }
                });
    }

    @Nullable
    private ArrayList<PhotoBean> getPhotoFromFile(String filePath) {
        ArrayList<PhotoBean> arrayList = new ArrayList<>();
        File file = new File(filePath);
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return null;
        }
        for (int i = 0; i < listFiles.length; i++) {
            PhotoBean photoBean = new PhotoBean();
            if (listFiles[i].isDirectory()) {
                photoBean.setTitle(listFiles[i].getName());
                File[] files = listFiles[i].listFiles();
                for (int j = 0; j < files.length; j++) {
                    if (files[j].isFile() && files[j].getName().endsWith("jpg")) {
                        photoBean.getArrayList().add(files[j].getAbsolutePath());
                    }
                }
            }
            arrayList.add(photoBean);
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public interface HintInfo {
        void infoSuccess(ArrayList<PhotoBean> arrayList);
    }
}
