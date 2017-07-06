package com.esint.photopicker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.esint.photopicker.Activity.ShowImageActivity;
import com.esint.photopicker.Bean.Image;
import com.esint.photopicker.R;

import java.util.List;

/**
 * 显示图片RecyclerView的Adapter
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private Context mContext;
    private List<Image> mData;

    public ImageAdapter(Context context, List<Image> data) {
        this.mContext = context;
        this.mData = data;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Image image = mData.get(position);
                Intent intent = new Intent(mContext, ShowImageActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("count", mData.size());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Image image = mData.get(position);
        Glide.with(mContext)
                .load(image.getImgUri())
                .into(holder.iv_image);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
