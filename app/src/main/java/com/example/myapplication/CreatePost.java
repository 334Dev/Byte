package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jp.wasabeef.richeditor.RichEditor;

import static com.example.myapplication.R.drawable.bold_clicked;
import static com.example.myapplication.R.drawable.center_align_clicked;
import static com.example.myapplication.R.drawable.ic_baseline_format_align_center_24;
import static com.example.myapplication.R.drawable.ic_baseline_format_align_left_24;
import static com.example.myapplication.R.drawable.ic_baseline_format_bold_24;
import static com.example.myapplication.R.drawable.ic_baseline_format_italic_24;
import static com.example.myapplication.R.drawable.ic_baseline_image_24;
import static com.example.myapplication.R.drawable.ic_baseline_play_circle_filled_24;
import static com.example.myapplication.R.drawable.image_insert_clicked;
import static com.example.myapplication.R.drawable.italic_clicked;
import static com.example.myapplication.R.drawable.left_align_clicked;
import static com.example.myapplication.R.drawable.link_clicked;
import static com.example.myapplication.R.drawable.youtube_clicked;

public class CreatePost extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private FloatingActionButton done;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private ProgressBar loading;
    private String UserID,FileName,TitleURI;

    private ImageButton bold_btn;
    private int Bold_flag =0;

    private ImageButton italic_btn;
    private int italic_flag=0;

    private  ImageButton left_align_btn;
    private int leftAlign_flag=0;

    private ImageButton center_align_btn;
    private int center_alignFlag=0;

    private ImageButton insert_image_btn;

    private ImageButton insert_youtube_btn;


    private ImageButton insert_link_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        mEditor=(RichEditor)findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(getColor(R.color.plainText));
        mEditor.setBackgroundColor(getColor(R.color.Background));
        loading=findViewById(R.id.createPostLoad);
        mAuth=FirebaseAuth.getInstance();
        UserID=mAuth.getCurrentUser().getUid();
        done=findViewById(R.id.doneButton);
        FileName=getIntent().getStringExtra("FileName");
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        mEditor.setPadding(10,10,10,10);
        mEditor.setPlaceholder("Type Here...");

        bold_btn=findViewById(R.id.action_bold);
        bold_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   if(Bold_flag==0) {
                       Bold_flag = 1;
                      bold_btn.setImageResource(bold_clicked);
                   }
                   else {
                       Bold_flag=0;
                       bold_btn.setImageResource(ic_baseline_format_bold_24);

                   }
                mEditor.setBold();
            }
        });

         italic_btn=findViewById(R.id.action_italic);
        italic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(italic_flag==0) {
                    italic_flag = 1;
                    italic_btn.setImageResource(italic_clicked);
                }
                else {
                    italic_flag=0;
                    italic_btn.setImageResource(ic_baseline_format_italic_24);

                }

                mEditor.setItalic();
            }
        });


           insert_image_btn=findViewById(R.id.action_insert_image);
        insert_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);


            }
        });

         insert_youtube_btn=findViewById(R.id.action_insert_youtube);
        insert_youtube_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(CreatePost.this);

                alert.setTitle("Youtube");
                alert.setMessage("Enter the Youtube Link here");
                final String[] value = new String[1];

                // Set an EditText view to get user input
                final EditText input = new EditText(CreatePost.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.i("link", "onClick: "+input.getText().toString());
                        String[] res = input.getText().toString().split("/",-2);
                        Log.i("link", "onClick: "+res[3]);
                        String embeddedUrl = "https://www.youtube.com/embed/"+res[3];
                        mEditor.insertYoutubeVideo(embeddedUrl,320);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

            }
        });


         insert_link_btn=findViewById(R.id.action_insert_link);
        insert_link_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alert = new AlertDialog.Builder(CreatePost.this);

                alert.setTitle("Enter Link");


                final EditText input1 = new EditText(CreatePost.this);
                input1.setHint("Enter Link here");
                alert.setView(input1);

              /*  final EditText input2 = new EditText(CreatePost.this);
                input2.setHint("Title of Link");
                alert.setView(input2);*/

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String res1 = input1.getText().toString();


                        mEditor.insertLink(res1,res1);

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();


                 }
        });


        findViewById(R.id.action_insert_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audio_upload=new Intent();
                audio_upload.setType("audio/*");
                audio_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(audio_upload,1);


            }
        });

        findViewById(R.id.action_insert_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent audio_upload=new Intent();
                audio_upload.setType("video/*");
                audio_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(audio_upload,2);

            }
        });



        left_align_btn=findViewById(R.id.action_align_left);
       left_align_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leftAlign_flag==0) {
                    leftAlign_flag = 1;
                    left_align_btn.setImageResource(left_align_clicked);
                }
                else {
                    leftAlign_flag=0;
                    left_align_btn.setImageResource(ic_baseline_format_align_left_24);

                }

                mEditor.setAlignLeft();
            }
        });



           center_align_btn=findViewById(R.id.action_align_center);
        center_align_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(center_alignFlag==0) {
                    center_alignFlag = 1;
                    center_align_btn.setImageResource(center_align_clicked);
                }
                else {
                    center_alignFlag=0;
                    center_align_btn.setImageResource(ic_baseline_format_align_center_24);

                }


                mEditor.setAlignCenter();
            }
        });





        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String text= mEditor.getHtml();
               Long time=System.currentTimeMillis();
               Intent intent=getIntent();
               String title=intent.getStringExtra("Title");
               String desc=intent.getStringExtra("Desc");
               String tag=intent.getStringExtra("Tag");
               String img=intent.getStringExtra("TitleImage");

               final Map<String,Object> userMap=new HashMap<>();
               userMap.put("PostName",FileName);
                Map<String,Object> map=new HashMap<>();
                map.put("ID",FileName);
                map.put("Post",text);
                map.put("img",img);
                map.put("time",time);
                map.put("title",title);
                map.put("desc",desc);
                map.put("tag",tag);
                map.put("UpVote",0);
                map.put("Report",0);
                map.put("viewCount",0);
                fstore.collection("Post").document(FileName).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fstore.collection("Users").document(UserID).collection("UserPost")
                                .document(FileName).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(CreatePost.this,"Posted",Toast.LENGTH_LONG).show();
                                Intent i=new Intent(CreatePost.this,ViewPost.class);
                                i.putExtra("PostId",FileName);
                                startActivity(i);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("UserPost", "onFailure: "+e.getMessage());
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreatePost.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        StorageReference reff=storageReference.child("posts").child(FileName);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            loading.setVisibility(View.VISIBLE);
            String randomName= UUID.randomUUID().toString()+".jpg";
            Uri resultUri = data.getData();
            reff.child(randomName).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata().getReference()!=null){
                        Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURI=uri.toString();
                                mEditor.insertImage(imageURI, "Image Not Loaded",320);
                                loading.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed get Download URI", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.INVISIBLE);
                }
            });


        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loading.setVisibility(View.VISIBLE);
            String randomName= UUID.randomUUID().toString()+".mp3";
            Uri resultUri = data.getData();
            reff.child(randomName).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata().getReference()!=null){
                        Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String audioURI=uri.toString();
                                mEditor.insertAudio(audioURI);
                                loading.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed get Download URI", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.INVISIBLE);
                }
            });

        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            loading.setVisibility(View.VISIBLE);
            String randomName= UUID.randomUUID().toString()+".mp4";
            Uri resultUri = data.getData();
            reff.child(randomName).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if(taskSnapshot.getMetadata().getReference()!=null){
                        Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String VideoURI=uri.toString();
                                mEditor.insertVideo(VideoURI);
                                loading.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed get Download URI", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload", Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.INVISIBLE);
                }
            });

        }


    }
}