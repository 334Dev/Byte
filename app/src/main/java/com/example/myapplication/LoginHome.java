package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginHome extends AppCompatActivity {
    private Button google, email,register;
    private TextView password, emailT, signup;
    private ImageView logo;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private View parentLayout;
    private int RC_SIGN_IN=101;
    private String UserID;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginhome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(1000);
            getWindow().getSharedElementReturnTransition().setDuration(1000)
                    .setInterpolator(new DecelerateInterpolator());
        }

        register=findViewById(R.id.login2);
        password=findViewById(R.id.passwordUp2);
        emailT=findViewById(R.id.emailUp2);
        signup=findViewById(R.id.signinText2);

        google=findViewById(R.id.google);
        email=findViewById(R.id.email);
        logo=findViewById(R.id.logo);

        firestore=FirebaseFirestore.getInstance();

        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        email.setAnimation(animation);
        google.setAnimation(animation);

        mAuth= FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation1=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
                email.setAnimation(animation1);
                google.setAnimation(animation1);
                transitionAnimation();

            }
        });

    }

    private void transitionAnimation() {
        Intent sharedintent= new Intent(LoginHome.this,signin.class);
        ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(LoginHome.this,
                new android.util.Pair<View, String>(logo, "logoTransition"),
                //new android.util.Pair<View, String>(google, "google"),
                //new android.util.Pair<View, String>(email, "email"),
                new android.util.Pair<View, String>(password, "passwordT"),
                new android.util.Pair<View, String>(emailT, "emailT"),
                new android.util.Pair<View, String>(signup, "signinT"),
                new android.util.Pair<View, String>(register, "loginT"));
        startActivity(sharedintent, options.toBundle());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Snackbar.make(parentLayout, "Google Sign in Failed", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserID=mAuth.getCurrentUser().getUid();
                            firestore.collection("Users").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.getResult().exists()){
                                        // Sign in success, update UI with the signed-in user's information
                                        Snackbar.make(parentLayout, "Authentication Successful.", Snackbar.LENGTH_SHORT).show();
                                        Intent i=new Intent(LoginHome.this, HomeActivity.class);
                                        startActivity(i);
                                    }else{
                                        // Sign in success, update UI with the signed-in user's information
                                        Snackbar.make(parentLayout, "Authentication Successful.", Snackbar.LENGTH_SHORT).show();
                                        Intent i=new Intent(LoginHome.this, loginDetails.class);
                                        startActivity(i);
                                    }
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(parentLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

}