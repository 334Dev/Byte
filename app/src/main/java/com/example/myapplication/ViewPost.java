package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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

public class ViewPost extends AppCompatActivity {

     WebView web;
    private FirebaseFirestore fstore;
    private TextView titleHeader;
    private ImageView HeaderImage;
    private ImageView saveButton;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        saveButton=findViewById(R.id.imageViewSaved);
        web=findViewById(R.id.webView);
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);


        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        HeaderImage=findViewById(R.id.headerimg);
        titleHeader=findViewById(R.id.postTitle);

        //passing the text which contain html code to web view
        Intent intent=getIntent();
        String id=intent.getStringExtra("PostId"); // getting PostId from Intent

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                @SuppressLint("ResourceType") String color="#"+getResources().getString(R.color.plainText).substring(3);
                Log.i("color", "onSuccess: "+color);
                String text="<font color="+color+">"+documentSnapshot.getString("Post")+"</font>";

                  text="<style>a:link{color:"+color+";}</style>"+text;
                web.loadDataWithBaseURL("",text,"text/html","utf-8",null);



                // "text" will be containing the HTML code for the article


                //getting value of title from firestore and displaying it in title header
                String t=documentSnapshot.getString("title");
                titleHeader.setText(t);




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        final int[] isSaved = {0};
     saveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(isSaved[0] ==0)
             {
                 saveButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                 isSaved[0] =1;
             }
             else
             {
                 saveButton.setImageResource(R.drawable.ic_outline_bookmark_border_24);
                 isSaved[0] =0;
             }



         }
     });


    }


    // Getting back to HomeActivity on back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ViewPost.this, HomeActivity.class);
        startActivity(i);
    }
}