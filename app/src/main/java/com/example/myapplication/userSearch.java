package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class userSearch extends AppCompatActivity implements userSearchAdapter.SelectedItem {

    private RecyclerView UserSearchRecycler;
    private TextView UserSearchText;
    private FirebaseFirestore fstore;
    private List<userSearchModel> userModel;
    private userSearchAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        UserSearchRecycler=findViewById(R.id.UserSearchRecycler);
        UserSearchText=findViewById(R.id.UserSearchText);
        UserSearchText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_search_24, 0, 0, 0);
        fstore=FirebaseFirestore.getInstance();

        userModel=new ArrayList<>();
        userAdapter=new userSearchAdapter(userModel,this);
        UserSearchRecycler.setAdapter(userAdapter);

        UserSearchRecycler.hasFixedSize();
       UserSearchRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        UserSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                getUserFirestore(UserSearchText.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getUserFirestore(String searchKeyword) {
         fstore.collection("Users").orderBy("Username").startAt(searchKeyword).endAt("$searchKeyword\uf8ff")
                 .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   if(task.isSuccessful())
                   {
                     userModel=task.getResult().toObjects(userSearchModel.class);
                     userAdapter.notifyDataSetChanged();
                   }
                   else{

                   }
             }
         });



    }

    @Override
    public void selectedItem(userSearchModel userModel) {

        Intent i=new Intent(userSearch.this,AnotherUserProfile.class);
        i.putExtra("SearchUserName",userModel.Username);

        startActivity(i);

    }
}