package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.UUID;

public class SetPostTitle extends AppCompatActivity{

    ImageView TitleImage;
    Button finishBtn;
    EditText Title;
    Spinner Tag;
    EditText Desc;
    ProgressBar loading;
    private String post_Title,post_Tag,post_Desc,UserID,FileName;
    private Uri titleUri;
    private Integer IMAGE_ADDED=0;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_post_title);

        TitleImage = findViewById(R.id.ArticleimageView);
        Title = findViewById(R.id.editTextTitle);
        Tag = findViewById(R.id.spinnerTag);
        Desc = findViewById(R.id.editTextDesc);
        finishBtn=findViewById(R.id.FinishBtn);

        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        String TimeMillie= String.valueOf(System.currentTimeMillis());
        FileName=UserID+TimeMillie;

        storageReference= FirebaseStorage.getInstance().getReference();

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,
                R.array.Tag_Spinner,
                R.layout.onselected_tag
                );
        adapter.setDropDownViewResource(R.layout.ondropdown_tag);

        Tag.setAdapter(adapter);
        //Tag.getOnItemSelectedListener().toString();



        TitleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(3,2).start(SetPostTitle.this);
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_Title = Title.getText().toString();
                post_Desc = Desc.getText().toString();
                post_Tag=Tag.getSelectedItem().toString();
                if(IMAGE_ADDED==0){
                    Toast.makeText(getApplicationContext(), "Add a cover image",Toast.LENGTH_LONG).show();
                }else if(post_Title.isEmpty()){
                    Title.setError("Field Empty");
                }else if(post_Desc.isEmpty()){
                    Desc.setError("Field Empty");
                }else{
                    storageReference.child("posts").child(FileName).child("title.jpg").putFile(titleUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot.getMetadata().getReference()!=null){
                                Task<Uri> result=taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Intent i=new Intent(SetPostTitle.this, CreatePost.class);
                                        i.putExtra("FileName", FileName);
                                        i.putExtra("TitleImage",uri.toString());
                                        i.putExtra("Tag",post_Tag);
                                        i.putExtra("Title",post_Title);
                                        i.putExtra("Desc",post_Desc);
                                        startActivity(i);
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
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            Uri resultUri=result.getUri();
            titleUri=resultUri;
            Picasso.get().load(resultUri).into(TitleImage);
            IMAGE_ADDED=1;
        }
    }

}