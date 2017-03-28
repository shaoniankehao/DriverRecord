package com.android.dvr.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class ViewpagerAdapter extends FragmentPagerAdapter {
    private static final String[] mTitle = {"照片", "视频"};

    private ArrayList<Fragment> mArrayList = new ArrayList<>();

    public ViewpagerAdapter(FragmentManager fragment, ArrayList<Fragment> arrayList) {
        super(fragment);
        mArrayList = arrayList;
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mArrayList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //重写不让fragment销毁
    }
}
