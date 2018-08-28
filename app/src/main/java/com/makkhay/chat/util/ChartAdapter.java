package com.makkhay.chat.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makkhay.chat.R;
import com.makkhay.chat.model.ChartModel;
import com.makkhay.chat.ui.BarChartActivity;
import com.makkhay.chat.ui.HorizontalBarChartActivity;
import com.makkhay.chat.ui.LineChartActivity;
import com.makkhay.chat.ui.PieChartActivity;
import com.makkhay.chat.ui.SingleLineActivity;

import java.util.List;


public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.MyViewHolder> {

    private Context mContext;
    private List<ChartModel> albumList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public ChartAdapter(Context mContext, List<ChartModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    public boolean isHeader(int position) {
        return position == 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chart_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ChartModel album = albumList.get(position);
        holder.title.setText(album.getName());



        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = position;
                if (pos == 0) {
                    Intent i = new Intent(mContext, SingleLineActivity.class);
                    mContext.startActivity(i);
                }
                if (pos == 1) {
                    Intent i = new Intent(mContext, LineChartActivity.class);
                    mContext.startActivity(i);
                }
                if (pos == 2) {
                    Intent i = new Intent(mContext, BarChartActivity.class);
                    mContext.startActivity(i);
                }
                if (pos == 3) {
                    Intent i = new Intent(mContext, PieChartActivity.class);
                    mContext.startActivity(i);
                }
                if (pos == 4) {
                    Intent i = new Intent(mContext, HorizontalBarChartActivity.class);
                    mContext.startActivity(i);

                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
