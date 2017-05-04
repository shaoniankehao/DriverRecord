package com.android.dvr.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.dvr.R;
import com.android.dvr.adapter.MediaAdapter;
import com.android.dvr.base.BaseFragment;
import com.android.dvr.bean.DisplayEvent;
import com.android.dvr.bean.MediaBean;
import com.android.dvr.contract.MediaDataContract;
import com.android.dvr.presenter.VideoDataPresenter;
import com.android.dvr.util.LogUtils;
import com.viewpagerindicator.TabPageIndicator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class VideoFragment extends BaseFragment<VideoDataPresenter> implements MediaDataContract.DataFragment {
    private RecyclerView     mRv;
    private MediaAdapter     mAdapter;
    private TabPageIndicator mIndicator;
    private RelativeLayout   ll_title;
    private ArrayList<MediaBean> mArrayList = new ArrayList<>();

    @Override
    protected int getRelayoutId() {
        return R.layout.fragment_video;
    }

    @Override
    protected void initView() {
        mIndicator = (TabPageIndicator) getActivity().findViewById(R.id.indicator);
        ll_title = (RelativeLayout) getActivity().findViewById(R.id.ll_title);

        mRv = (RecyclerView) rootView.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MediaAdapter(getActivity(), mArrayList);
        mRv.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        mPresenter.loadMedias();
    }

    @Override
    protected void initEvent() {
        mAdapter.setOnItemClickListener(new MediaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String filePath, boolean isEdited) {
                if (!isEdited) {
                    //调用系统自带的播放器
                    Uri uri = Uri.parse(filePath);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/mp4");
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick() {
                mIndicator.setVisibility(View.GONE);
                ll_title.setVisibility(View.VISIBLE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void disposeEvent(DisplayEvent displayEvent) {
        LogUtils.e("TAG", "disposeEvent-video");
        if (displayEvent.getPageIndex() == 0) {
            return;
        }
        switch (displayEvent.getButtonId()) {
            case 0:
                changeMediaState(false);
                mIndicator.setVisibility(View.VISIBLE);
                ll_title.setVisibility(View.GONE);
                break;

            case 1:
                changeMediaState(displayEvent.isChecked());
                break;

            case 2:
                new MaterialDialog.Builder(getActivity())
                        .content("确定删除这些视频？")
                        .positiveText("yes")
                        .negativeText("no")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final ArrayList<String> deleteList = new ArrayList<>();
                                for (int i = 0; i < mArrayList.size(); i++) {
                                    ArrayList<MediaBean.MediaInfo> arrayList = mArrayList.get(i).getArrayList();
                                    for (int j = 0; j < arrayList.size(); j++) {
                                        MediaBean.MediaInfo mediaInfo = arrayList.get(j);
                                        if (mediaInfo.selected) {
                                            deleteList.add(mediaInfo.filePath);
                                            arrayList.remove(mediaInfo);
                                        }
                                    }
                                }
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < deleteList.size(); i++) {
                                            File file = new File(deleteList.get(i));
                                            file.delete();
                                        }
                                    }
                                }).start();
                            }
                        }).show();
                mIndicator.setVisibility(View.VISIBLE);
                ll_title.setVisibility(View.GONE);
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void changeMediaState(boolean isSelect) {
        for (MediaBean mediaBean : mArrayList) {
            ArrayList<MediaBean.MediaInfo> arrayList = mediaBean.getArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayList.get(i).selected = isSelect;
            }
        }
    }

    @Override
    public void showMedias(ArrayList<MediaBean> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            LogUtils.e(getClass(), arrayList.get(i).toString());
        }
        mArrayList.addAll(arrayList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected VideoDataPresenter loadPresenter() {
        return new VideoDataPresenter();
    }
}
