package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
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

public class HomeFragment extends Fragment implements LatestAdapter.SelectedItem, trendViewPagerAdapter.SelectedPager {

   private RecyclerView recyclerView;
   private List<Model_Latest> item_list;
   private LatestAdapter latestAdapter;
   private FirebaseFirestore firestore;
   private ImageView postSearchBtn;
   private FirebaseAuth mAuth;
   private List<String> Tag;
   private ViewPager2 trendViewPager;
   private List<trendViewPagerModel> pagerModels;
   private trendViewPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_home,container,false);

        trendViewPager=root.findViewById(R.id.trendViewPager);
        //View pager basic settings



        pagerModels=new ArrayList<>();


        recyclerView=root.findViewById(R.id.latestView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postSearchBtn=root.findViewById(R.id.postSearchbtn);
        postSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),postSearch.class);
                startActivity(i);
            }
        });

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        item_list=new ArrayList<>();
        latestAdapter= new LatestAdapter(item_list,this);
        recyclerView.setAdapter(latestAdapter);

        recyclerView.setHasFixedSize(true);

        Tag=new ArrayList<>();

        firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Tag= (List<String>) documentSnapshot.get("Tag");
                setLatestPost();
                setupViewPager();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("getTagFailed", "onFailure: "+e.getMessage());
            }
        });

        recyclerView.setNestedScrollingEnabled(false);




        return root;



    }

    private void setupViewPager() {
            firestore.collection("Post")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .orderBy("UpVote", Query.Direction.DESCENDING)
                    .limit(4).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot doc : value) {

                        Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                        if(Tag.contains(doc.getString("tag"))) {
                            trendViewPagerModel set = doc.toObject(trendViewPagerModel.class);
                            pagerModels.add(set);
                            pagerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            Log.i("ViewPager", "setupViewPager: "+pagerModels.size());
        pagerAdapter=new trendViewPagerAdapter(pagerModels,this);
        trendViewPager.setAdapter(pagerAdapter);
        trendViewPager.setPadding(0,0,0,0);
        trendViewPager.setClipToPadding(false);
        trendViewPager.setClipChildren(false);
        trendViewPager.setOffscreenPageLimit(4);

    }

    private void setLatestPost() {

        firestore.collection("Post")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(30).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {

                    Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                    if(Tag.contains(doc.getString("tag"))) {
                        Model_Latest set = doc.toObject(Model_Latest.class);
                        item_list.add(set);
                        latestAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    @Override
    public void selectedItem(Model_Latest model_latest) {
        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model_latest.ID);
        startActivity(i);
    }

    public void selectedpager(trendViewPagerModel model) {
        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model.getID());
        startActivity(i);
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
