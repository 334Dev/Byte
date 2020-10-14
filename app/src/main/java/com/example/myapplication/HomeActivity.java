package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homefragment;
    private FeedFragment feedFragment;
    private SavedFragment savedFragment;
    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homefragment=new HomeFragment();
        feedFragment=new FeedFragment();
        savedFragment=new SavedFragment();
        profileFragment=new ProfileFragment();

        BottomNavigationView bottomNav= findViewById(R.id.nav_bottom_id);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment =null;

                    switch (item.getItemId())
                    {
                        case R.id.nav_home_id:
                            selectedFragment=homefragment;
                            break;
                        case R.id.nav_profile_id:
                            selectedFragment=profileFragment;
                            break;
                        case R.id.nav_feed_id:
                            selectedFragment=feedFragment;
                            break;
                        case R.id.nav_saved_id:
                            selectedFragment=savedFragment;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                            ,selectedFragment).commit();
                    return true;
                }
            };
}