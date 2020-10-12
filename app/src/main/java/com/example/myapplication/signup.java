package com.example.myapplication;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class signup extends AppCompatActivity {

    private static final String TAG ="Register" ;
    private TextView signinText,email,password;
    private ImageView logo;
    private Button register;
    private String Username, Email, Password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private View parentLayout;
    private Boolean USERNAME_ALREADY=false;

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

        signinText=findViewById(R.id.signinText);

        email=findViewById(R.id.emailin);
        password=findViewById(R.id.passwordin);

        logo=findViewById(R.id.logo);

        mAuth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        parentLayout = findViewById(android.R.id.content);

        register=findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        signinText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharedintent= new Intent(signup.this,signin.class);
                //Transition Animation
                ActivityOptions options= ActivityOptions.makeSceneTransitionAnimation(signup.this,
                        new android.util.Pair<View, String>(logo, "logoTransition"));
                startActivity(sharedintent, options.toBundle());
            }
        });
    }

    private void registerUser() {
        Email = email.getText().toString();
        Password = password.getText().toString();
        if (Email.isEmpty()) {
            email.setError("Email is empty");
        } else if (Password.isEmpty()) {
            password.setError("Password is empty");
        } else if (Password.length() < 6) {
            password.setError("Minimum 6 character");
        } else {
            mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String UID=user.getUid();
                        Map<String, String> map=new HashMap<>();
                        map.put("Username", Username);
                        firestore.collection("Users").document(UID).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(parentLayout, "Authentication Successful", Snackbar.LENGTH_SHORT).show();
                                Intent i=new Intent(signup.this, loginDetails.class);
                                startActivity(i);
                            }
                        });

                    } else {
                        Snackbar.make(parentLayout, "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean checkUsername(final String username) {
        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i(TAG, "onSuccess: checkUsername");
                if(queryDocumentSnapshots.isEmpty()){
                    USERNAME_ALREADY=false;
                }else {
                    List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot : snapshotList) {
                        if (snapshot.getString("Username").equals(username)) {
                            Log.i(TAG, "onSuccess: Equal");
                            USERNAME_ALREADY = true;
                            break;
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                USERNAME_ALREADY=true;
            }
        });
        return USERNAME_ALREADY;
    }

}