package com.example.myApplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class commentAdapter extends RecyclerView.Adapter<commentAdapter.mViewholder>{


    private List<commentModel> item_list;
    private commentAdapter.SelectedItem selectedItem;
    public commentAdapter(List<commentModel> item_list, commentAdapter.SelectedItem selectedItem){
        this.item_list=item_list;
        this.selectedItem=selectedItem;
    }
    @NonNull
    @Override

    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentitem, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new commentAdapter.mViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {

        holder.setComment(item_list.get(position).getComment());
        holder.setDate(item_list.get(position).getDate());
        holder.setUsername(item_list.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public interface SelectedItem{
        void selectedItem(commentModel commentModel_);
    }

    public class mViewholder extends RecyclerView.ViewHolder{


        private TextView CommentTextView, commentUser;
        private TextView Date;
        private View v;

        public mViewholder(@NonNull View itemView) {
            super(itemView);
            v=itemView;


        }


        public void setComment(String cmt) {
            CommentTextView=v.findViewById(R.id.commentText);
            CommentTextView.setText(cmt);
        }

        public void setDate(String date) {
            Date=v.findViewById(R.id.commentTime);
            Date.setText(date);
        }
        public void setUsername(String username){
            commentUser=v.findViewById(R.id.commentUser);
            commentUser.setText(username);
        }
    }

}
