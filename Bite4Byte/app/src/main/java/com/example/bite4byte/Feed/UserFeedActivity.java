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

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserFeedActivity extends Activity {
    private View view;

    public String loadJsonFromAsset() {
        // method taken in part from https://preview.tinyurl.com/yxzhv883
        String json = null;
        try {
            InputStream is = this.getAssets().open("generatedFoodPosts.json");
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

    public List<JSONObject> filterByParam(List<String> fields,
                                          List<String> values, Iterator<JSONObject> posts) {
        ArrayList<JSONObject> ret = new ArrayList<>();

        while (posts.hasNext()) {
            JSONObject jo = (JSONObject) posts.next();
            ret.add(jo);
        }

        return ret;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONParser parser = new JSONParser();
        JSONArray posts = null;

        try {
            posts = (JSONArray) parser.parse(loadJsonFromAsset());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Iterator<JSONObject> iter = posts.iterator();
        List<JSONObject> result = filterByParam(null, null, iter);

        setContentView(R.layout.activity_user_feed);
        ViewGroup parent = (ViewGroup) findViewById(R.id.post_container);

        for (JSONObject jo : result) {
            view = LayoutInflater.from(this).inflate(R.layout.post, parent, false);
            parent.addView(view);

            TextView title = view.findViewById(R.id.postTitle);
            title.setText((String) jo.get("foodName"));

            TextView seller = view.findViewById(R.id.seller);
            seller.setText((String) jo.get("sellerUserName"));

            TextView desc = view.findViewById(R.id.description);
            desc.setText((String) jo.get("description"));

            ImageView iv = view.findViewById(R.id.imageView);
            iv.setImageResource(R.drawable.chicken_curry);

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(UserFeedActivity.this, PostActivity.class);

                TextView title = view.findViewById(R.id.postTitle);
                intent.putExtra("foodName", title.getText().toString());

                TextView seller = view.findViewById(R.id.seller);
                intent.putExtra("sellerUserName", seller.getText().toString());

                TextView desc = view.findViewById(R.id.description);
                intent.putExtra("description", desc.getText().toString());

                Data md = (Data) UserFeedActivity.this.getIntent().getSerializableExtra("manageData");
                intent.putExtra("manageData", md);

                startActivity(intent);
                }
            });
        }
    }

    public void onSearchClick() {

    }
}
