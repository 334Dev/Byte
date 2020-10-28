package com.example.myApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowSavedPost extends AppCompatActivity implements latestAdapter.SelectedItem {

    private RecyclerView savedrecyclerView;
    private modelLatest savedModel;
    private latestAdapter savedAdapter;
    private AlertDialog.Builder builder;
    private AlertDialog show;
    private List<modelLatest> SavedPostsItem;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UserID;
    private FirebaseFirestore fstore;
    private DocumentSnapshot lastSavedPost;
    private Query query;
    private ScrollView scrollView;
    private Integer index=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_post);

        savedrecyclerView=findViewById(R.id.savedPostsRecycler);
        savedrecyclerView.setHasFixedSize(true);
        savedrecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        SavedPostsItem=new ArrayList<>();
        savedAdapter=new latestAdapter(SavedPostsItem,this);
        savedAdapter.setHasStableIds(true);
        savedrecyclerView.setAdapter(savedAdapter);
        savedrecyclerView.setHasFixedSize(true);
        scrollView=findViewById(R.id.scrollSaved);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();

        /*
        builder=new AlertDialog.Builder(getApplicationContext());
        builder.setView(R.layout.loading_dailog);
        builder.setCancelable(true);
        //show = builder.show();

        show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        */



        setSavedPost();

    }

    private void setSavedPost() {

        query=fstore.collection("Post")
                .whereArrayContains("SavedId",UserID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty())
                    Toast.makeText(getApplicationContext(),"You have no posts",Toast.LENGTH_SHORT).show();
                else {

                    for (QueryDocumentSnapshot doc : value) {

                        Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                        modelLatest set = doc.toObject(modelLatest.class);
                        SavedPostsItem.add(set);
                        savedAdapter.notifyDataSetChanged();
                        //show.dismiss();

                    }
                    index=index+value.size()-1;
                    lastSavedPost = value.getDocuments().get(value.size() - 1);
                }

            }
        });

    }

    @Override
    public void selectedItem(modelLatest model_latest) {
        Intent i=new Intent(getApplicationContext(),ViewPost.class);
        i.putExtra("PostId",model_latest.ID);
        startActivity(i);
    }
}