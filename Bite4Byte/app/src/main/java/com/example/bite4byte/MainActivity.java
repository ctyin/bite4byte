package com.example.bite4byte;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.account.CreateAccountActivity;
import com.example.bite4byte.account.LoginActivity;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public static final int LoginActivityActivity_ID = 1;
    public static final int CreateAccountActivity_ID = 2;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    Data manageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manageData = new Data(this);
    }

    public void onLoginClick(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("manageData", manageData);
        startActivityForResult(i, LoginActivityActivity_ID);
    }

    public void onCreateAccClick(View view) {
        Intent i = new Intent(this, CreateAccountActivity.class);
        i.putExtra("manageData", manageData);
        startActivityForResult(i, CreateAccountActivity_ID);
    }

}
