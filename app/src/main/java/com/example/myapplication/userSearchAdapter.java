package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class userSearchAdapter extends RecyclerView.Adapter<userSearchAdapter.mViewholder> {
    private List<userSearchModel> userList;
    private SelectedItem selectedItem;

    public userSearchAdapter(List<userSearchModel> userList, SelectedItem selectedItem){
        this.userList=userList;
        this.selectedItem=selectedItem;
    }

    
    @NonNull
    @Override
    public mViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_list, parent, false);
        //view.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new userSearchAdapter.mViewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull mViewholder holder, int position) {

           holder.setText(userList.get(position).Username);

    }

    public interface SelectedItem{
        void selectedItem(userSearchModel userModel);
    }

    @Override
    public int getItemCount() {

        return userList.size();
    }

    public class mViewholder extends RecyclerView.ViewHolder{

        private View view;
        private TextView userNameList;

        public mViewholder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedItem.selectedItem(userList.get(getAdapterPosition()));
                }
            });
        }
        public void setText(String userName)
        {
            userNameList=view.findViewById(R.id.UserListText);
            userNameList.setText(userName);
        }
    }


}
