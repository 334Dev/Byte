package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.mViewholder>{


    private List<Model_Comment> item_list;
    private CommentAdapter.SelectedItem selectedItem;
    public CommentAdapter(List<Model_Comment> item_list, CommentAdapter.SelectedItem selectedItem){
        this.item_list=item_list;
        this.selectedItem=selectedItem;
    }
    @NonNull
    @Override

    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentitem, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return new CommentAdapter.mViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {

        holder.setComment(item_list.get(position).getComment());
        holder.setDate(item_list.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface SelectedItem{
        void selectedItem(Model_Comment model_comment);
    }

    public class mViewholder extends RecyclerView.ViewHolder{


        private TextView CommentTextView;
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
    }

}
