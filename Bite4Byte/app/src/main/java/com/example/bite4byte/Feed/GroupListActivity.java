package com.example.bite4byte.Feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.Messaging.AllMsgActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.GroupContents;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.example.bite4byte.account.UserProfileActivity;

import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupListActivity extends AppCompatActivity {
    Data manageData;
    UserContents user;
    JSONObject userAccount;
    View view;
    List<String> res = new LinkedList<String>();

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

        setContentView(R.layout.activity_group_list);

        if (user.getGroupNames() != null) {
            for (String group : user.getGroupNames()) {
                Call<GroupContents> call = iMyService.getGroup(group);

                call.enqueue(new Callback<GroupContents>() {
                    @Override
                    public void onResponse(Call<GroupContents> call, Response<GroupContents> response) {
                        res.add(response.body().getGroupName());

                        updateGroupList(res);
                    }

                    @Override
                    public void onFailure(Call<GroupContents> call, Throwable t) {
                        Toast.makeText(GroupListActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void updateGroupList(List<String> results) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.group_list);
        parent.removeAllViews();

        for (String group : results) {
            view = LayoutInflater.from(this).inflate(R.layout.group, parent, false);
            parent.addView(view);

            view.setTag(group);

            TextView un = view.findViewById(R.id.group_name);
            un.setText(group);

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupListActivity.this, GroupActivity.class);

                    //intent.putExtra("manageData", manageData);
                    intent.putExtra("user", user);
                    intent.putExtra("groupName", ((TextView)v.findViewById(R.id.group_name)).getText().toString());

                    startActivity(intent);
                }
            });
        }
    }

    public void onNewGroupClick(View view) {
        Intent i = new Intent(this, CreateGroupActivity.class);
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
