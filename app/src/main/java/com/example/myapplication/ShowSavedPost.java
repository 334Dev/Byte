package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ScrollView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ShowSavedPost extends AppCompatActivity {

    private RecyclerView savedrecyclerView;
    private Model_Latest savedModel;
    private LatestAdapter savedAdapter;
    private AlertDialog.Builder builder;
    private AlertDialog show;
    private List<Model_Latest> SavedPostsItem;

    private DocumentSnapshot lastSavedPost;
    private Query query;
    private ScrollView scrollView;
    private Integer index=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_saved_post);

        savedrecyclerView=findViewById(R.id.savedPostsRecycler);
        scrollView=findViewById(R.id.scrollSaved);

    }
}