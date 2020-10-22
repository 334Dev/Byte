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
    private int numberOfUpvotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        savedId=new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        UserID=mAuth.getCurrentUser().getUid();


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

        HeaderImage=findViewById(R.id.postCover);
        titleHeader=findViewById(R.id.postTitle);
        ViewCount=findViewById(R.id.viewCount);


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



        getSavedId();

        //Searching if already upvoted

        updateUpVoteCounts();



        fstore.collection("Post").document(id).collection("upVotes")
                .whereEqualTo("ProfileId",UserID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    Log.i("status", "onEvent: Has not upVoted");
                }else{
                    upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);
                }
            }
        });



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



        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(upvoteButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_thumb_up_alt_24).getConstantState()){
                     addUserUpvote();

                   }

                   else {
                         Log.i("Else Working","else eordd");
                         removeUserUpdate();
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

    private void addUserUpvote() {

        Map<String,Object> UpvoteMap=new HashMap<>();
        UpvoteMap.put("ProfileId",UserID);
        fstore.collection("Post").document(id).collection("upVotes").document(UserID).set(UpvoteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Double upvotesCounts = documentSnapshot.getDouble("UpVote");
                        fstore.collection("Post").document(id).update("UpVote", upvotesCounts + 1);
                        upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);

                        updateUpVoteCounts();
                    }
                });
            }
        });




    }

    private void removeUserUpdate() {

        fstore.collection("Post").document(id).collection("upVotes")
                .whereEqualTo("ProfileId",UserID).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot doc: value){
                    doc.getReference().delete();
                }
            }
        });
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Double upvotesCounts = documentSnapshot.getDouble("UpVote");
                fstore.collection("Post").document(id).update("UpVote", upvotesCounts -1);
                upvoteButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                updateUpVoteCounts();
            }
        });




    }

    private void updateUpVoteCounts() {

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                // getting value of number of UpVote from database and showing in this activity..

                Double mUpvote=documentSnapshot.getDouble("UpVote");


                upvoteCountText.setText(String.format("%.0f",mUpvote));




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