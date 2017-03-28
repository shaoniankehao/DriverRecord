package com.android.dvr.contract;

import com.android.dvr.bean.PhotoBean;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/23 0023.
 */

public class PhotoFragmentContract {

    public interface PhotoFragment {
        void showPhotos(ArrayList<PhotoBean> arrayList);
    }

    public interface PhotoFragmentPresenter {
        void loadPhotos();
    }
}
