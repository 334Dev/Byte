package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditTags extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;
    private CheckBox radio1,radio2,radio3,radio4,radio5,radio6,radio7;
    private List<String> Tag;
    private Button updatebtn;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);

        parentLayout = findViewById(android.R.id.content);

        radio1=findViewById(R.id.radio1);
        radio2=findViewById(R.id.radio2);
        radio3=findViewById(R.id.radio3);
        radio4=findViewById(R.id.radio4);
        radio5=findViewById(R.id.radio5);
        radio6=findViewById(R.id.radio6);
        radio7=findViewById(R.id.radio7);

        firestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        Tag=new ArrayList<>();

        updatebtn=findViewById(R.id.updateBtn);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectionListUpdate();
                if(Tag.isEmpty()){
                    Snackbar.make(parentLayout, "Select at least one", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    firestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("Tag",Tag).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i("updateSuccess", "onSuccess: Successfully updated");
                            Intent i=new Intent(EditTags.this, HomeActivity.class);
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("updateFailed", "onFailure: "+e.getMessage());
                        }
                    });
                }

            }
        });
    }

    private void selectionListUpdate() {
        Log.i("selection", "selectionListUpdate: True");
        if(radio1.isChecked()){
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
    }
}