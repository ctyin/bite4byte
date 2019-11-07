package com.example.bite4byte.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.MainActivity;
import com.example.bite4byte.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UserProfileActivity extends AppCompatActivity {

    Data manageData;
    String username;
    JSONObject userAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manageData = (Data) getIntent().getSerializableExtra("manageData");
        username = (String) getIntent().getStringExtra("user");
        userAccount = manageData.getAccount(username);

        setContentView(R.layout.activity_user_profile);

        ((TextView) findViewById(R.id.usernameText)).setText(username);
        ((TextView) findViewById(R.id.firstnameText)).setText((String) userAccount.get("firstname"));
        ((TextView) findViewById(R.id.lastnameText)).setText((String) userAccount.get("lastname"));

        String restricts = "";
        String allers = "";
        JSONArray restrictions = (JSONArray) userAccount.get("restrictions");
        JSONArray allergies = (JSONArray) userAccount.get("allergies");
        if (restrictions != null) {
            for (Object j : restrictions) {
                restricts += j.toString() + ", ";
            }
        }
        if (allergies != null) {
            for (Object j : allergies) {
                allers += j.toString() + ", ";
            }
        }


        ((TextView) findViewById(R.id.restrictionsText)).setText(restricts);
        ((TextView) findViewById(R.id.allergiesText)).setText(allers);
    }

    public void onEditAccountButtonClick(View view) {

    }

    public void onDeleteAccountButtonClick(View view) {
        manageData.deleteAccount(username, this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
