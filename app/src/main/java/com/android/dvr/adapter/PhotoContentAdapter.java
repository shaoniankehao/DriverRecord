package com.android.dvr.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.dvr.util.LogUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.android.dvr.R;

/**
 * Created by duanpeifeng on 2017/3/20 0020.
 */

public class PhotoContentAdapter extends RecyclerView.Adapter<PhotoContentAdapter.ImageHolder> {
    private Context           mContext;
    private ArrayList<String> mData;

    public PhotoContentAdapter(Context context, ArrayList<String> arrayList) {
        mContext = context;
        mData = arrayList;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_photo_content, null, false);

        return new ImageHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        Glide.with(mContext).load(mData.get(position)).into(holder.iv_picture);
        holder.iv_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(mData.get(position));
                }
            }
        });
        holder.iv_picture.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemLongClick(mData.get(position));
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        private ImageView iv_picture;

        public ImageHolder(View itemView) {
            super(itemView);
            iv_picture = (ImageView) itemView.findViewById(R.id.iv);
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
