package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class signin extends AppCompatActivity {

    TextView signupText;
    ImageView mountain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();
        //transition Time period
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(1000);
            getWindow().getSharedElementReturnTransition().setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator());
        }
        mountain=findViewById(R.id.mountain);
        signupText=findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedintent= new Intent(signin.this,signup.class);
                //Transition Animation
                ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(signin.this,
                        new android.util.Pair<View, String>(mountain, "MountainTransition"));
                startActivity(sharedintent, options.toBundle());
            }
        });
    }
}