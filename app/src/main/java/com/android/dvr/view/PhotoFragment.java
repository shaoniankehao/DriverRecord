package com.android.dvr.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.dvr.R;
import com.android.dvr.adapter.PhotoAdapter;
import com.android.dvr.base.BaseFragment;
import com.android.dvr.bean.PhotoBean;
import com.android.dvr.contract.PhotoFragmentContract;
import com.android.dvr.presenter.PhotoFragmentPresenter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class PhotoFragment extends BaseFragment<PhotoFragmentPresenter> implements PhotoFragmentContract.PhotoFragment {
    private RecyclerView mRv;
    private ImageView    mIv;
    private PhotoAdapter mAdapter;
    private ArrayList<PhotoBean> mArrayList = new ArrayList<>();

    @Override
    protected int getRelayoutId() {
        return R.layout.fragment_photo;
    }

    @Override
    protected void initView() {
        mIv = (ImageView) getActivity().findViewById(R.id.iv);
        mRv = (RecyclerView) rootView.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PhotoAdapter(getActivity(), mArrayList);
        mRv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mPresenter.loadPhotos();
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new PhotoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path) {
                mIv.setVisibility(View.VISIBLE);
                Glide.with(getActivity()).load(path).into(mIv);
            }

            @Override
            public void onItemLongClick(String path) {

            }
        });

        mIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIv.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showPhotos(ArrayList<PhotoBean> arrayList) {
        mArrayList.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected PhotoFragmentPresenter loadPresenter() {
        return new PhotoFragmentPresenter();
    }
}
