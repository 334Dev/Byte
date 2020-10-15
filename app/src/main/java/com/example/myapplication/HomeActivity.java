package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeActivity extends AppCompatActivity {

    private HomeFragment homefragment;
    private FeedFragment feedFragment;
    private SavedFragment savedFragment;
    private ProfileFragment profileFragment;
    private FloatingActionButton addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homefragment=new HomeFragment();
        feedFragment=new FeedFragment();
        savedFragment=new SavedFragment();
        profileFragment=new ProfileFragment();
        addBtn=findViewById(R.id.AddPostButton);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeActivity.this,CreatePost.class);
                startActivity(intent);
            }
        });

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