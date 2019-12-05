package com.example.bite4byte.Feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.GroupContents;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreateGroupActivity extends AppCompatActivity{
    UserContents user;
    JSONObject userAccount;
    View view;
    List<String[]> res = new LinkedList<String[]>();

    Set<String> addedFriends;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        //manageData = (Data) getIntent().getSerializableExtra("manageData");
        user = (UserContents) getIntent().getSerializableExtra("user");
        //userAccount = manageData.getAccount(username);

        setContentView(R.layout.activity_create_group);

        addedFriends = new HashSet<String>();
        addedFriends.add(user.getUsername());

        for (String friend : user.getFriendsList()) {
            Call<UserContents> call = iMyService.getAccount(friend);

            call.enqueue(new Callback<UserContents>() {
                @Override
                public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                    String[] results = new String[3];

                    results[0] = response.body().getUsername();
                    results[1] = response.body().getFirstName();
                    results[2] = response.body().getLastName();


                    res.add(results);

                    addFriendsToGroup(res);

                /*Intent intent = new Intent(UserSearchActivity.this, UserFeedActivity.class);
                intent.putExtra("manageData", manageData);
                intent.putExtra("user", username);
                startActivity(intent);*/
                }

                @Override
                public void onFailure(Call<UserContents> call, Throwable t) {
                    Toast.makeText(CreateGroupActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void addFriendsToGroup(List<String[]> results) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.friends_list);
        parent.removeAllViews();

        for (String [] arr : results) {
            view = LayoutInflater.from(this).inflate(R.layout.group_add_friend, parent, false);
            parent.addView(view);

            view.setTag(arr[0]);

            TextView un = view.findViewById(R.id.username);
            un.setText(arr[0]);
            final String otherUsername = arr[0];

            TextView fullName = view.findViewById(R.id.user_full_name);
            fullName.setText(arr[1] + " " + arr[2]);

            /*TextView restrictions = view.findViewById(R.id.user_restrictions);
            String restricts = "";
            for (Object r : (JSONArray) jo.get("restrictions")) {
                if (!restricts.isEmpty()) {
                    restricts = restricts + ", " + ((JSONObject) r).toString();
                } else {
                    restricts = restricts + ((JSONObject) r).toString();
                }
            }
            restrictions.setText(restricts);*/

            // need to get the data from the post itself
            view.findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addedFriends.add(otherUsername);
                }
            });
        }
    }

    public void onCreateGroupSubmitButtonClick(View view) {
        String name = ((EditText) findViewById(R.id.create_group_name)).getText().toString();
        String [] users = new String[addedFriends.size()];
        int i = 0;
        for (String u : addedFriends) {
            users[i] = u;
            i++;
        }
        Call<UserContents> call = iMyService.createGroup(name, users, new String[] {}, user.getUsername());
        call.enqueue(new Callback<UserContents>() {
            @Override
            public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                if (response.body().getUsername() == null) {
                    Toast.makeText(CreateGroupActivity.this, "Group name exists already", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateGroupActivity.this, CreateGroupActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateGroupActivity.this, "Group successfully made", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateGroupActivity.this, GroupListActivity.class);
                    intent.putExtra("user", response.body());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserContents> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CreateGroupActivity.this, "Error thrown", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
