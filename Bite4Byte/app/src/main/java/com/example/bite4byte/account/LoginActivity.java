package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.MainActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.UploadItemActivity;

import org.json.simple.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    Data manageData;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manageData = (Data) getIntent().getSerializableExtra("manageData");

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onLoginSubmitClick(View view) {
        EditText usernameView = (EditText) findViewById(R.id.login_username);
        String username = usernameView.getText().toString();

        EditText passView = (EditText) findViewById(R.id.login_password);
        String pass = passView.getText().toString();

        /*compositeDisposable.add(iMyService.loginUser(username, pass)
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
                }));*/

        //get current account - will return null if invalid username or password
        JSONObject currentAccount = manageData.login(username, pass);
        if (currentAccount == null) {
            System.out.println("Invalid Username/Password");
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
        } else {
            System.out.println("Welcome" + " " + (String) currentAccount.get("firstname"));
            Toast.makeText(this, "Welcome" + " " + (String) currentAccount.get("firstname") + "!", Toast.LENGTH_LONG).show();
            try {
                Intent i = new Intent(this, UploadItemActivity.class);
                i.putExtra("manageData", manageData);
                startActivity(i);
            } catch (Exception e) {
                System.out.println(e);
            }
        }



    }

}
