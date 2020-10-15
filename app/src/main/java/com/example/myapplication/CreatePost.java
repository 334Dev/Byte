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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class CreatePost extends AppCompatActivity {

    private RichEditor mEditor;
    private TextView mPreview;
    private FloatingActionButton done;
    private FirebaseFirestore fstore;
    private FirebaseAuth mAuth;
    StorageReference storageReference;
    private String UserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        mEditor=(RichEditor)findViewById(R.id.editor);
        mEditor.setEditorHeight(200);
        mEditor.setEditorFontSize(16);
        mEditor.setEditorFontColor(getColor(R.color.plainText));
        mEditor.setBackgroundColor(getColor(R.color.Background));


        done=findViewById(R.id.doneButton);
        mAuth = FirebaseAuth.getInstance();
      //  UserID = mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        mEditor.setPadding(10,10,10,10);
        mEditor.setPlaceholder("Type Here...");



        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.setBold();
            }
        });


        findViewById(R.id.action_italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.setItalic();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);


            }
        });

        findViewById(R.id.action_insert_youtube).setOnClickListener(new View.OnClickListener() {
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

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
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


        findViewById(R.id.action_align_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignLeft();
            }
        });

        findViewById(R.id.action_align_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditor.setAlignCenter();
            }
        });





        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String text= mEditor.getHtml();
                Map<String,String> map=new HashMap<>();
                map.put("Post",text);
                fstore.collection("Post").document("hello").set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CreatePost.this,"fdsgffd",Toast.LENGTH_LONG).show();
                        Intent i=new Intent(CreatePost.this,ViewPost.class);
                        startActivity(i);
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
        if (requestCode == 1000 && resultCode == RESULT_OK) {

            Uri resultUri = data.getData();
            mEditor.insertImage(resultUri.toString(), "Image Not Loaded",320);
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Uri resultUri = data.getData();
            mEditor.insertAudio(resultUri.toString());

        }

        if (requestCode == 2 && resultCode == RESULT_OK) {

            Uri resultUri = data.getData();

            mEditor.insertVideo(resultUri.toString(),360);

        }


    }
}