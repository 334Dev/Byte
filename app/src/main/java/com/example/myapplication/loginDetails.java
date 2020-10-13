package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class loginDetails extends AppCompatActivity {

    private CircleImageView circleImageView;
    private EditText UserName;
    private RadioButton radioButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);
    }
}