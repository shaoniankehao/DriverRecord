package com.android.dvr.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.android.dvr.R;
import com.android.dvr.adapter.ViewpagerAdapter;
import com.android.dvr.base.BaseActivity;
import com.android.dvr.base.BasePresenter;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class DisplayActivity extends BaseActivity {
    @InjectView(R.id.indicator)
    TabPageIndicator mIndicator;
    @InjectView(R.id.vp)
    ViewPager        mVp;
    @InjectView(R.id.iv)
    ImageView        mIv;

    private ArrayList<Fragment> mArrayList = new ArrayList<>();

    @Override
    protected int getRelayoutId() {
        return R.layout.activity_display;
    }

    @Override
    protected void initData() {
        mArrayList.add(new PhotoFragment());
        mArrayList.add(new VideoFragment());
        mVp.setAdapter(new ViewpagerAdapter(getSupportFragmentManager(), mArrayList));
        mIndicator.setViewPager(mVp);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected BasePresenter loadPresenter() {
        return null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIv.getVisibility() == ImageView.VISIBLE) {
                mIv.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
