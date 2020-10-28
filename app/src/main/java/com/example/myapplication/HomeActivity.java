package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homefragment;
    private FeedFragment feedFragment;
    private QuickFragment quickFragment;
    private ProfileFragment profileFragment;
    private SpeedDialView addBtn;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
                    Intent intent=new Intent(HomeActivity.this,SetPostTitle.class);
                    startActivity(intent);
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