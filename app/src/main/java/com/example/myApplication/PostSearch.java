package com.example.myApplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostSearch extends AppCompatActivity implements com.example.myApplication.latestAdapter.SelectedItem {

    private TextView searchText;
    private RecyclerView searchRecycler;
    private FirebaseFirestore firestore;
    private List<modelLatest> searchModels;
    private com.example.myApplication.latestAdapter latestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_search);

        searchText=findViewById(R.id.postSearchText);
        searchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_search_24, 0, 0, 0);
        searchRecycler=findViewById(R.id.postSearchRecycler);

        firestore=FirebaseFirestore.getInstance();

        searchModels=new ArrayList<>();
        latestAdapter= new latestAdapter(searchModels,this);
        searchRecycler.setAdapter(latestAdapter);
        searchRecycler.hasFixedSize();
        searchRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchtxt=searchText.getText().toString();
                Log.i("search", "onTextChanged: "+searchtxt);
                searchInFirestore(searchtxt);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchInFirestore(String searchtxt) {
        Log.i("searchFirestore", "onTextChanged: "+searchtxt);
        //searching in firestore
        firestore.collection("Post").whereArrayContains("Keyword",searchtxt).limit(10).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                searchModels.clear();
                for (QueryDocumentSnapshot doc : value) {

                    Log.i("searchCheck", "onEvent:" + value.size());

                    modelLatest set = doc.toObject(modelLatest.class);
                    searchModels.add(set);
                    latestAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void selectedItem(modelLatest model_latest) {
        Intent i=new Intent(PostSearch.this,ViewPost.class);
        i.putExtra("PostId",model_latest.ID);
        startActivity(i);

    }
}