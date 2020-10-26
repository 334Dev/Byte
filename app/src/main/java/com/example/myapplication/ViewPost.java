package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewPost extends AppCompatActivity implements commentAdapter.SelectedItem {

    private WebView web;
    private FirebaseFirestore fstore;
    private TextView titleHeader;
    private ImageView HeaderImage;
    private TextView ViewCount;
    private ImageView saveButton;
    private ImageView upvoteButton;
    private TextView upvoteCountText;
    private FirebaseAuth mAuth;

    private View parentLayout;
    private StorageReference storageReference;
    private String UserID;
    private List<String> savedId,upvotearray;
    private String id;
    private Double upVoteCount;


    //comments
    private TextView addComment;
    private Button sendComment;
    private RecyclerView commentRecycler;
    private List<commentModel> commentModels;
    private commentAdapter commentadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        savedId=new ArrayList<>();

        //comments
        addComment=findViewById(R.id.addComment);
        sendComment=findViewById(R.id.sendComment);
        commentRecycler=findViewById(R.id.commentRecycler);

        commentRecycler.setLayoutManager(new LinearLayoutManager(ViewPost.this));

        commentModels=new ArrayList<>();
        commentadapter=new commentAdapter(commentModels,this);
        commentRecycler.setAdapter(commentadapter);

        commentRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setHasFixedSize(true);


        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().isEmpty()){
                    addComment.setError("Comment is Empty");
                }else{
                    fstore.collection("Users").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String username=documentSnapshot.getString("Username");
                            addCommenttoFstore(username);
                        }
                    });
                }
            }
        });



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

        getUpvoteArray();

        //set Comments
        setComments();

        setUpvoteCount();
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


         upvoteButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if(upvoteButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.thumbs_up_clicked).getConstantState()){
                     Map<String,Object> upVoteMap=new HashMap<>();
                     upVoteMap.put("UpVoteArray",FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                     fstore.collection("Post").document(id).update(upVoteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                             upvoteButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                             upvotearray.clear();
                             fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                 @Override
                                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                                      upVoteCount= (Double) documentSnapshot.get("UpVote");
                                      fstore.collection("Post").document(id).update("UpVote",upVoteCount-1);
                                      setUpvoteCount();
                                 }
                             });
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Snackbar.make(parentLayout,"Upvote Failed",Snackbar.LENGTH_SHORT).show();
                         }
                     });
                 }
                 else if(upvoteButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_thumb_up_alt_24).getConstantState())
                 {
                     upvotearray.add(mAuth.getCurrentUser().getUid());
                     Map<String,Object> upVoteMap=new HashMap<>();
                     upVoteMap.put("UpVoteArray",upvotearray);
                     fstore.collection("Post").document(id).update(upVoteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void aVoid) {
                             upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);
                             fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                 @Override
                                 public void onSuccess(DocumentSnapshot documentSnapshot) {
                                     upVoteCount= (Double) documentSnapshot.get("UpVote");
                                     fstore.collection("Post").document(id).update("UpVote",upVoteCount+1);
                                     setUpvoteCount();
                                 }
                             });
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Snackbar.make(parentLayout,"Failed",Snackbar.LENGTH_SHORT).show();
                         }
                     });
                 }
             }
         });




    //saveButton OnClick
     saveButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {

             if(saveButton.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_bookmark_24).getConstantState()){
                 //Toast.makeText(getApplicationContext(),"if block",Toast.LENGTH_LONG).show();
                 Map<String,Object> map=new HashMap<>();
                 map.put("SavedId", FieldValue.arrayRemove(mAuth.getCurrentUser().getUid()));
                 fstore.collection("Post").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void aVoid) {
                         saveButton.setImageResource(R.drawable.ic_outline_bookmark_border_24);
                         savedId.clear();

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

    private void setUpvoteCount() {

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                  upVoteCount= (Double) documentSnapshot.get("UpVote");
                  upvoteCountText.setText(String.format("%.0f",upVoteCount));

            }
        });


    }

    private void getUpvoteArray() {

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                upvotearray= (List<String>) documentSnapshot.get("UpVoteArray");
                if(upvotearray.contains(mAuth.getCurrentUser().getUid()))
                {
                    upvoteButton.setImageResource(R.drawable.thumbs_up_clicked);

                }
                else{

                    upvoteButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Msg",e.getMessage());
            }
        });


    }

    private void addCommenttoFstore(String username) {
        fstore=FirebaseFirestore.getInstance();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String sDate=sdf.format(date);
        try {
            date=sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Comments", "addCommenttoFstore: "+sDate);
        Map<String, Object> map=new HashMap<>();
        map.put("username",username);
        map.put("date",sDate);
        map.put("comment",addComment.getText().toString());

        fstore.collection("Post").document(id).collection("Comments").document().set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Comments", "onFailure: "+e.getMessage());
            }
        });

    }

    private void setComments() {
        fstore=FirebaseFirestore.getInstance();
        fstore.collection("Post").document(id).collection("Comments")
                .orderBy("date", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Log.i("Comments", "onSuccess: No Comments");
                }else{
                    List<DocumentSnapshot> value =queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot docs: value){
                        commentModels.add(docs.toObject(commentModel.class));
                        commentadapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Comments", "onFailure: "+e.getMessage());
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

    @Override
    public void selectedItem(commentModel commentModel_) {
        //future
    }
}