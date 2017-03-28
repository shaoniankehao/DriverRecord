package com.android.dvr.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.dvr.R;
import com.android.dvr.adapter.VideoAdapter;
import com.android.dvr.base.BaseFragment;
import com.android.dvr.bean.VideoBean;
import com.android.dvr.contract.VideoFragmentContract;
import com.android.dvr.presenter.VideoFragmentPresenter;
import com.android.dvr.util.LogUtils;

import java.util.ArrayList;


/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class VideoFragment extends BaseFragment<VideoFragmentPresenter> implements VideoFragmentContract.VideoFragment {
    private RecyclerView mRv;
    private VideoAdapter mAdapter;
    private ArrayList<VideoBean> mArrayList = new ArrayList<>();

    @Override
    protected int getRelayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        mRv = (RecyclerView) rootView.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new VideoAdapter(getActivity(), mArrayList);
        mRv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mPresenter.loadVideos();
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new VideoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path) {
                Uri uri = Uri.parse(path);
                //调用系统自带的播放器
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "video/mp4");
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(String path) {

            }
        });
    }

    @Override
    public void showVideos(ArrayList<VideoBean> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            LogUtils.e(getClass(), arrayList.get(i).toString());
        }
        mArrayList.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected VideoFragmentPresenter loadPresenter() {
        return new VideoFragmentPresenter();
    }
}
