package com.android.dvr.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.dvr.mvp.IView;

import org.greenrobot.eventbus.EventBus;


/**
 * @创建者 duanp
 * @创建时间 2017/3/5 0005
 * @描述 ${TODO}
 */

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IView {
    protected P    mPresenter;
    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getRelayoutId(), null);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // 只要Activity没有被销毁，此方法不会被重复调用
        super.onActivityCreated(savedInstanceState);
        initView();
        initPresenter();
        initData();
        initEvent();
    }

    private void initPresenter() {
        mPresenter = loadPresenter();
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    protected abstract int getRelayoutId();

    protected abstract void initView();

    protected abstract void initData();

    protected abstract void initEvent();

    protected abstract P loadPresenter();
}
