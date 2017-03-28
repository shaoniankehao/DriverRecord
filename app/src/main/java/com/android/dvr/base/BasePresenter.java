package com.android.dvr.base;


import com.android.dvr.mvp.IModel;
import com.android.dvr.mvp.IPresenter;
import com.android.dvr.mvp.IView;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @创建者 duanp
 * @创建时间 2017/2/24 0024
 * @描述 ${TODO}
 */

public abstract class BasePresenter<V extends IView> implements IPresenter {
    private WeakReference mWeakReference;

    @Override
    public void attachView(IView view) {
        mWeakReference = new WeakReference(view);
    }

    @Override
    public void detachView() {
        if (mWeakReference != null) {
            mWeakReference.clear();
        }
    }

    @Override
    public V getView() {
        return (V) mWeakReference.get();
    }

    protected abstract HashMap<String, IModel> getModel();

    protected abstract HashMap<String, IModel> loadModel(IModel... models);
}
