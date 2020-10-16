package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SetPostTitle extends AppCompatActivity {

    ImageView TitleImage;
    EditText Title;
    EditText Tag;
    EditText Desc;
    ProgressBar loading;
    String post_Title;
    String post_Desc;
    String post_Tag;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_post_title);

        TitleImage = findViewById(R.id.ArticleimageView);
        Title = findViewById(R.id.editTextTitle);
        Tag = findViewById(R.id.editTextTag);
        Desc = findViewById(R.id.editTextDesc);

        post_Title = Title.getText().toString();
        post_Desc = Desc.getText().toString();
        post_Tag = Tag.getText().toString();

        TitleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 100);
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




    }
}