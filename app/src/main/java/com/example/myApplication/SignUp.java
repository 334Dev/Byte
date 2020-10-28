 package com.example.myApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

 public class SignUp extends AppCompatActivity {

    private static final String TAG ="Register" ;
    private TextView signinText,email,password;
    private ImageView logo;
    private Button register;
    private String Email, Password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private View parentLayout;
    private Boolean USERNAME_ALREADY=false;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //transition Time period
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(1000);
            getWindow().getSharedElementReturnTransition().setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator());
        }


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        loading=findViewById(R.id.signupLoading);
        loading.setVisibility(View.INVISIBLE);
        signinText=findViewById(R.id.signinText);
        email=findViewById(R.id.emailin);
        password=findViewById(R.id.passwordin);
        register=findViewById(R.id.register);

        logo=findViewById(R.id.logo);

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        parentLayout = findViewById(android.R.id.content);

        //registering new user
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        //sign-in intent for already registered user
        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedintent= new Intent(SignUp.this, SignIn.class);
                //Transition Animation
                ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(SignUp.this,
                        new android.util.Pair<View, String>(logo, "logoTransition"));
                startActivity(sharedintent, options.toBundle());
            }
        });
    }

    private void registerUser() {
        Email = email.getText().toString();
        Password = password.getText().toString();
        //Field Checks
        if (Email.isEmpty()) {
            email.setError("Email is empty");
        } else if (Password.isEmpty()) {
            password.setError("Password is empty");
        } else if (Password.length() < 6) {
            password.setError("Minimum 6 character");
        } else {
            loading.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        Snackbar.make(parentLayout, "Authentication Successful", Snackbar.LENGTH_SHORT).show();
                        Intent i=new Intent(SignUp.this, LoginDetails.class);
                        startActivity(i);
                        loading.setVisibility(View.INVISIBLE);

                    } else {
                        Snackbar.make(parentLayout, "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

}