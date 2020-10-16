package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.mViewholder> {

    private List<Model_Latest> item_list;
    private SelectedItem selectedItem;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public LatestAdapter(List<Model_Latest> item_list, SelectedItem selectedItem){
        this.item_list=item_list;
        this.selectedItem=selectedItem;
    }

    @NonNull
    @Override
    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new LatestAdapter.mViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {
        holder.setThumbView(item_list.get(position).img);
        holder.setTime(item_list.get(position).time);
        holder.setTitle(item_list.get(position).title);
        holder.setDesc(item_list.get(position).desc);
        holder.setViewCount(item_list.get(position).viewCount);
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public interface SelectedItem{
        void selectedItem(Model_Latest model_latest);
    }

    public class mViewholder extends RecyclerView.ViewHolder{

        private ImageView thumbView;
        private TextView title,desc,time,viewCount;
        private View view;

        public mViewholder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem.selectedItem(item_list.get(getAdapterPosition()));
                }
            });
        }

        public void setThumbView(String imageView){
            thumbView=view.findViewById(R.id.latest_image);
            Picasso.get().load(imageView).into(thumbView);
        }
        public void setTitle(String Title){
            title=view.findViewById(R.id.post_title);
            title.setText(Title);
        }
        public void setDesc(String Desc){
            desc=view.findViewById(R.id.post_description);
            desc.setText(Desc);
        }
        public void setTime(Long Time){

            time=view.findViewById(R.id.postTime);
            time.setText(getTimeAgo(Time));
        }
        public void setViewCount(Integer ViewCount){
            viewCount=view.findViewById(R.id.view_count);
            viewCount.setText(ViewCount.toString());
        }

        public String getTimeAgo(long time) {
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }

            // TODO: localize
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " days ago";
            }
        }

    }


}
