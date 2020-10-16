package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements LatestAdapter.OnNoteListener {

   private RecyclerView recyclerView;
   private List<Model_Latest> item_list;
   private LatestAdapter latestAdapter;
   private FirebaseFirestore firestore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_home,container,false);

        recyclerView=root.findViewById(R.id.latestView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firestore=FirebaseFirestore.getInstance();

        item_list=new ArrayList<>();

        firestore.collection("Post").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList= queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot: snapshotList){
                    item_list.add(snapshot.toObject(Model_Latest.class));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("LatestPost", "onFailure: "+e.getMessage());
            }
        });


        recyclerView.setNestedScrollingEnabled(false);
        latestAdapter= new LatestAdapter(item_list,this);
        recyclerView.setAdapter(latestAdapter);

        return root;



    }

    public void onNoteClick(int position) {
        Intent intent = new Intent(getActivity(), ViewPost.class);
        Log.i("QuestionIntent", "onNoteClick:" + position);
        String ID=item_list.get(position).ID;
        intent.putExtra("PostId",ID);
        startActivity(intent);
    }

    private List<Model_Latest> initData() {



        return item_list;

    }

}



        /*item_list=new ArrayList<>();
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
                ,  672537,890));*/
