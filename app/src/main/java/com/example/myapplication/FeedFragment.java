package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class FeedFragment extends Fragment implements trendViewPagerAdapter.SelectedPager {
    private ImageView search;
    private RecyclerView feedRecycler;
    private trendViewPagerAdapter feedAdapter;
    private List<trendViewPagerModel> feedModels;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private String UserID;
    private List<String> following;
    private Query query;
    private DocumentSnapshot LastPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_feed,container,false);

        search=v.findViewById(R.id.imageViewSearch);
        feedRecycler=v.findViewById(R.id.feedRecycler);

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        UserID=mAuth.getCurrentUser().getUid();

        feedModels=new ArrayList<>();
        feedAdapter= new trendViewPagerAdapter(feedModels,this);
        feedRecycler.setAdapter(feedAdapter);
        feedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        feedRecycler.setHasFixedSize(true);

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

        feedRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition()==recyclerView.getChildCount()){
                    getFollowingPosts();
                }
            }
        });

        return v;
    }

    private void getFollowingPosts() {
        if(following.isEmpty()){
            Toast.makeText(getContext(),"You Don't follow anyone",Toast.LENGTH_LONG).show();
        }else {
            if (LastPost == null) {
                query = firestore.collection("Post")
                        .whereIn("Owner", following)
                        .orderBy("time")
                        .limit(10);
            } else {
                query = firestore.collection("Post")
                        .whereIn("Owner", following)
                        .orderBy("time")
                        .startAt(LastPost)
                        .limit(10);
            }
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.isEmpty()) {
                        Log.i("FeedPost", "onEvent: Empty");
                    } else {
                        for (QueryDocumentSnapshot doc : value) {
                            trendViewPagerModel set = doc.toObject(trendViewPagerModel.class);
                            feedModels.add(set);
                            feedAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }


    @Override
    public void selectedpager(trendViewPagerModel model) {
        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model.getID());
        startActivity(i);
    }
}
