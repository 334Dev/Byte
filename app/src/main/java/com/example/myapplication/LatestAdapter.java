package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ViewHolder> {
    List<Model_Latest> itemList1;

    public LatestAdapter(List<Model_Latest> itemList)
    {
        this.itemList1=itemList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.itemImage.setImageResource(itemList1.get(position).getImg());
        holder.Title.setText(itemList1.get(position).getTitle());
        holder.Desc.setText(itemList1.get(position).getDesc());
        holder.viewCountTxt.setText(itemList1.get(position).getViewCount());
        holder.time.setText(Integer.toString(itemList1.get(position).getTime()));

    }

    @Override
    public int getItemCount() {
        return itemList1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView Title;
        TextView Desc;
        TextView viewCountTxt;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage=itemView.findViewById(R.id.latest_image);
            Title=itemView.findViewById(R.id.post_title);
            Desc=itemView.findViewById(R.id.post_description);
            viewCountTxt=itemView.findViewById(R.id.view_count);
            time=itemView.findViewById(R.id.time);


        }
    }
}
