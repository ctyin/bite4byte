package com.example.bite4byte.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.PostActivity;
import com.example.bite4byte.Feed.UploadItemActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.MainActivity;
import com.example.bite4byte.Messaging.AllMsgActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserProfileActivity extends AppCompatActivity {

    Data manageData;
    UserContents user;
    JSONObject userAccount;
    IMyService iMyService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        user = (UserContents) getIntent().getSerializableExtra("user");

        setContentView(R.layout.activity_user_profile);

        ((TextView) findViewById(R.id.usernameText)).setText(user.getUsername());
        ((TextView) findViewById(R.id.firstnameText)).setText(user.getFirstName());
        ((TextView) findViewById(R.id.lastnameText)).setText(user.getLastName());

        String restricts = "", allers = "";
        String[] restrictions = user.getRestrictions();
        String[] allergies = user.getAllergies();
        if (restrictions != null) {
            for (String j : restrictions) {
                restricts += j + " ";
            }
        }
        if (allergies != null) {
            for (String j : allergies) {
                allers += j + " ";
            }
        }

        String [] orderIds = user.getOrders();
        String orderStr = "";
        if (orderIds != null) {
            for (String j : orderIds) {
                orderStr += j + " ";
            }
        }

        ((TextView) findViewById(R.id.restrictionsText)).setText(restricts);
        ((TextView) findViewById(R.id.allergiesText)).setText(allers);
        ((TextView) findViewById(R.id.pastOrders)).setText(orderStr);
    }

    public void onProfileSearchButtonClick(View view) {
        EditText searchQuery = (EditText)findViewById(R.id.profile_search_text);
        String query = searchQuery.getText().toString().trim();

        Intent intent = new Intent(this, UserSearchActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    public void onEditAccountButtonClick(View view) {
        Intent intent = new Intent(this, EditAccountActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onDeleteAccountButtonClick(View view) {
        Call<UserContents> call = iMyService.deleteAccount(user.getUsername());

        call.enqueue(new Callback<UserContents>() {
            @Override
            public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<UserContents> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLogOutButtonClick(View view) {
        //manageData.eraseLoggedInUser(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onFeedButtonClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onPostButtonClick(View view) {
        Intent intent = new Intent(this, UploadItemActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onDMClick(View view) {
        try {
            Intent i = new Intent(this, AllMsgActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
