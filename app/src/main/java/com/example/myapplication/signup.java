package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private TextView signinText,username,email,password;
    private ImageView mountain;
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
        getSupportActionBar().hide();
        mountain=findViewById(R.id.mountain);
        signinText=findViewById(R.id.signinText);

        username=findViewById(R.id.username);
        email=findViewById(R.id.emailUp);
        password=findViewById(R.id.passwordUp);

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
                        new android.util.Pair<View, String>(mountain, "MountainTransition"));
                startActivity(sharedintent, options.toBundle());
            }
        });
    }

    private void registerUser() {
        Username = username.getText().toString();
        Email = email.getText().toString();
        Password = password.getText().toString();
        if (Email.isEmpty()) {
            email.setError("Email is empty");
        } else if (Password.isEmpty()) {
            password.setError("Password is empty");
        } else if (Password.length() < 6) {
            password.setError("Minimum 6 character");
        } else if (Username.isEmpty()) {
            username.setError("Username is empty");
        } else if (!checkUsername(Username)) {
            Log.i(TAG, "registerUser: username exist");
            username.setError("Username already exist");
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