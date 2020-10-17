package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signin extends AppCompatActivity {

    private static final String TAG ="SignIn" ;
    private TextView email,password,signupTxt;
    private Button Login;
    private ImageView logo;
    private FirebaseAuth mAuth;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        //transition Time period
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(1000);
            getWindow().getSharedElementReturnTransition().setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator());
        }
        mAuth=FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);

        Login=findViewById(R.id.register);
        email=findViewById(R.id.emailin);
        password=findViewById(R.id.passwordin);
        signupTxt=findViewById(R.id.signupText);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty()){
                    email.setError("Email field is empty");           //if Email field is Empty
                }else if(password.getText().toString().isEmpty()){
                    password.setError("Password field is empty");      //if Password field is Empty
                }else {
                    mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        //and go to HomeActivity
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Snackbar.make(parentLayout, "Login Successful", Snackbar.LENGTH_SHORT).show();
                                        Intent i=new Intent(signin.this, HomeActivity.class);
                                        startActivity(i);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Snackbar.make(parentLayout, "Login Failed", Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //SharedElement Transition
        logo=findViewById(R.id.logo);
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedintent= new Intent(signin.this,signup.class);
                //Transition Animation
                ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(signin.this,
                        new android.util.Pair<View, String>(logo, "logoTransition"));
                startActivity(sharedintent, options.toBundle());
            }
        });
    }
}