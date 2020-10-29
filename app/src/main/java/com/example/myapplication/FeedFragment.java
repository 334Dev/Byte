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

public class FeedFragment extends Fragment implements latestAdapter.SelectedItem {
    private ImageView search;
    private RecyclerView feedRecycler;
    private latestAdapter feedAdapter;
    private List<modelLatest> feedModels;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String UserID;
    private List<String> following;
    private DocumentSnapshot LastPost;
    private Integer LAST_VISIBLE=0;
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
        feedAdapter= new latestAdapter(feedModels,this);
        feedRecycler.setAdapter(feedAdapter);
        feedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecycler.setHasFixedSize(true);
        feedRecycler.setNestedScrollingEnabled(false);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), UserSearch.class);
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

        return v;
    }

    private void getFollowingPosts() {
        LAST_VISIBLE=1;
        if(following.isEmpty()){
            Toast.makeText(getContext(),"You Don't follow anyone",Toast.LENGTH_LONG).show();
        }else {
               Query query = firestore.collection("Post")
                        .whereIn("Owner", following)
                        .orderBy("time", Query.Direction.DESCENDING)
                        .limit(10);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(queryDocumentSnapshots.isEmpty()){
                        Log.i("LatestPost", "onSuccess: Empty");
                    }else{
                        //item_list.clear();
                        List<DocumentSnapshot> snapshotList=queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot snapshot:snapshotList){
                            feedModels.add(snapshot.toObject(modelLatest.class));
                        }
                        feedAdapter.notifyDataSetChanged();
                        LastPost = snapshotList.get(snapshotList.size() -1);
                        if(snapshotList.size()<10){
                            LAST_VISIBLE=0;
                        }

                        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged()
                            {
                                View view = (View)scrollView.getChildAt(scrollView.getChildCount() - 1);

                                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                                        .getScrollY()));

                                if (diff == 0 && LAST_VISIBLE==1 ) {
                                    loadFollowingPost();
                                }
                            }
                        });
                    }
                }
            });

        }
    }

    public void loadFollowingPost(){
        LAST_VISIBLE=1;
        Query nextquery = firestore.collection("Post")
                .whereIn("Owner", following)
                .orderBy("time", Query.Direction.DESCENDING)
                .startAfter(LastPost)
                .limit(10);
        nextquery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.i("LatestPost", "onSuccess: Empty");
                }else if(feedModels.size()%10==0) {
                    List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot doc: snapshots){
                        feedModels.add(doc.toObject(modelLatest.class));
                    }
                    feedAdapter.notifyDataSetChanged();
                    LastPost=snapshots.get(snapshots.size()-1);
                    if(snapshots.size()<10){
                        Log.i("LatestPost", "onScrollChanged: limit reached");
                        LAST_VISIBLE=0;
                    }
                    scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged()
                        {
                            View view = (View)scrollView.getChildAt(scrollView.getChildCount() - 1);

                            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView
                                    .getScrollY()));

                            if (diff == 0 && LAST_VISIBLE==1 ) {
                                Log.i("LatestPost", "onScrollChanged: More");
                                loadFollowingPost();
                            }
                        }
                    });
                }
            }
        });
    }


    @Override
    public void selectedItem(modelLatest model_latest) {
        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model_latest.getID());
        startActivity(i);
    }
}
