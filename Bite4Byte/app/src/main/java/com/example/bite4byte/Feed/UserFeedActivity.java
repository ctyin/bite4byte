package com.example.bite4byte.Feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bite4byte.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class UserFeedActivity extends Activity {
    private View view;
    int i;

    public String loadJsonFromAsset() {
        // method taken in part from https://preview.tinyurl.com/yxzhv883
        String json = null;
        try {
            InputStream is = this.getAssets().open("yourfilename.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            JSONObject obj = new JSONObject(loadJsonFromAsset());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_user_feed);
        ViewGroup parent = (ViewGroup) findViewById(R.id.post_container);

        for (i=0; i<4; i++) {
            view = LayoutInflater.from(this).inflate(R.layout.post, parent, false);
            parent.addView(view);

            view.setTag(i);

            ImageView iv = view.findViewById(R.id.imageView);
            iv.setImageResource(R.drawable.chicken_curry);

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserFeedActivity.this, PostActivity.class);
                    intent.putExtra("postId", v.getTag().toString());
                    startActivity(intent);
                }
            });
        }
    }
}
