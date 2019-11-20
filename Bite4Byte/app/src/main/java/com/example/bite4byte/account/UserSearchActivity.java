package com.example.bite4byte.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.PostActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

public class UserSearchActivity extends AppCompatActivity {

    Data manageData;
    String username;
    JSONObject userAccount;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manageData = (Data) getIntent().getSerializableExtra("manageData");
        username = (String) getIntent().getStringExtra("username");
        userAccount = manageData.getAccount(username);

        setContentView(R.layout.activity_profile_search);
    }

    public void updateFeed(Set<JSONObject> set) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.profile_container);
        parent.removeAllViews();

        for (JSONObject jo : set) {
            view = LayoutInflater.from(this).inflate(R.layout.user, parent, false);
            parent.addView(view);

            view.setTag(jo.get("username"));

            TextView un = view.findViewById(R.id.username);
            un.setText((String) jo.get("username"));

            TextView fullName = view.findViewById(R.id.user_full_name);
            fullName.setText((String) jo.get("firstname") + " " + (String) jo.get("lastname"));

            TextView restrictions = view.findViewById(R.id.user_restrictions);
            String restricts = "";
            for (Object r : (JSONArray) jo.get("restrictions")) {
                if (!restricts.isEmpty()) {
                    restricts = restricts + ", " + ((JSONObject) r).toString();
                } else {
                    restricts = restricts + ((JSONObject) r).toString();
                }
            }
            restrictions.setText(restricts);

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserSearchActivity.this, OtherUserProfileActivity.class);

                    intent.putExtra("manageData", manageData);
                    intent.putExtra("user", username);
                    intent.putExtra("otherUser", ((TextView)v.findViewById(R.id.username)).getText().toString());

                    startActivity(intent);
                }
            });
        }
    }

    public void onProfileSearchButtonClick(View view) {
        EditText searchQuery = (EditText)findViewById(R.id.profile_search_text);
        String query = searchQuery.getText().toString().trim();

        Intent intent = new Intent(this, UserSearchActivity.class);
        intent.putExtra("manageData", manageData);
        intent.putExtra("username", username);
        intent.putExtra("query", query);
        startActivity(intent);
    }

}
