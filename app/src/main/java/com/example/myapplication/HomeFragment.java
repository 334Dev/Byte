package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment()
    {

    }
   RecyclerView recyclerView;
   List<Model_Latest> item_list;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView=root.findViewById(R.id.latestView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        recyclerView.setAdapter(new LatestAdapter(initData()));

        return root;



    }

    private List<Model_Latest> initData() {

        item_list=new ArrayList<>();
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
        ,  6725,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  672537,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  67253,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  67253,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  6725,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                , 6725,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                , 672,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  67253,890));
        item_list.add(new Model_Latest(R.drawable.iceland,"Book","This is the best book.","politics"
                ,  672537,890));

        return item_list;

    }

}
