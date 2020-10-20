package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements LatestAdapter.SelectedItem{

    private CircleImageView profileImageView;
    private TextView userName;
    private Button Logout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String UserID;
    StorageReference storageReference;
    private FirebaseFirestore fstore;
    private ImageView coverImage;
    private TextView post,followers,following;
    private AlertDialog.Builder builder;
    private AlertDialog show;
    private LatestAdapter ProfilePostsAdapter;
    private List<Model_Latest> ProfilePostsItem;
    private RecyclerView profileRecyclerView;
    private DocumentSnapshot lastProfilePost;
    private Query query;

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



        profileRecyclerView=root.findViewById(R.id.ProfileRecyclerView);
        profileRecyclerView.setHasFixedSize(true);
        profileRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ProfilePostsItem=new ArrayList<>();
        ProfilePostsAdapter=new LatestAdapter(ProfilePostsItem,this);
        profileRecyclerView.setAdapter(ProfilePostsAdapter);
        profileRecyclerView.setHasFixedSize(true);

        profileRecyclerView.setNestedScrollingEnabled(false);

        profileRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) profileRecyclerView.getLayoutManager();
                if(linearLayoutManager.findLastCompletelyVisibleItemPosition()==profileRecyclerView.getChildCount()){
                    setProfilePosts();
                }
            }
        });


          setProfilePosts();


        //getImage
        StorageReference profileRef = storageReference.child("users/" + mAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //setting profile picture
                Picasso.get().load(uri).into(profileImageView);
                //setting cover image
                Picasso.get().load(uri).transform(new BlurTransformation(getContext(),25,3)).into(coverImage);
            }
        });
        //change profile picture
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

    private void setProfilePosts() {


         if(lastProfilePost==null)
         {
             query=fstore.collection("Post")
                     .orderBy("time", Query.Direction.DESCENDING)
                     .whereEqualTo("Owner",UserID)
                     .limit(1000);

         }else{
             query=fstore.collection("Post")
                     .orderBy("time", Query.Direction.DESCENDING)
                     .whereEqualTo("Owner",UserID)
                     .startAfter(lastProfilePost)
                     .limit(1000);
         }

         query.addSnapshotListener(new EventListener<QuerySnapshot>() {
             @Override
             public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
               if(value.isEmpty())
                   Toast.makeText(getContext(),"You have no posts",Toast.LENGTH_SHORT).show();
               else {

                   for (QueryDocumentSnapshot doc : value) {

                       Log.i("dataRecieveCHeck", "onEvent:" + value.size());
                       Model_Latest set = doc.toObject(Model_Latest.class);
                       ProfilePostsItem.add(set);
                       ProfilePostsAdapter.notifyDataSetChanged();
                       show.dismiss();

                   }
                   lastProfilePost = value.getDocuments().get(value.size() - 1);
               }

             }
         });



    }

    //setting user profile details; post, followers, following
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
    //uploading new image in firebase
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

    @Override
    public void selectedItem(Model_Latest model_latest) {

        Intent i=new Intent(getActivity(),ViewPost.class);
        i.putExtra("PostId",model_latest.ID);
        startActivity(i);

    }
}