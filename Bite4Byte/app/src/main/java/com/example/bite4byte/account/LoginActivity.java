package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.MainActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onLoginBackClick(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void onLoginSubmitClick(View view) {
        compositeDisposable.add(iMyService.loginUser())
    }

}
