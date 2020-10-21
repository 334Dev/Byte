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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPost extends AppCompatActivity {

     WebView web;
    private FirebaseFirestore fstore;
    private TextView titleHeader;
    private ImageView HeaderImage;
    private ImageView saveButton;
    private ImageView upvoteButton;
    private TextView upvoteCountText;
    private FirebaseAuth mAuth;
    private View parentLayout;
    StorageReference storageReference;
    private String UserID;
    private List<String> savedId,upvote;
    private String id;
    private int numberOfUpvotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        savedId=new ArrayList<>();

        saveButton=findViewById(R.id.imageViewSaved);
        web=findViewById(R.id.webView);
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);

        parentLayout = findViewById(android.R.id.content);
        fstore = FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        HeaderImage=findViewById(R.id.headerimg);
        titleHeader=findViewById(R.id.postTitle);

        upvoteButton=findViewById(R.id.Upvotebtn);
        upvoteCountText=findViewById(R.id.UpvoteCount);

        //passing the text which contain html code to web view
        Intent intent=getIntent();
         id=intent.getStringExtra("PostId"); // getting PostId from Intent

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

        getSavedId();

        //Searching if already upvoted

        updateUpVoteCounts();

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                upvote= (List<String>) documentSnapshot.get("UpVote");

                if(upvote.contains(mAuth.getCurrentUser().getUid()))
                {
                    upvoteButton.setImageResource(R.drawable.upvoted);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Msg",e.getMessage());
            }
        });




        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upvoteButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.upvoted).getConstantState()){
                    Map<String,Object> UpvoteMap=new HashMap<>();
                    UpvoteMap.put("UpVote",FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                    fstore.collection("Post").document(id).update(UpvoteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            upvoteButton.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
                            upvote.clear();
                              updateUpVoteCounts();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                             Snackbar.make(parentLayout,"There is Some Problem happening",Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                   else{
                       upvote.add(mAuth.getCurrentUser().getUid());
                       Map<String,Object> UpvoteMap=new HashMap<>();
                       UpvoteMap.put("UpVote",upvote);
                       fstore.collection("Post").document(id).update(UpvoteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               upvoteButton.setImageResource(R.drawable.upvoted);
                               updateUpVoteCounts();

                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Snackbar.make(parentLayout,"There is some problem happpening",Snackbar.LENGTH_SHORT).show();
                           }
                       });
                }

            }

        });




     saveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(saveButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_bookmark_24).getConstantState()){
                 Toast.makeText(getApplicationContext(),"if block",Toast.LENGTH_LONG).show();
                 Map<String,Object> map=new HashMap<>();
                 map.put("SavedId", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                 fstore.collection("Post").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         saveButton.setImageResource(R.drawable.ic_outline_bookmark_border_24);

                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Snackbar.make(parentLayout, "saved Failed", Snackbar.LENGTH_SHORT).show();
                     }
                 });


             }
              else{
                 savedId.add(mAuth.getCurrentUser().getUid());
                 Map<String,Object> map=new HashMap<>();
                 map.put("SavedId",savedId);
                 fstore.collection("Post").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         saveButton.setImageResource(R.drawable.ic_baseline_bookmark_24);
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Snackbar.make(parentLayout, "saved Failed", Snackbar.LENGTH_SHORT).show();
                     }
                 });
             }
         }
     });


    }

    private void updateUpVoteCounts() {

           fstore.collection("Post").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot=task.getResult();
                     List<String> upvotesList= (List<String>) documentSnapshot.get("UpVote");
                     numberOfUpvotes=upvotesList.size();

               }
           });

            fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Double upvoteCounts = documentSnapshot.getDouble("UpVoteCount");

                    fstore.collection("Post").document(id).update("UpVoteCount", numberOfUpvotes);


                   upvoteCountText.setText(Integer.toString(numberOfUpvotes));

                }
            });
    }


    private void getSavedId() {
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                savedId= (List<String>) documentSnapshot.get("SavedId");
                if(savedId.contains(mAuth.getCurrentUser().getUid()))
                {
                    saveButton.setImageResource(R.drawable.ic_baseline_bookmark_24);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Msg",e.getMessage());
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