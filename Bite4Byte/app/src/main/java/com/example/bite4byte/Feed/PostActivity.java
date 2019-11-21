package com.example.bite4byte.Feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.FoodContents;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.example.bite4byte.account.UserProfileActivity;

import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostActivity extends Activity {

    private UserContents user;
    private String order_id;
    IMyService iMyService;
    FoodContents food;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        setContentView(R.layout.post_activity);

        Intent i = getIntent();
        user = (UserContents) i.getSerializableExtra("user");
        order_id = i.getStringExtra("id");

        Call<FoodContents> call = iMyService.getOneFood(order_id);
        call.enqueue(new Callback<FoodContents>() {
            @Override
            public void onResponse(Call<FoodContents> call, Response<FoodContents> response) {
                food = response.body();
            }

            @Override
            public void onFailure(Call<FoodContents> call, Throwable t) {
                Toast.makeText(PostActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView iv = findViewById(R.id.postImg);
        if (food.getPicturePath() != null && !food.getPicturePath().isEmpty()) {
            Bitmap bm = BitmapFactory.decodeFile(food.getPicturePath());
            iv.setImageBitmap(bm);
        }

        TextView fName = findViewById(R.id.postTitleActivity);
        fName.setText(food.getFoodName());

        TextView sellUser = findViewById(R.id.sellerActivity);
        sellUser.setText(food.getSellerUserName());

        TextView desc = findViewById(R.id.postDescActivity);
        desc.setText(food.getDescription());
    }
/*
    public void onOrderBtnClick(View v) {
        // set the current food's availability to false
        food.setIsAvailable();

        md.addToAccountOrders(username, order_id, this);

        Toast.makeText(this, "Your order is confirmed!", Toast.LENGTH_LONG).show();
    }*/

    public void onFeedBtnClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }



}
