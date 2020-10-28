package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginDetails extends AppCompatActivity {

    private static final String TAG ="User Details" ;
    private CircleImageView profileImage;
    private EditText UserName;
    private FirebaseAuth mAuth;
    private Button finishbtn;

    private String UserID;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private CheckBox radio1,radio2,radio3,radio4,radio5,radio6,radio7;
    private View parentLayout;
    private Integer USERNAME_ALREADY;
    private List<String> keyword;
    private List<String> Tag;
    private ProgressBar loading;
    private Integer UPLOADING=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);

        parentLayout = findViewById(android.R.id.content);

        loading=findViewById(R.id.detailsLoading);
        loading.setVisibility(View.INVISIBLE);

        radio1=findViewById(R.id.radioButton1);
        radio2=findViewById(R.id.radioButton2);
        radio3=findViewById(R.id.radioButton3);
        radio4=findViewById(R.id.radioButton4);
        radio5=findViewById(R.id.radioButton5);
        radio6=findViewById(R.id.radioButton6);
        radio7=findViewById(R.id.radioButton7);

        Tag=new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        profileImage = findViewById(R.id.circleImageView);
        UserName = findViewById(R.id.editTextTextPersonName);
        finishbtn = findViewById(R.id.finishBtn);
        firestore = FirebaseFirestore.getInstance();
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
            UPLOADING=0;
             CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(LoginDetails.this);
         }
     });
     USERNAME_ALREADY=1;
     finishbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            USERNAME_ALREADY=1;

             selectionListUpdate();
             if(Tag.isEmpty()){
                 Snackbar.make(parentLayout, "Select at least one", Snackbar.LENGTH_SHORT).show();
                 USERNAME_ALREADY=0;
             }
             else if(UserName.getText().toString().isEmpty()){
                 UserName.setError("Username is empty");
                 USERNAME_ALREADY=0;
             }
             else if(UPLOADING!=1){
                 Snackbar.make(parentLayout, "Image is uploading", Snackbar.LENGTH_SHORT).show();
             }
             else{
                 loading.setVisibility(View.VISIBLE);
                 firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                     @Override
                     public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                         Log.i(TAG, "onSuccess: checkUsername");
                         List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                         for (DocumentSnapshot snapshot : snapshotList) {
                             if (snapshot.getString("Username").equals(UserName.getText().toString())) {
                                 Log.i(TAG, "onSuccess: Equal");
                                 UserName.setError("Username already exist");
                                 USERNAME_ALREADY=0;
                                 Log.i("LastCheck1", "onClick: "+USERNAME_ALREADY);
                                 break;
                             }
                         }
                         Log.i("LastCheck2", "onClick: "+USERNAME_ALREADY);
                         if(USERNAME_ALREADY==1) {
                             registerUser();
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.i(TAG, "onFailure: "+e.getMessage());
                         USERNAME_ALREADY=0;
                         loading.setVisibility(View.INVISIBLE);
                     }
                 });
             }

         }
     });



    }

    private void registerUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        String UID=user.getUid();
        Map<String, Object> map=new HashMap<>();
        createKeyword(UserName.getText().toString());
        map.put("Username", UserName.getText().toString());
        map.put("UserID",mAuth.getCurrentUser().getUid());
        map.put("Tag",Tag);
        map.put("Followers",0);
        map.put("Following",0);
        map.put("Post",0);
        map.put("Saved",0);
        map.put("keyword",keyword);
        firestore.collection("Users").document(UID).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(parentLayout, "Welcome "+UserName.getText().toString(), Snackbar.LENGTH_SHORT).show();
                Intent i=new Intent( LoginDetails.this, HomeActivity.class);
                startActivity(i);
                loading.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                loading.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createKeyword(String Username) {
        keyword=new ArrayList<>();
        char tit[]=Username.toCharArray();
        String str="";
        for(int i=0;i<Username.length();i++){
            str=str+tit[i];
            keyword.add(str);
        }
    }


    private void selectionListUpdate() {
        Log.i("selection", "selectionListUpdate: True");
        if(radio1.isChecked()){
            Log.i(TAG, "selectionListUpdate: web");
            Tag.add("Web Development");
        }
        if(radio2.isChecked()){
            Tag.add("App Development");
        }
        if(radio3.isChecked()){
            Tag.add("Competitive Programming");
        }
        if(radio4.isChecked()){
            Tag.add("Politics");
        }
        if(radio5.isChecked()){
            Tag.add("T.V. Series");
        }
        if(radio6.isChecked()){
            Tag.add("Automobile");
        }
        if(radio7.isChecked()){
            Tag.add("Literature");
        }
        Log.i(TAG, "onClick$"+Tag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            Uri resultUri=result.getUri();

            Picasso.get().load(resultUri).into(profileImage);
            uploadImageTOFirebase(resultUri);
        }

    }

    private void uploadImageTOFirebase(Uri imageUri) {
            loading.setVisibility(View.VISIBLE);
            final StorageReference Fileref=storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
            Fileref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            UPLOADING=1;
                            Picasso.get().load(uri).into(profileImage);
                            loading.setVisibility(View.INVISIBLE);
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   Toast.makeText(LoginDetails.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

    }
}