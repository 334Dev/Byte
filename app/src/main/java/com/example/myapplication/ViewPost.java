package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewPost extends AppCompatActivity {

     WebView web;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        web=findViewById(R.id.webView);
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);


        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        fstore.collection("Post").document("hello").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                @SuppressLint("ResourceType") String color="#"+getResources().getString(R.color.plainText).substring(3);
                //String color="#ff333333";
                Log.i("color", "onSuccess: "+color);
                String text="<font color="+color+">"+documentSnapshot.getString("Post")+"</font>";

                  text="<style>a:link{color:"+color+";}</style>"+text;
                //String text=documentSnapshot.getString("Post");
                web.loadDataWithBaseURL("",text,"text/html","utf-8",null);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



    }
}