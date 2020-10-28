package com.example.myApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homefragment;
    private FeedFragment feedFragment;
    private QuickFragment quickFragment;
    private ProfileFragment profileFragment;
    private SpeedDialView addBtn;
    private BottomNavigationView bottomNav;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fstore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        UserID=mAuth.getCurrentUser().getUid();

        homefragment=new HomeFragment();
        feedFragment=new FeedFragment();
        quickFragment =new QuickFragment();
        profileFragment=new ProfileFragment();
        addBtn=findViewById(R.id.AddPostButton);

        addBtn.inflate(R.menu.floatingbtn_menu);
        addBtn.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                if(actionItem.getId()==R.id.postFAB){
                    checkDraftexist();
                }
                if(actionItem.getId()==R.id.quickFAB){
                    Intent intent=new Intent(HomeActivity.this, CreateQuick.class);
                    startActivity(intent);
                }
                return false;
            }
        });


        bottomNav= findViewById(R.id.nav_bottom_id);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,homefragment).commit();

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home_id:
                        replaceFragment(homefragment);
                        break;
                    case R.id.nav_profile_id:
                        replaceFragment(profileFragment);
                        break;
                    case R.id.nav_feed_id:
                        replaceFragment(feedFragment);
                        break;
                    case R.id.nav_saved_id:
                        replaceFragment(quickFragment);
                        break;
                    default:
                        return false;
                }

                return true;
            }
        });

    }

    public void checkDraftexist() {
        fstore.collection("Users").document(UserID).collection("draft").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.i("Draft", "onSuccess: No Draft");
                    Intent intent=new Intent(HomeActivity.this,SetPostTitle.class);
                    startActivity(intent);
                }else{
                    Log.i("Draft", "onSuccess: Draft exist");
                    String Filename=queryDocumentSnapshots.getDocuments().get(0).getString("FileName");
                    String TitleImage=queryDocumentSnapshots.getDocuments().get(0).getString("TitleImage");
                    String Tag=queryDocumentSnapshots.getDocuments().get(0).getString("Tag");
                    String Title=queryDocumentSnapshots.getDocuments().get(0).getString("Title");
                    String Desc=queryDocumentSnapshots.getDocuments().get(0).getString("Desc");

                    Intent intent=new Intent(HomeActivity.this,CreatePost.class);

                    intent.putExtra("FileName",Filename);
                    intent.putExtra("DraftExist",true);
                    intent.putExtra("TitleImage",TitleImage);
                    intent.putExtra("Tag",Tag);
                    intent.putExtra("Title",Title);
                    intent.putExtra("Desc",Desc);
                    startActivity(intent);
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }


}