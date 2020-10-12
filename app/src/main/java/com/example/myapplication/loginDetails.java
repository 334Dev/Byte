package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class loginDetails extends AppCompatActivity {

    private CircleImageView profileImage;
    private EditText UserName;
    private FirebaseAuth mAuth;
    private Button finishbtn;


    String UserID;
    StorageReference storageReference;
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.circleImageView);
        UserName = findViewById(R.id.editTextTextPersonName);
        finishbtn = findViewById(R.id.finishBtn);
        fstore = FirebaseFirestore.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");



        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);

            }
        });


     profileImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            Intent openGallery =new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(openGallery,1000);
         }
     });

     finishbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             Intent i= new Intent(loginDetails.this,HomeActivity.class);
             startActivity(i);
         }
     });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000 && resultCode==RESULT_OK){

            Uri resultUri=data.getData();
            profileImage.setImageURI(resultUri);
            uploadImageTOFirebase(resultUri);
        }

    }

    private void uploadImageTOFirebase(Uri imageUri) {

            final StorageReference Fileref=storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
            Fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(profileImage);
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   Toast.makeText(loginDetails.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

    }
}