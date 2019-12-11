package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Feed.UploadItemActivity;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

//    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onLoginSubmitClick(View view) {
        EditText usernameView = (EditText) findViewById(R.id.login_username);
        String username = usernameView.getText().toString();

        EditText passView = (EditText) findViewById(R.id.login_password);
        String pass = passView.getText().toString();

        Call<UserContents> call = iMyService.loginUser(username, pass);

        call.enqueue(new Callback<UserContents>() {
            @Override
            public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                if (response.body().getUsername() == null) {
                    Toast.makeText(LoginActivity.this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
                } else if (response.body().isBanned()) {
                    Toast.makeText(LoginActivity.this, "You have been banned, please contact the administrators", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Welcome" + " " + response.body().getUsername() + "!", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(LoginActivity.this, UserFeedActivity.class);
                    i.putExtra("user", response.body());

                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<UserContents> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        //get current account - will return null if invalid username or password
        //JSONObject currentAccount = manageData.login(username, pass);
        /*
        if (currentAccount == null) {
            System.out.println("Invalid Username/Password");
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_LONG).show();
        } else {
            System.out.println("Welcome" + " " + (String) currentAccount.get("firstname"));

            try {

                //manageData.writeLoggedInUser(this, (String) currentAccount.get("username"), (String) currentAccount.get("firstname"), (String) currentAccount.get("lastname"),
                        (String) currentAccount.get("password"), (String) currentAccount.get())
                Intent i = new Intent(this, UserFeedActivity.class);
                i.putExtra("manageData", manageData);
                i.putExtra("user", username);
                startActivity(i);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        */
    }

}
