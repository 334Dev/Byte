package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ViewPost extends AppCompatActivity {

     WebView web;
    private FirebaseFirestore fstore;
    private TextView titleHeader;
    private ImageView HeaderImage;
   private TextView ViewCount;
    private ImageView saveButton;
    private ImageView upvoteButton;
    private TextView upvoteCountText;
    private FirebaseAuth mAuth;

    private View parentLayout;
    StorageReference storageReference;
    private String UserID;
    private List<String> savedId,upvotearray;
    private String id;
    private Double upVoteCount;
    private Boolean VOTE_FLAG=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        savedId=new ArrayList<>();

        //firebase instantiation
        mAuth = FirebaseAuth.getInstance();
        UserID=mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        saveButton=findViewById(R.id.imageViewSaved);

        //webView to show the article
        web=findViewById(R.id.webView);

        //webView settings
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);

        parentLayout = findViewById(android.R.id.content);

        HeaderImage=findViewById(R.id.postCover);
        titleHeader=findViewById(R.id.postTitle);
        ViewCount=findViewById(R.id.viewCount);


        upvoteButton=findViewById(R.id.Upvotebtn);
        upvoteCountText=findViewById(R.id.UpvoteCount);

        //getting postID intent
        Intent intent=getIntent();
        id=intent.getStringExtra("PostId"); // getting PostId from Intent


        //passing the text which contain html code to web view
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


        //Header Image
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String coverImgRef=documentSnapshot.getString("img");
               Picasso.get().load(coverImgRef).into(HeaderImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                 Snackbar.make(parentLayout,"Failed to load cover image",Snackbar.LENGTH_SHORT).show();
            }
        });

        //checking if post already saved
        getSavedId();

        //updating the value of upvotes
        updateUpVoteCounts();


        //check already Up voted
        onCreateCheckUpVote();



        //setting up total views
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 Double Views=documentSnapshot.getDouble("viewCount");
                fstore.collection("Post").document(id).update("viewCount",Views+1);
                Views=documentSnapshot.getDouble("viewCount");
                ViewCount.setText(String.format("%.0f",Views));


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("View Not Updated","View Not Updated");
            }
        });


        //upVote setonClickListener
        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(VOTE_FLAG){
                    Log.i("onClickUpVote","remove user");
                    removeUserUpdate();
                    Log.i("removeUser", "onClick: ");
                }
                else {
                    Log.i("AddUser","onClick");
                    addUserUpvote();
                }

            }

        });



    //saveButton OnClick
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

    private void onCreateCheckUpVote() {
        fstore.collection("Post").document(id).collection("upVotes")
                .whereEqualTo("ProfileId",UserID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    Log.i("status", "onEvent: Has not upVoted");
                    VOTE_FLAG=false;
                }else{
                    //set button drawable if already voted
                    upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);
                    VOTE_FLAG=true;
                }
            }
        });
    }

    private void addUserUpvote() {
        fstore=FirebaseFirestore.getInstance();
        Map<String,Object> UpvoteMap=new HashMap<>();
        UpvoteMap.put("ProfileId",UserID);
        String name= UUID.randomUUID().toString();
        fstore.collection("Post").document(id).collection("upVotes").document(name).set(UpvoteMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //setUpVote
                            fstore.collection("Post").document(id).update("UpVote", upVoteCount + 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);
                                    upVoteCount=upVoteCount+1;
                                    upvoteCountText.setText(String.format("%.0f",upVoteCount));
                                    VOTE_FLAG=true;
                                    Log.i("AddUser", "onSuccess"+VOTE_FLAG);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("AddUser", "onFailure: "+e.getMessage());
                                }
                            });

                        }
                    }
                });
    }

    private void removeUserUpdate() {
        fstore=FirebaseFirestore.getInstance();
        fstore.collection("Post").document(id).collection("upVotes")
                .whereEqualTo("ProfileId",UserID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot doc: value){
                    doc.getReference().delete();
                    fstore.collection("Post").document(id).update("UpVote", upVoteCount -1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            upvoteButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                            upVoteCount=upVoteCount-1;
                            upvoteCountText.setText(String.format("%.0f",upVoteCount));
                            VOTE_FLAG=false;
                            Log.i("removeUser", "onSuccess"+VOTE_FLAG);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("removeUser", "onFailure: "+e.getMessage());
                        }
                    });
                }
            }
        });

    }


    // getting value of number of UpVote from database and showing in this activity..
    private void updateUpVoteCounts() {

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                upVoteCount=documentSnapshot.getDouble("UpVote");
                upvoteCountText.setText(String.format("%.0f",upVoteCount));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
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