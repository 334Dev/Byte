package com.example.myapplication;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class userSearchAdapter extends RecyclerView.Adapter<LatestAdapter.mViewholder>{
    private List<userSearchModel> userList;
    
    @NonNull
    @Override
    public LatestAdapter.mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LatestAdapter.mViewholder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class mViewholder extends RecyclerView.ViewHolder{

        public mViewholder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
