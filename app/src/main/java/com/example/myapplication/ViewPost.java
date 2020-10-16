package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewPost extends AppCompatActivity {

    private WebView web;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private String UserID;
    private TextView postTitle,viewCount;
    private ImageView postCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        viewCount=findViewById(R.id.viewCount);
        postCover=findViewById(R.id.postCover);
        postTitle=findViewById(R.id.postTitle);

        web=findViewById(R.id.webView);
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);


        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        Intent intent=getIntent();
        String id=intent.getStringExtra("PostId");
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                @SuppressLint("ResourceType") String color="#"+getResources().getString(R.color.plainText).substring(3);
                //String color="#ff333333";
                Log.i("color", "onSuccess: "+color);
                String text="<font color="+color+">"+documentSnapshot.getString("Post")+"</font>";

                text="<style>a:link{color:"+color+";}</style>"+text;
                //String text=documentSnapshot.getString("Post");
                web.loadDataWithBaseURL("",text,"text/html","utf-8",null);
                String title=documentSnapshot.getString("title");
                String img=documentSnapshot.getString("img");
                Double view_Count=documentSnapshot.getDouble("viewCount");

                postTitle.setText(title);
                Picasso.get().load(img).into(postCover);
                viewCount.setText(String.format("%.0f", view_Count));



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ViewPost.this, HomeActivity.class);
        startActivity(i);
    }
}