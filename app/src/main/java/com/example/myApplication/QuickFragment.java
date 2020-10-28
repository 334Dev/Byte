package com.example.myApplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuickFragment extends Fragment implements quickAdapter.SelectedPager {
    private ViewPager2 viewPager;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private quickAdapter quickAdapter;
    private List<quickModel> quickModels;
    private Date date;
    private DocumentSnapshot lastQuick;
    private Query query;
    private Integer index;
    private ProgressBar loading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_quick,container,false);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        viewPager=v.findViewById(R.id.quickPager);
        loading=v.findViewById(R.id.quickLoading);

        loading.setVisibility(View.VISIBLE);
        
        quickModels=new ArrayList<>();

        quickAdapter=new quickAdapter(quickModels,this);
        viewPager.setAdapter(quickAdapter);
        viewPager.setPadding(0,0,0,0);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(4);

        date=new Date();
        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
        String sDate=format.format(date);

        try {
            date=format.parse(sDate);
            Log.i("millieTime", "onClick: "+date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        setViewPager();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if(position%5==0){
                    loadViewPager();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        return v;

    }

    private void setViewPager() {
            query=firestore.collection("Quick")
                    .whereGreaterThan("time", date.getTime())
                    .limit(5);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.isEmpty()){
                        Log.i("PostEmpty", "onEvent: Empty");
                    }else {
                        for (QueryDocumentSnapshot doc : value) {

                            Log.i("PostL", "onEvent:" + doc.getId());
                            quickModel set = doc.toObject(quickModel.class);
                            quickModels.add(set);
                            quickAdapter.notifyDataSetChanged();
                            

                        }
                       // index=index+value.size()-1;
                        //Log.i("PostIndex", "onEvent: "+index);
                        loading.setVisibility(View.INVISIBLE);

                        lastQuick = value.getDocuments().get(value.size() - 1);
                        Log.i("PostLast", "onEvent: "+quickModels.size());
                        Log.i("PostLast", "onEvent: "+lastQuick.getId());
                    }
                }
            });

        }

        public void loadViewPager(){
            query=firestore.collection("Quick")
                    .whereGreaterThan("time", date.getTime())
                    .startAfter(lastQuick)
                    .limit(5);
            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.isEmpty()){
                        Log.i("PostEmpty", "onEvent: Empty");
                    }else {
                        List<quickModel> inputList;
                        inputList=new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {

                            Log.i("PostL", "onEvent:" + doc.getId());
                            quickModel set = doc.toObject(quickModel.class);
                            inputList.add(set);

                        }
                        quickModels.addAll(index,inputList);
                        quickAdapter.notifyItemRangeChanged(index,value.size());

                        index=index+value.size();
                        lastQuick = value.getDocuments().get(value.size() - 1);

                        Log.i("PostIndex", "onEvent: "+index);
                        Log.i("PostLast", "onEvent: "+quickModels.size());
                        Log.i("PostLast", "onEvent: "+lastQuick.getId());
                    }
                }
            });
        }



    @Override
    public void selectedpager(quickModel viewPagerModel) {
        //future
    }
}
