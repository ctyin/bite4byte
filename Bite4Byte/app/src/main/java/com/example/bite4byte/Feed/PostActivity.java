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
import com.example.bite4byte.account.UserProfileActivity;

import org.json.simple.JSONObject;

public class PostActivity extends Activity {

    private Data md;
    private String username;
    private String order_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_activity);

        Intent i = getIntent();
        md = (Data) i.getSerializableExtra("manageData");
        username = i.getStringExtra("username");
        order_id = i.getStringExtra("id");

        ImageView iv = findViewById(R.id.postImg);
        JSONObject pic = md.getFoodItems().get(Integer.parseInt(order_id));
        if (pic.get("picture") != null && !pic.get("picture").toString().isEmpty()) {
            Bitmap bm = BitmapFactory.decodeFile(pic.get("picture").toString());
            iv.setImageBitmap(bm);
        }

        TextView fName = findViewById(R.id.postTitleActivity);
        fName.setText(i.getStringExtra("foodName"));

        TextView sellUser = findViewById(R.id.sellerActivity);
        sellUser.setText(i.getStringExtra("sellerUserName"));

        TextView desc = findViewById(R.id.postDescActivity);
        desc.setText(i.getStringExtra("description"));
    }

    public void onOrderBtnClick(View v) {
        // set the current food's availability to false
        md.setFoodAvailability(Integer.parseInt(getIntent().getStringExtra("id")), false);

        md.addToAccountOrders(username, order_id, this);

        Toast.makeText(this, "Your order is confirmed!", Toast.LENGTH_LONG).show();
    }

    public void onFeedBtnClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("manageData", md);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void onProfileButtonClick(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("manageData", md);
        intent.putExtra("user", username);
        startActivity(intent);
    }



}
