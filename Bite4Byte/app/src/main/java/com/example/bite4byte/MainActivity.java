package com.example.bite4byte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.account.CreateAccountActivity;
import com.example.bite4byte.account.LoginActivity;

public class MainActivity extends AppCompatActivity {
    public static final int LoginActivityActivity_ID = 1;
    public static final int CreateAccountActivity_ID = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onLoginClick(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LoginActivityActivity_ID);
    }

    public void onCreateAccClick(View view) {
        Intent i = new Intent(this, CreateAccountActivity.class);
        startActivityForResult(i, CreateAccountActivity_ID);
    }

}
