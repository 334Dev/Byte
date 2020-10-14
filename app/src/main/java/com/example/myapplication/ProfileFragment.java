package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private CircleImageView profileImageView;
    private TextView userName;
    private Button Logout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String UserID;
    StorageReference storageReference;
    private FirebaseFirestore fstore;
    private BlurImageView coverImage;
    private TextView post,followers,following;
    private AlertDialog.Builder builder;
    private AlertDialog show;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = root.findViewById(R.id.circleImageViewPro);
        userName = root.findViewById(R.id.username);
        Logout = root.findViewById(R.id.logoutBtn);
        coverImage=root.findViewById(R.id.dogBlurImageView);

        //Post, Follower, Following
        post=root.findViewById(R.id.post);
        followers=root.findViewById(R.id.followers);
        following=root.findViewById(R.id.following);

        //Loading Dialog Box
        builder=new AlertDialog.Builder(getContext());
        builder.setView(R.layout.loading_dailog);
        builder.setCancelable(true);
        show = builder.show();
        show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        UserID = mAuth.getCurrentUser().getUid();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //getImage
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
                Picasso.get().load(uri).into(coverImage);
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

        setUserDetails();

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent i = new Intent(getActivity(), LoginHome.class);
                startActivity(i);



            }
        });

        return root;


    }

    private void setUserDetails() {
        fstore.collection("Users").document(UserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String UserName=documentSnapshot.getString("Username");
                Double Followers=documentSnapshot.getDouble("Followers");
                Double Following=documentSnapshot.getDouble("Following");
                Double Post=documentSnapshot.getDouble("Post");

                post.setText(String.format("%.0f", Post));
                followers.setText(String.format("%.0f", Followers));
                following.setText(String.format("%.0f", Following));
                userName.setText(UserName);
                show.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000 && resultCode == RESULT_OK) {

            Uri resultUri = data.getData();
            profileImageView.setImageURI(resultUri);
            uploadImageTOFirebase(resultUri);
        }
    }

    private void uploadImageTOFirebase(Uri resultUri) {

        final StorageReference fileref=storageReference.child("users/"+mAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

}