package com.android.dvr.constant;

import com.android.dvr.model.PhotoFragmentModel;
import com.android.dvr.model.PhotoModel;
import com.android.dvr.model.VideoFragmentModel;
import com.android.dvr.model.VideoModel;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public interface ModelHelper {
    String VIDEO = VideoModel.class.getSimpleName();

    String PHOTO = PhotoModel.class.getSimpleName();

    String PHOTOFRAGMENT = PhotoFragmentModel.class.getSimpleName();

    String VIDEOFRAGMENT = VideoFragmentModel.class.getSimpleName();
}
