package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
    ImageView logo;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo=findViewById(R.id.splashLogo);
        mAuth=FirebaseAuth.getInstance();
        Handler mHandler= new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mAuth.getCurrentUser()==null){
                    Intent sharedintent= new Intent(SplashScreen.this,LoginHome.class);
                    ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                            new android.util.Pair<View, String>(logo, "logoTransition"));
                    startActivity(sharedintent, options.toBundle());
                    finish();
                }else{
                    Intent i=new Intent(SplashScreen.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        },1000);

        
        
    }
}