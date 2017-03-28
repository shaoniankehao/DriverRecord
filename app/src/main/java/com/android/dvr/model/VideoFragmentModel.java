package com.android.dvr.model;

import android.support.annotation.Nullable;

import com.android.dvr.bean.VideoBean;
import com.android.dvr.constant.ConfigHelper;
import com.android.dvr.mvp.IModel;
import com.android.dvr.util.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class VideoFragmentModel implements IModel {

    public void loadVideos(final HintInfo hintInfo) {
        if (hintInfo == null) {
            throw new RuntimeException("hintInfo不能为空");
        }

        Observable<ArrayList<VideoBean>> observable1 = Observable.create(new ObservableOnSubscribe<ArrayList<VideoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<VideoBean>> e) throws Exception {
                ArrayList<VideoBean> arrayList = getVideoFromFile(ConfigHelper.SAVE_PROTECTED_PATH);
                e.onNext(arrayList);
            }
        }).subscribeOn(Schedulers.io());

        Observable<ArrayList<VideoBean>> observable2 = Observable.create(new ObservableOnSubscribe<ArrayList<VideoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<VideoBean>> e) throws Exception {
                ArrayList<VideoBean> arrayList = getVideoFromFile(ConfigHelper.SAVE_RECORD_PATH);
                e.onNext(arrayList);
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<ArrayList<VideoBean>, ArrayList<VideoBean>, ArrayList<VideoBean>>() {
            @Override
            public ArrayList<VideoBean> apply(ArrayList<VideoBean> arrayList, ArrayList<VideoBean> arrayList2) throws Exception {
                LogUtils.e("protect", arrayList.toString());
                LogUtils.e("normal", arrayList2.toString());
                arrayList.addAll(arrayList2);
                return arrayList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<VideoBean>>() {
            @Override
            public void accept(ArrayList<VideoBean> arrayList) throws Exception {
                hintInfo.infoSuccess(arrayList);
            }
        });

    }

    @Nullable
    private ArrayList<VideoBean> getVideoFromFile(String filePath) {
        ArrayList<VideoBean> arrayList = new ArrayList<>();
        File file = new File(filePath);
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return arrayList;
        }
        for (int i = 0; i < listFiles.length; i++) {
            VideoBean videoBean = new VideoBean();
            if (listFiles[i].isDirectory()) {
                videoBean.setTitle(listFiles[i].getName());
                File[] files = listFiles[i].listFiles();
                for (int j = 0; j < files.length; j++) {
                    if (files[j].isFile() && files[j].getName().endsWith("mp4")) {
                        VideoBean.VideoInfo videoInfo = videoBean.new VideoInfo();
                        videoInfo.protective = true;
                        videoInfo.filePath = files[j].getAbsolutePath();
                        videoBean.getArrayList().add(videoInfo);
                    }
                }
            }
            arrayList.add(videoBean);
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public interface HintInfo {
        void infoSuccess(ArrayList<VideoBean> arrayList);
    }
}
