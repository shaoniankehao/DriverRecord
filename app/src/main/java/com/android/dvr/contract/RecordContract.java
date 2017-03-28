package com.android.dvr.contract;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class RecordContract {
    public interface RecordService {

    }

    public interface RecordPresenter {
        void startRecord();

        void stopRecord();

        void hitSave();

        void takePicture();

        void captureVideo();

        void capturePicture();
    }

    public interface VideoModel {
        void startRecord();

        void stopRecord();

        void setIsHit();

        void captureVideo();
    }

    public interface PhotoModel {
        void takePicture();

        void capturePicture();
    }
}
