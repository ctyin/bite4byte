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
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OtherUserProfileActivity extends AppCompatActivity {

    Data manageData;
    UserContents user;
    String otherUsername;
    //JSONObject userAccount;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //manageData = (Data) getIntent().getSerializableExtra("manageData");
        user = (UserContents) getIntent().getSerializableExtra("user");
        otherUsername = (String) getIntent().getStringExtra("otherUser");
        //userAccount = manageData.getAccount(otherUsername);


        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        //Map<Integer, JSONObject> foodMap = manageData.getFoodItems();

        setContentView(R.layout.activity_other_user_profile);

        Call<UserContents> call = iMyService.getAccount(otherUsername);

        call.enqueue(new Callback<UserContents>() {
            @Override
            public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                UserContents user = response.body();

                ((TextView) findViewById(R.id.usernameText)).setText(otherUsername);
                ((TextView) findViewById(R.id.firstnameText)).setText(user.getFirstName());
                ((TextView) findViewById(R.id.lastnameText)).setText(user.getLastName());

                String restricts = "";
                String allers = "";
                String [] restrictions = user.getRestrictions();
                String [] allergies= user.getAllergies();
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

                /*JSONArray orderIds = (JSONArray) userAccount.get("orders");
                String orderStr = "";
                if (orderIds != null) {
                    for (Object j : orderIds) {
                        orderStr += foodMap.get(Integer.parseInt((String)j)).get("foodName") + "\n";
                    }
                }*/

                ((TextView) findViewById(R.id.restrictionsText)).setText(restricts);
                ((TextView) findViewById(R.id.allergiesText)).setText(allers);
                //((TextView) findViewById(R.id.pastOrders)).setText(orderStr);



            }

            @Override
            public void onFailure(Call<UserContents> call, Throwable t) {
                Toast.makeText(OtherUserProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onProfileSearchButtonClick(View view) {
        EditText searchQuery = (EditText)findViewById(R.id.profile_search_text);
        String query = searchQuery.getText().toString().trim();

        Intent intent = new Intent(this, UserSearchActivity.class);
        //intent.putExtra("manageData", manageData);
        intent.putExtra("user", user);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    public void onFriendRequestButtonClick(View view) {

    }

    public void onPostButtonClick(View view) {
        Intent intent = new Intent(this, UploadItemActivity.class);
        //intent.putExtra("manageData", manageData);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onFeedButtonClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onUploadClick(View view) {
        try {
            Intent i = new Intent(this, UploadItemActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onProfileClick(View view) {
        try {
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("user", user);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}

