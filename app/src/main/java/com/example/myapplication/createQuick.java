package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.ChangeEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class createQuick extends AppCompatActivity {

    private TextView editTitle, editDesc;
    private TextView previewTitle, previewDesc;
    private Button addImage;
    private ImageView coverImage;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private Integer IMAGE_ADDED=0;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private Bitmap compressedImageFile;
    private byte[] finalImage;
    private FloatingActionButton DoneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quick);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        editTitle=findViewById(R.id.createQuick_title);
        editDesc=findViewById(R.id.createQuick_desc);

        coverImage=findViewById(R.id.quickCoverImage);
        addImage=findViewById(R.id.quickAddImage);

        previewDesc=findViewById(R.id.textView4);
        previewTitle=findViewById(R.id.textView16);

        DoneBtn=findViewById(R.id.quickDoneBtn);

        editTitle.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textTitle=editTitle.getText().toString();
                setPreviewTitle(textTitle);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        editDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String textDesc=editDesc.getText().toString();
                setPreviewDesc(textDesc);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(9,16).start(createQuick.this);
            }
        });


        DoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTitle.getText().toString().isEmpty()) {
                    editTitle.setError("Title Empty");
                } else if (editDesc.getText().toString().isEmpty()) {
                    editDesc.setError("Description is empty");
                } else if (IMAGE_ADDED != 1) {
                    Toast.makeText(getApplicationContext(), "No Image Added", Toast.LENGTH_SHORT).show();
                } else {
                    String Random = UUID.randomUUID().toString();
                    final StorageReference Fileref = storageReference.child("quick/" + mAuth.getCurrentUser().getUid() + "/" + Random + ".jpg");
                    Fileref.putBytes(finalImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    addtoFirestore(uri);
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(createQuick.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }


    private void addtoFirestore(Uri uri) {
        Map<String, Object> map=new HashMap<>();
        map.put("title", editTitle.getText().toString());
        map.put("desc", editDesc.getText().toString());
        map.put("image",uri.toString());
        map.put("owner",mAuth.getCurrentUser().getUid());
        map.put("time", System.currentTimeMillis());

        firestore.collection("Quick").document().set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i=new Intent(createQuick.this, HomeActivity.class);
                startActivity(i);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("createQuick", "onFailure: "+e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            Uri resultUri=result.getUri();

            File actualImage=new File(resultUri.getPath());
            try {
                compressedImageFile = new Compressor(this)
                        .setMaxWidth(1080)
                        .setMaxHeight(1920)
                        .setQuality(70)
                        .compressToBitmap(actualImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
            finalImage=byteArrayOutputStream.toByteArray();

            Picasso.get().load(resultUri).into(coverImage);
            IMAGE_ADDED=1;
        }
    }

    private void setPreviewDesc(String textDesc) {
        previewDesc.setText(textDesc);
    }

    private void setPreviewTitle(String textTitle) {
        previewTitle.setText(textTitle);
    }
}