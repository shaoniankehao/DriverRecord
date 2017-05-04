package com.android.dvr.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.dvr.R;
import com.android.dvr.adapter.ViewpagerAdapter;
import com.android.dvr.base.BaseActivity;
import com.android.dvr.base.BasePresenter;
import com.android.dvr.bean.DisplayEvent;
import com.viewpagerindicator.TabPageIndicator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class DisplayActivity extends BaseActivity {
    @InjectView(R.id.indicator)
    TabPageIndicator  mIndicator;
    @InjectView(R.id.vp)
    NoScrollViewPager mVp;
    @InjectView(R.id.iv)
    ImageView         mIv;
    @InjectView(R.id.ll_title)
    RelativeLayout    ll_Title;
    @InjectView(R.id.cb_select)
    CheckBox          cb_Select;

    private ArrayList<Fragment> mArrayList = new ArrayList<>();
    private DisplayEvent        mEvent     = new DisplayEvent();

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
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mEvent.setPageIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        cb_Select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cb_Select.setText("取消全选");
                } else {
                    cb_Select.setText("全选");
                }
                mEvent.setButtonId(1);
                mEvent.setChecked(isChecked);
                EventBus.getDefault().post(mEvent);
            }
        });
    }

    @Override
    protected BasePresenter loadPresenter() {
        return null;
    }

    @OnClick({R.id.iv_back, R.id.iv_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                mEvent.setButtonId(0);
                EventBus.getDefault().post(mEvent);
                break;

            case R.id.iv_delete:
                mEvent.setButtonId(2);
                EventBus.getDefault().post(mEvent);
                break;
        }
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
