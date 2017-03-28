package com.android.dvr.contract;

import com.android.dvr.bean.VideoBean;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class VideoFragmentContract {

    public interface VideoFragment {
        void showVideos(ArrayList<VideoBean> arrayList);
    }

    public interface VideoFragmentPresenter {
        void loadVideos();
    }
}
