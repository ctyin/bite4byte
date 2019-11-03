package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.MainActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
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
        EditText nameView = (EditText) findViewById(R.id.login_username);
        String name = nameView.getText().toString();

        EditText passView = (EditText) findViewById(R.id.login_password);
        String pass = passView.getText().toString();

        compositeDisposable.add(iMyService.loginUser(name, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(
                                LoginActivity.this,
                                ""+response,
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
