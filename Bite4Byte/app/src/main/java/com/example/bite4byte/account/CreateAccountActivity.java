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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CreateAccountActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_create_acc);

        manageData = (Data) getIntent().getSerializableExtra("manageData");

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onCreateAccSubmitClick(View view) {
        EditText usernameView = (EditText) findViewById(R.id.create_acc_username);
        String username = usernameView.getText().toString();

        EditText firstnameView = (EditText) findViewById(R.id.create_acc_firstname);
        String firstname = firstnameView.getText().toString();
        firstname = firstname.substring(0, 1).toUpperCase() + firstname.substring(1).toLowerCase();

        EditText lastnameView = (EditText) findViewById(R.id.create_acc_lastname);
        String lastname = lastnameView.getText().toString();
        lastname = lastname.substring(0, 1).toUpperCase() + lastname.substring(1).toLowerCase();

        EditText passView = (EditText) findViewById(R.id.create_acc_password);
        String password = passView.getText().toString();

        EditText passConfirmView = (EditText) findViewById(R.id.create_acc_password_confirm);
        String pwConfirm = passConfirmView.getText().toString();

        if (!password.equals(pwConfirm)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            return;
        }

        /*
        compositeDisposable.add(iMyService.registerUser(username, firstname, lastname, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(
                                CreateAccountActivity.this,
                                ""+response,
                                Toast.LENGTH_SHORT).show();
                    }
                }));
         */

        if (!manageData.verifyAvailableUsername(username)) {
            System.out.println("Username taken");
            Toast.makeText(
                    CreateAccountActivity.this,
                    "Username already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(this, CreateAccPreferencesActivity.class);
        i.putExtra("username", username);
        i.putExtra("firstname", firstname);
        i.putExtra("lastname", lastname);
        i.putExtra("password", password);
        i.putExtra("manageData", manageData);
        startActivity(i);
    }

}