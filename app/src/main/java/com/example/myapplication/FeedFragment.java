package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FeedFragment extends Fragment implements LatestAdapter.SelectedItem {
    private ImageView search;
    private RecyclerView feedRecycler;
    private LatestAdapter feedAdapter;
    private List<Model_Latest> feedModels;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String UserID;
    private List<String> following;
    private Query query;
    private DocumentSnapshot LastPost;
    private Integer index=0;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_feed,container,false);

        search=v.findViewById(R.id.imageViewSearch);
        feedRecycler=v.findViewById(R.id.feedRecycler);

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        scrollView=v.findViewById(R.id.scrollFeed);

        UserID=mAuth.getCurrentUser().getUid();

        feedModels=new ArrayList<>();
        feedAdapter= new LatestAdapter(feedModels,this);
        feedRecycler.setAdapter(feedAdapter);
        feedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecycler.setHasFixedSize(true);
        feedRecycler.setNestedScrollingEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), userSearch.class);
                startActivity(intent);
            }
        });

        firestore.collection("Users").document(UserID)
                .collection("Following").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                following=new ArrayList<>();
                List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot snapshot: snapshotList){
                    following.add(snapshot.getString("ProfileId"));
                }
                getFollowingPosts();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                View view = (View)scrollView.getChildAt(scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                        .getScrollY()));

                if (diff == 0) {
                    loadFollowingPost();
                }
            }
        });

        return v;
    }

    private void getFollowingPosts() {
        if(following.isEmpty()){
            Toast.makeText(getContext(),"You Don't follow anyone",Toast.LENGTH_LONG).show();
        }else {
                query = firestore.collection("Post")
                        .whereIn("Owner", following)
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(10);
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.isEmpty()){
                            Log.i("PostEmpty", "onEvent: Empty");
                        }else {
                            for (QueryDocumentSnapshot doc : value) {

                                Log.i("PostL", "onEvent:" + doc.getId());
                                Model_Latest set = doc.toObject(Model_Latest.class);
                                feedModels.add(set);
                                feedAdapter.notifyDataSetChanged();

                            }
                            index=index+value.size()-1;
                            Log.i("PostIndex", "onEvent: "+index);
                            LastPost = value.getDocuments().get(value.size() - 1);
                            Log.i("PostLast", "onEvent: "+feedModels.size());
                            Log.i("PostLast", "onEvent: "+LastPost.getId());
                        }
                    }
                });

        }
    }

    public void loadFollowingPost(){
        query = firestore.collection("Post")
                .whereIn("Owner", following)
                .orderBy("time", Query.Direction.DESCENDING)
                .startAt(LastPost)
                .limit(10);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    Log.i("PostEmpty", "onEvent: Empty");
                }else {
                    List<Model_Latest> inputList;
                    inputList=new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {

                        Log.i("PostL", "onEvent:" + doc.getId());
                        Model_Latest set = doc.toObject(Model_Latest.class);
                        inputList.add(set);

                    }
                    feedModels.addAll(index,inputList);
                    feedAdapter.notifyItemRangeChanged(index,value.size());

                    index=index+value.size();
                    LastPost = value.getDocuments().get(value.size() - 1);

                    Log.i("PostIndex", "onEvent: "+index);
                    Log.i("PostLast", "onEvent: "+feedModels.size());
                    Log.i("PostLast", "onEvent: "+LastPost.getId());
                }
            }
        });
    }


    @Override
    public void selectedItem(Model_Latest model_latest) {
        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model_latest.getID());
        startActivity(i);
    }
}
