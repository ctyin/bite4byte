package com.example.bite4byte.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.PostActivity;
import com.example.bite4byte.Feed.UploadItemActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.MainActivity;
import com.example.bite4byte.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class OtherUserProfileActivity extends AppCompatActivity {

    Data manageData;
    String currUsername, otherUsername;
    JSONObject userAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manageData = (Data) getIntent().getSerializableExtra("manageData");
        currUsername = (String) getIntent().getStringExtra("user");
        otherUsername = (String) getIntent().getStringExtra("otherUser");
        userAccount = manageData.getAccount(otherUsername);
        Map<Integer, JSONObject> foodMap = manageData.getFoodItems();

        setContentView(R.layout.activity_other_user_profile);

        ((TextView) findViewById(R.id.usernameText)).setText(otherUsername);
        ((TextView) findViewById(R.id.firstnameText)).setText((String) userAccount.get("firstname"));
        ((TextView) findViewById(R.id.lastnameText)).setText((String) userAccount.get("lastname"));

        String restricts = "";
        String allers = "";
        JSONArray restrictions = (JSONArray) userAccount.get("restrictions");
        JSONArray allergies = (JSONArray) userAccount.get("allergies");
        if (restrictions != null) {
            for (Object j : restrictions) {
                restricts += j.toString() + " ";
            }
        }
        if (allergies != null) {
            for (Object j : allergies) {
                allers += j.toString() + " ";
            }
        }

        JSONArray orderIds = (JSONArray) userAccount.get("orders");
        String orderStr = "";
        if (orderIds != null) {
            for (Object j : orderIds) {
                orderStr += foodMap.get(Integer.parseInt((String)j)).get("foodName") + "\n";
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
        intent.putExtra("manageData", manageData);
        intent.putExtra("username", currUsername);
        intent.putExtra("query", query);
        startActivity(intent);
    }

    public void onFriendRequestButtonClick(View view) {

    }

    public void onFeedButtonClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("manageData", manageData);
        intent.putExtra("user", currUsername);
        startActivity(intent);
    }

    public void onPostButtonClick(View view) {
        Intent intent = new Intent(this, UploadItemActivity.class);
        intent.putExtra("manageData", manageData);
        intent.putExtra("username", currUsername);
        startActivity(intent);
    }

}

