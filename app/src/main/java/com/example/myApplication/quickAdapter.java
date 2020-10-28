package com.example.myApplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class quickAdapter extends RecyclerView.Adapter<quickAdapter.mViewHolder>{

    private List<quickModel> viewPagerModel;
    private SelectedPager selectedpager;

    public quickAdapter(List<quickModel> viewPagerModel,SelectedPager selectedpager){
        this.viewPagerModel=viewPagerModel;
        this.selectedpager=selectedpager;
    }

    @NonNull
    @Override
    public quickAdapter.mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_item_layout, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new quickAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull quickAdapter.mViewHolder holder, int position) {
        holder.setCoverImage(viewPagerModel.get(position).getImage());
        holder.setDesc(viewPagerModel.get(position).getDesc());
        holder.setTitle(viewPagerModel.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return viewPagerModel.size();
    }
    public interface SelectedPager{
        void selectedpager(quickModel viewPagerModel);
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView coverImage;
        TextView Title, Desc;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedpager.selectedpager(viewPagerModel.get(getAdapterPosition()));
                }
            });
        }

        public void setCoverImage(String img){
            coverImage=view.findViewById(R.id.quickCover);
            Picasso.get().load(img).into(coverImage);
        }
        public void setTitle(String title){
            Title=view.findViewById(R.id.quickTITle);
            Title.setText(title);
        }
        public void setDesc(String desc){
            Desc=view.findViewById(R.id.quickDescription);
            Desc.setText(desc);
        }
    }
}

