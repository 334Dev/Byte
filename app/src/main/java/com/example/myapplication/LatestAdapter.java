package com.example.myapplication;

import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.mViewholder> {

    private List<Model_Latest> item_list;
    private OnNoteListener mOnNoteListener;
    public LatestAdapter(List<Model_Latest> item_list,OnNoteListener onNoteListener){
        this.item_list=item_list;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new LatestAdapter.mViewholder(view, mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {
        //holder.setThumbView(item_list.get(position).img);
        holder.setTime(item_list.get(position).time);
        holder.setTitle(item_list.get(position).title);
        holder.setDesc(item_list.get(position).desc);
        holder.setViewCount(item_list.get(position).viewCount);
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public class mViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView thumbView;
        private TextView title,desc,time,viewCount;
        private View view;
        private OnNoteListener onNoteListener;

        public mViewholder(@NonNull View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            view=itemView;
            this.onNoteListener = onNoteListener;
        }
        public void setThumbView(Integer imageView){
            thumbView=view.findViewById(R.id.latest_image);
            thumbView.setImageResource(imageView);
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
            time.setText(Time.toString());
        }
        public void setViewCount(Integer ViewCount){
            viewCount=view.findViewById(R.id.view_count);
            viewCount.setText(ViewCount.toString());
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }
    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
