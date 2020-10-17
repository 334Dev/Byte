package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class AnotherUserProfile extends AppCompatActivity {

    private TextView posts;
    private TextView followers;
    private TextView following;
    private CircleImageView AnotherUserProfileImageView;
    private ImageView CoverPhoto;
    private Button FollowBtn;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private String UserID;
    private String AnotherUserName;
    private String AnotherUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_user_profile);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        FollowBtn=findViewById(R.id.FollowBtn);


        followers=findViewById(R.id.followers);
        following=findViewById(R.id.following);
        posts=findViewById(R.id.post);

        Intent intent=getIntent();
        AnotherUserName=intent.getStringExtra("SearchUserName");
        fstore.collection("Users").whereEqualTo("Username",AnotherUserName).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                List<DocumentSnapshot> snapshotList= queryDocumentSnapshots.getDocuments();
                AnotherUserId=snapshotList.get(0).getId();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG);
            }
        });

        StorageReference profileRef = storageReference.child("users/" +AnotherUserId+ "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //setting profile picture
                Picasso.get().load(uri).into(AnotherUserProfileImageView);
                //setting cover image
                Picasso.get().load(uri).transform(new BlurTransformation(AnotherUserProfile.this,25,3)).into(CoverPhoto);
            }
        });


        fstore.collection("Users").document(AnotherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                // getting value of number of followers,following and posts from database and showing in this activity..

                String f1=documentSnapshot.getString("Followers");
                String f2=documentSnapshot.getString("Following");
                String p=documentSnapshot.getString("Post");
                followers.setText(f1);
                following.setText(f2);
                posts.setText(p);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



         FollowBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 addProfileFollowing();


             }
         });


    }

    private void addProfileFollowing() {
        Map<String,Object> map=new HashMap();
        map.put("ProfileId",AnotherUserId);

        Map<String,Object> map1=new HashMap();
        map1.put("ProfileId",UserID);

        fstore.collection("Users").document(UserID).collection("Following").add(map);
        fstore.collection("Users").document(AnotherUserId).collection("Followers").add(map1);
        fstore.collection("Users").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String f2=documentSnapshot.getString("Following");
                int fllwing=Integer.parseInt(f2);
                fllwing=fllwing+1;
                String UpdatedFollowing=Integer.toString(fllwing);
                fstore.collection("Users").document(UserID).update("Following",fllwing);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        fstore.collection("Users").document(AnotherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String f3=documentSnapshot.getString("Followers");
                int fllowers=Integer.parseInt(f3);
                 fllowers=fllowers+1;
                String UpdatedFollowers=Integer.toString(fllowers);
                fstore.collection("Users").document(UserID).update("Followers",fllowers);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        }
}