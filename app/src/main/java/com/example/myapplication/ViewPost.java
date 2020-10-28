package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
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

import java.lang.reflect.Field;
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
    private ImageView reportBtn;

    private View parentLayout;
    private StorageReference storageReference;
    private String UserID;
    private List<String> savedId,upvotearray,reportUser,reportList;
    private String id;
    private Double upVoteCount,trend;


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


        HeaderImage=findViewById(R.id.postCover);
        titleHeader=findViewById(R.id.postTitle);
        ViewCount=findViewById(R.id.viewCount);
        upvoteButton=findViewById(R.id.Upvotebtn);
        upvoteCountText=findViewById(R.id.UpvoteCount);
        reportBtn=findViewById(R.id.reportBtn);
        saveButton=findViewById(R.id.imageViewSaved);


        //parent layout for Snack Bar
        parentLayout = findViewById(android.R.id.content);

        savedId=new ArrayList<>();

        //comments
        addComment=findViewById(R.id.addComment);
        sendComment=findViewById(R.id.sendComment);
        commentRecycler=findViewById(R.id.commentRecycler);
        commentRecycler.setLayoutManager(new LinearLayoutManager(ViewPost.this));

        //model class and adapter for comments
        commentModels=new ArrayList<>();
        commentadapter=new commentAdapter(commentModels,this);
        commentRecycler.setAdapter(commentadapter);

        commentRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setHasFixedSize(true);

        //post your comment
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
                            //method to add comment to database
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


        //webView to show the article
        web=findViewById(R.id.webView);

        //webView settings
        web.setBackgroundColor(getColor(R.color.Background));
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setLoadWithOverviewMode(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);


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

        //getting list of user already upvoted
        getUpvoteArray();

        //checking if already reported
        getReportArray();

        //set Comments
        setComments();

        //get upVote count
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


        //report Btn on click
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if already reported
               if(reportBtn.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.reported).getConstantState()){
                    Snackbar.make(parentLayout,"You already reported this post.If found against our community guidelines we will remove it",Snackbar.LENGTH_LONG).show();
                }

               //else
                else if(reportBtn.getDrawable().getConstantState()==getResources().getDrawable(R.drawable.ic_baseline_report_24).getConstantState()){                   AlertDialog.Builder alert = new AlertDialog.Builder(ViewPost.this);

                   // alert dialog box for entering the report text
                   alert.setTitle("Report the post");
                   alert.setMessage("Tell us the reason so we could speed up the process");
                   final String[] value = new String[1];
                   final EditText input = new EditText(ViewPost.this);
                   alert.setView(input);
                   alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {

                           //getting report reason from dialog box
                           String reportReason = input.getText().toString();

                           //snackbar of the report reason
                           Snackbar.make(parentLayout,reportReason,Snackbar.LENGTH_LONG).show();

                           //adding the report message
                           fstore.collection("Post").document(id).update("reportList",FieldValue.arrayUnion(reportReason)).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                            //increasing report count
                                           fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                               @Override
                                               public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                   Double reportCount = documentSnapshot.getDouble("Report");
                                                   fstore.collection("Post").document(id).update("Report", reportCount + 1);

                                                   //adding the user to database of reportList
                                                   setReportArray();
                                                   
                                               }
                                           });
                               }
                           });

                       }
                   });
                   alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int whichButton) {
                           //cancelled
                       }
                   });
                   alert.show();
                }
            }
        });


        //upVote setOnClickListener
         upvoteButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //if already upvoted remove the upvote
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
                                      upVoteCount=documentSnapshot.getDouble("UpVote");
                                      trend=documentSnapshot.getDouble("trend");
                                      fstore.collection("Post").document(id).update("Trend",trend-1);
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
                 //else add the upvote
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
                                     //getting upVote count
                                     upVoteCount= (Double) documentSnapshot.get("UpVote");

                                     //getting trend points of the post
                                     trend=documentSnapshot.getDouble("trend");

                                     //updating trend and upVote
                                     fstore.collection("Post").document(id).update("Trend",trend-1);
                                     fstore.collection("Post").document(id).update("UpVote",upVoteCount+1);

                                     //increasing upVote Count
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

    //setting up report- if already reported change in button drawable
    private void setReportArray() {
        fstore.collection("Post").document(id).update("reportUser", FieldValue.arrayUnion(UserID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reportBtn.setImageResource(R.drawable.reported);
            }
        });
    }

    //onCreate check if already reported
    private void getReportArray() {
        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                reportUser= (List<String>) documentSnapshot.get("reportUser");
                if(reportUser.contains(mAuth.getCurrentUser().getUid())){
                    reportBtn.setImageResource(R.drawable.reported);
                }
                else{
                    reportBtn.setImageResource(R.drawable.ic_baseline_report_24);
                }
            }
        });

    }

    //setting the upVote count in text View;
    private void setUpvoteCount() {

        fstore.collection("Post").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                  upVoteCount= documentSnapshot.getDouble("UpVote");
                  upvoteCountText.setText(String.format("%.0f",upVoteCount));

            }
        });


    }

    //checking if already upVoted setting the drawable of upVote btn
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

    // adding user comment in firestore
    private void addCommenttoFstore(final String username) {
        fstore=FirebaseFirestore.getInstance();
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
        final String sDate=sdf.format(date);
        try {
            date=sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Comments", "addCommenttoFstore: "+sDate);
        final Map<String, Object> map=new HashMap<>();
        map.put("username",username);
        map.put("date",sDate);
        map.put("comment",addComment.getText().toString());

        fstore.collection("Post").document(id).collection("Comments").document().set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                        commentModels.add(new commentModel(addComment.getText().toString(),sDate,username));
                        commentadapter.notifyDataSetChanged();
                        addComment.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Comments", "onFailure: "+e.getMessage());
            }
        });

    }

    //setting up comments in the comment recycler view
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


    //checking if already saved the article or not and changing the drawable accordingly
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