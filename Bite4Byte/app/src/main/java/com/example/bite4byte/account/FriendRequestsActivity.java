package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.UploadItemActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.Messaging.AllMsgActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FriendRequestsActivity extends AppCompatActivity {
    Data manageData;
    UserContents user;
    JSONObject userAccount;
    View view;
    List<String[]> res = new LinkedList<String[]>();
    String otherUsername;

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

        setContentView(R.layout.activity_friend_requests);

        for (String friend : user.getFriendRequests()) {
            Call<UserContents> call = iMyService.getAccount(friend);

            call.enqueue(new Callback<UserContents>() {
                @Override
                public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                    String[] results = new String[3];

                    results[0] = response.body().getUsername();
                    results[1] = response.body().getFirstName();
                    results[2] = response.body().getLastName();

                    res.add(results);

                    updateFriendRequests(res);

                /*Intent intent = new Intent(UserSearchActivity.this, UserFeedActivity.class);
                intent.putExtra("manageData", manageData);
                intent.putExtra("user", username);
                startActivity(intent);*/
                }

                @Override
                public void onFailure(Call<UserContents> call, Throwable t) {
                    Toast.makeText(FriendRequestsActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateFriendRequests(List<String[]> results) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.friend_request_list);
        parent.removeAllViews();

        for (String[] arr : results) {
            view = LayoutInflater.from(this).inflate(R.layout.friend_request, parent, false);
            parent.addView(view);

            view.setTag(arr[0]);

            TextView un = view.findViewById(R.id.username);
            un.setText(arr[0]);
            otherUsername = arr[0];

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
                    Call<UserContents> call = iMyService.acceptFriend(user.getUsername(), otherUsername);

                    call.enqueue(new Callback<UserContents>() {
                        @Override
                        public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                            Intent intent = new Intent(FriendRequestsActivity.this, FriendRequestsActivity.class);
                            intent.putExtra("user", response.body());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<UserContents> call, Throwable t) {
                            Toast.makeText(FriendRequestsActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            view.findViewById(R.id.decline).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Call<UserContents> call = iMyService.declineFriend(user.getUsername(), otherUsername);

                    call.enqueue(new Callback<UserContents>() {
                        @Override
                        public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                            Intent intent = new Intent(FriendRequestsActivity.this, FriendRequestsActivity.class);
                            intent.putExtra("user", response.body());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<UserContents> call, Throwable t) {
                            Toast.makeText(FriendRequestsActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    public void onFriendsButtonClick(View view) {
        Intent i = new Intent(this, FriendsListActivity.class);
        i.putExtra("user", user);
        startActivity(i);
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
