package com.example.bite4byte.Feed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.account.UserProfileActivity;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserFeedActivity extends Activity {
    private View view;
    private Data manageData;
    private JSONObject user;
    private String username;
    private Set<JSONObject> feed = new HashSet<JSONObject>();
    private Set<Integer> selectedCuisines = new HashSet<Integer>();
    private Set<String> cuisines = new HashSet<String>(), allergies = new HashSet<String>(),
            restrictions = new HashSet<String>();
    private String[] cuisinesFilter;
    private boolean[] cuisinesSelect;

    /*
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
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manageData = (Data) getIntent().getSerializableExtra("manageData");
        username = getIntent().getStringExtra("user");
        user = manageData.getAccount(username);
        setContentView(R.layout.activity_user_feed);

        cuisinesFilter = getResources().getStringArray(R.array.cuisines);
        cuisinesSelect = new boolean[cuisinesFilter.length];

        Map<Integer, JSONObject> foodItems = manageData.getFoodItems();
        for (int key : foodItems.keySet()) {
            feed.add(foodItems.get(key));
        }

        JSONArray allergiesJSON = (JSONArray) user.get("allergies");
        if (allergiesJSON != null) {
            for (Object j : allergiesJSON) {
                allergies.add(j.toString());
            }
        }

        JSONArray restrictsJSON = (JSONArray) user.get("restrictions");
        if (restrictsJSON != null) {
            for (Object j : restrictsJSON) {
                restrictions.add(j.toString());
            }
        }

        Set<JSONObject> newFeed = filterByParam();

        updateFeed(newFeed);
    }

    public void updateFeed(Set<JSONObject> set) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.post_container);
        parent.removeAllViews();

        for (JSONObject jo : set) {
            view = LayoutInflater.from(this).inflate(R.layout.post, parent, false);
            parent.addView(view);

            view.setTag(jo.get("_id"));

            TextView title = view.findViewById(R.id.postTitle);
            title.setText((String) jo.get("foodName"));

            TextView seller = view.findViewById(R.id.seller);
            seller.setText((String) jo.get("sellerUserName"));

            TextView desc = view.findViewById(R.id.description);
            desc.setText((String) jo.get("description"));

            ImageView iv = view.findViewById(R.id.imageView);
            if (!jo.get("picture").toString().isEmpty()) {
                Bitmap bm = BitmapFactory.decodeFile(jo.get("picture").toString());
                iv.setImageBitmap(bm);
            }

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent intent = new Intent(UserFeedActivity.this, PostActivity.class);

                TextView title = v.findViewById(R.id.postTitle);
                intent.putExtra("foodName", title.getText().toString());

                TextView seller = v.findViewById(R.id.seller);
                intent.putExtra("sellerUserName", seller.getText().toString());

                TextView desc = v.findViewById(R.id.description);
                intent.putExtra("description", desc.getText().toString());

                intent.putExtra("id", v.getTag().toString());
                intent.putExtra("username", username);

                Data md = (Data) UserFeedActivity.this.getIntent().getSerializableExtra("manageData");
                intent.putExtra("manageData", md);

                startActivity(intent);
                }
            });
        }
    }

    public Set<JSONObject> filterByParam() {
        Set<JSONObject> results = feed;
        Set<JSONObject> containAllergy = new HashSet<JSONObject>();
        Set<JSONObject> meetRestrict = new HashSet<JSONObject>();
        Set<JSONObject> inCuisine = new HashSet<JSONObject>();

        for (JSONObject item : results) {
            JSONArray ingredJSON = (JSONArray) item.get("ingredients");
            for (Object s : ingredJSON) {
                String ingredient = s.toString().trim();

                for (String a : allergies) {
                    if (ingredient.equals(a)) {
                        containAllergy.add(item);
                        break;
                    }
                }
            }
        }

        results.removeAll(containAllergy);

        for (JSONObject item : results) {
            JSONArray resJSON = (JSONArray) item.get("restrictions");
            for (Object r : resJSON) {
                String restriction = r.toString();

                for (String res : restrictions) {
                    if (restriction.equals(res)) {
                        meetRestrict.add(item);
                        break;
                    }
                }
            }
        }

        results = meetRestrict;

        if (!cuisines.isEmpty()) {
            for (JSONObject item : results) {
                JSONArray cuisJSON = (JSONArray) item.get("cuisines");
                for (Object c : cuisJSON) {
                    String cuisine = c.toString();

                    for (String cuis : cuisines) {
                        if (cuisine.equals(cuis)) {
                            inCuisine.add(item);
                            break;
                        }
                    }
                }
            }

            results = inCuisine;
        }

        return results;
    }

    public void onUploadClick(View view) {
        try {
            Intent i = new Intent(this, UploadItemActivity.class);
            i.putExtra("manageData", manageData);
            i.putExtra("username", username);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onProfileClick(View view) {
        try {
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("manageData", manageData);
            i.putExtra("user", (String) user.get("username"));
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onFilterClick(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserFeedActivity.this);
        mBuilder.setTitle("Filter by Cuisine");

        mBuilder.setMultiChoiceItems(cuisinesFilter, cuisinesSelect, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b) {
                    selectedCuisines.add(i);
                }
            }
        });

        mBuilder.setCancelable(false);

        mBuilder.setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                cuisines.clear();

                for (int c : selectedCuisines) {
                    cuisines.add(cuisinesFilter[c]);
                }

                Set<JSONObject> newFeed = filterByParam();
                updateFeed(newFeed);
            }
        });

        mBuilder.setNeutralButton("Clear all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < cuisinesSelect.length; j++) {
                    cuisinesSelect[j] = false;
                }

                selectedCuisines.clear();
                cuisines.clear();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
