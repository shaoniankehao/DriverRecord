package com.android.dvr.constant;

import com.android.dvr.model.PhotoDataModel;
import com.android.dvr.model.PhotoModel;
import com.android.dvr.model.VideoDataModel;
import com.android.dvr.model.VideoModel;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public interface ModelHelper {
    String VIDEO = VideoModel.class.getSimpleName();

    String PHOTO = PhotoModel.class.getSimpleName();

    String PHOTOFRAGMENT = PhotoDataModel.class.getSimpleName();

    String VIDEOFRAGMENT = VideoDataModel.class.getSimpleName();
}
