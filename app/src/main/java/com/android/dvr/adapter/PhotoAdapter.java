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

import java.util.ArrayList;

/**
 * Created by duanpeifeng on 2017/3/17 0017.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private Context              mContext;
    private ArrayList<PhotoBean> mData;

    public PhotoAdapter(Context context, ArrayList<PhotoBean> arrayList) {
        mContext = context;
        mData = arrayList;
    }

    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(LayoutInflater.from(mContext).inflate(R.layout.item_photo, null, false));
    }

    @Override
    public void onBindViewHolder(final PhotoHolder holder, final int position) {
        PhotoBean photoBean = mData.get(position);
        holder.tv.setText(photoBean.getTitle());
        holder.rv.setLayoutManager(new GridLayoutManager(mContext, 4));
        PhotoContentAdapter adapter = new PhotoContentAdapter(mContext, photoBean.getArrayList());
        holder.rv.setAdapter(adapter);
        adapter.setOnItemClickListener(new PhotoContentAdapter.OnItemClickListener() {
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

    class PhotoHolder extends RecyclerView.ViewHolder {
        TextView     tv;
        RecyclerView rv;

        public PhotoHolder(View itemView) {
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
