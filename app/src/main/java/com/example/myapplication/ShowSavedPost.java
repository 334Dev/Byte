package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

import java.util.List;

public class ShowSavedPost extends AppCompatActivity {

    private RecyclerView savedrecyclerView;
    private Model_Latest savedModel;
    private LatestAdapter savedAdapter;
    private AlertDialog.Builder builder;
    private AlertDialog show;
    private List<Model_Latest> SavedPostsItem;
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
        scrollView=findViewById(R.id.scrollSaved);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();

        builder=new AlertDialog.Builder(getApplicationContext());
        builder.setView(R.layout.loading_dailog);
        builder.setCancelable(true);
        show = builder.show();
        show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                View view = (View)scrollView.getChildAt(scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                        .getScrollY()));

                setSavedPost();
            }
        });

        setSavedPost();

    }

    private void setSavedPost() {

        query=fstore.collection("Post")
                .orderBy("time", Query.Direction.DESCENDING)
                .whereEqualTo("Owner",UserID)
                .limit(1000);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty())
                    Toast.makeText(getApplicationContext(),"You have no posts",Toast.LENGTH_SHORT).show();
                else {

                    for (QueryDocumentSnapshot doc : value) {

                        Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                        Model_Latest set = doc.toObject(Model_Latest.class);
                        SavedPostsItem.add(set);
                        savedAdapter.notifyDataSetChanged();
                        show.dismiss();

                    }
                    index=index+value.size()-1;
                    lastSavedPost = value.getDocuments().get(value.size() - 1);
                }

            }
        });

    }
}