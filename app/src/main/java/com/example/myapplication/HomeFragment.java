package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements LatestAdapter.SelectedItem, trendViewPagerAdapter.SelectedPager {

   private RecyclerView recyclerView, upVoteRecycler;
   private List<Model_Latest> item_list, upVote_list;
   private LatestAdapter latestAdapter, upVoteAdapter;
   private FirebaseFirestore firestore;
   private FirebaseAuth mAuth;
    private ImageView postSearchBtn;
   private List<String> Tag;
   private ViewPager2 trendViewPager;
   private List<trendViewPagerModel> pagerModels;
   private trendViewPagerAdapter pagerAdapter;
   private DocumentSnapshot lastLatestPost;
   private Query query;
   private NestedScrollView scrollHome;
   private Integer index=0;

   //loading dialog box
   private AlertDialog.Builder builder;
   private AlertDialog show;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_home,container,false);

        trendViewPager=root.findViewById(R.id.trendViewPager);

        scrollHome=root.findViewById(R.id.scrollHome);

        upVoteRecycler=root.findViewById(R.id.upvoteView);
        upVoteRecycler.setHasFixedSize(true);
        upVoteRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        upVote_list=new ArrayList<>();
        upVoteAdapter=new LatestAdapter(upVote_list,this);
        upVoteRecycler.setAdapter(upVoteAdapter);
        upVoteRecycler.setNestedScrollingEnabled(false);

        //Loading Dialog Box
        builder=new AlertDialog.Builder(getContext());
        builder.setView(R.layout.loading_dailog);
        builder.setCancelable(true);
        show = builder.show();
        show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));



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
        latestAdapter.setHasStableIds(true);
        recyclerView.setAdapter(latestAdapter);

        recyclerView.setHasFixedSize(true);

        Tag=new ArrayList<>();

        firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Tag= (List<String>) documentSnapshot.get("Tag");
                setLatestPost();
                setupViewPager();
                setUpVotePost();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("getTagFailed", "onFailure: "+e.getMessage());
            }
        });

        recyclerView.setNestedScrollingEnabled(false);

        scrollHome.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged()
            {
                View view = (View)scrollHome.getChildAt(scrollHome.getChildCount() - 1);

                int diff = (view.getBottom() - (scrollHome.getHeight() + scrollHome
                        .getScrollY()));

                if (diff == 0) {
                    loadLatestPost();
                }
            }
        });

        return root;

    }

    private void setUpVotePost() {
        firestore.collection("Post")
                .orderBy("UpVote", Query.Direction.DESCENDING)
                .whereIn("tag",Tag)
                .limit(2).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (QueryDocumentSnapshot doc : value) {

                    Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                    if(Tag.contains(doc.getString("tag"))) {
                        Model_Latest set = doc.toObject(Model_Latest.class);
                        upVote_list.add(set);
                        upVoteAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
    }

    private void setupViewPager() {
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.set(date.getYear(),date.getMonth(),date.getDay(),00,00,0);
        Log.i("Date", "setupViewPager: "+date);

        Query query=firestore.collection("Post")
                .orderBy("trend", Query.Direction.DESCENDING)
                .whereIn("tag",Tag);

            query.limit(6).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    for (QueryDocumentSnapshot doc : value) {

                        Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                        //if(Tag.contains(doc.getString("tag"))) {
                            trendViewPagerModel set = doc.toObject(trendViewPagerModel.class);
                            pagerModels.add(set);
                            pagerAdapter.notifyDataSetChanged();
                        //}
                    }
                }
            });
            Log.i("ViewPager", "setupViewPager: "+pagerModels.size());
        //View pager basic settings
        pagerAdapter=new trendViewPagerAdapter(pagerModels,this);
        trendViewPager.setAdapter(pagerAdapter);
        trendViewPager.setPadding(0,0,0,0);
        trendViewPager.setClipToPadding(false);
        trendViewPager.setClipChildren(false);
        trendViewPager.setOffscreenPageLimit(4);

    }

    private void setLatestPost() {
            query=firestore.collection("Post")
                    .orderBy("time", Query.Direction.DESCENDING)
                    .whereIn("tag",Tag)
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
                            item_list.add(set);
                            latestAdapter.notifyDataSetChanged();
                            show.dismiss();

                        }
                        index=index+value.size()-1;
                        Log.i("PostIndex", "onEvent: "+index);
                        lastLatestPost = value.getDocuments().get(value.size() - 1);
                        Log.i("PostLast", "onEvent: "+item_list.size());
                        Log.i("PostLast", "onEvent: "+lastLatestPost.getId());
                    }
                }
            });


    }

    private void loadLatestPost(){
        query=firestore.collection("Post")
                .orderBy("time", Query.Direction.DESCENDING)
                .whereIn("tag",Tag)
                .startAfter(lastLatestPost)
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
                        show.dismiss();

                    }
                    item_list.addAll(index,inputList);
                    latestAdapter.notifyItemRangeChanged(index,value.size());

                    index=index+value.size();
                    lastLatestPost = value.getDocuments().get(value.size() - 1);

                    Log.i("PostIndex", "onEvent: "+index);
                    Log.i("PostLast", "onEvent: "+item_list.size());
                    Log.i("PostLast", "onEvent: "+lastLatestPost.getId());
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
