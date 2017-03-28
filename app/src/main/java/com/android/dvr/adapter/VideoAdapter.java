package com.android.dvr.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.dvr.R;
import com.android.dvr.bean.PhotoBean;
import com.android.dvr.bean.VideoBean;

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {
    private Context              mContext;
    private ArrayList<VideoBean> mData;

    public VideoAdapter(Context context, ArrayList<VideoBean> arrayList) {
        mContext = context;
        mData = arrayList;
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video, null, false));
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, final int position) {
        VideoBean videoBean = mData.get(position);
        holder.tv.setText(videoBean.getTitle());
        holder.rv.setLayoutManager(new GridLayoutManager(mContext, 4));
        VideoContentAdapter adapter = new VideoContentAdapter(mContext, videoBean.getArrayList());
        holder.rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new VideoContentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String path) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(path);
                }
            }

            @Override
            public void onItemLongClick(String path) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(path);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {
        TextView     tv;
        RecyclerView rv;

        public VideoHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            rv = (RecyclerView) itemView.findViewById(R.id.rv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String path);

        void onItemLongClick(String path);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
