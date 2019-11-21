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
import android.widget.Toast;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.FoodContents;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.example.bite4byte.account.CreateAccPreferencesActivity;
import com.example.bite4byte.account.CreateAccountActivity;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserFeedActivity extends Activity {
    private View view;
    private UserContents user;
    private Set<FoodContents> feed = new HashSet<FoodContents>();
    private Set<Integer> selectedCuisines = new HashSet<Integer>();
    private Set<String> cuisines = new HashSet<String>(), allergies = new HashSet<String>(),
            restrictions = new HashSet<String>();
    private String[] cuisinesFilter;
    private boolean[] cuisinesSelect;
    IMyService iMyService;

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
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        user = (UserContents) getIntent().getSerializableExtra("user");
        setContentView(R.layout.activity_user_feed);

        cuisinesFilter = getResources().getStringArray(R.array.cuisines);
        cuisinesSelect = new boolean[cuisinesFilter.length];
        feed.clear();

        Call<List<FoodContents>> call = iMyService.getFoods();
        call.enqueue(new Callback<List<FoodContents>>() {
            @Override
            public void onResponse(Call<List<FoodContents>> call, Response<List<FoodContents>> response) {
                List<FoodContents> food = response.body();
                if (food != null) {
                    for (FoodContents f : food) {
                        feed.add(f);
                        System.out.println(f.getFoodName());
                    }
                }

                String[] allergiesArr = user.getAllergies();
                if (allergiesArr != null) {
                    for (int i = 0; i < allergiesArr.length; i++) {
                        allergies.add(allergiesArr[i]);
                    }
                }

                String[] restrictsArr = user.getRestrictions();
                if (restrictsArr != null) {
                    for (int i = 0; i < restrictsArr.length; i++) {
                        restrictions.add(restrictsArr[i]);
                    }
                }

                Set<FoodContents> newFeed = filterByParam(feed);

                updateFeed(newFeed);
            }

            @Override
            public void onFailure(Call<List<FoodContents>> call, Throwable t) {
                Toast.makeText(UserFeedActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateFeed(Set<FoodContents> set) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.post_container);
        parent.removeAllViews();

        for (FoodContents f : set) {
            System.out.println(f.getFoodName());
        }

        for (FoodContents food : set) {
            view = LayoutInflater.from(this).inflate(R.layout.post, parent, false);
            parent.addView(view);

            view.setTag(food.getID());

            TextView title = view.findViewById(R.id.postTitle);
            title.setText(food.getFoodName());

            TextView seller = view.findViewById(R.id.seller);
            seller.setText(food.getSellerUserName());

            TextView desc = view.findViewById(R.id.description);
            desc.setText(food.getDescription());

            ImageView iv = view.findViewById(R.id.imageView);
            if (!food.getPicturePath().isEmpty()) {
                Bitmap bm = BitmapFactory.decodeFile(food.getPicturePath());
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
                    intent.putExtra("user", user);

                    startActivity(intent);
                }
            });
        }
    }

    public Set<FoodContents> filterByParam(Set<FoodContents> set) {
        Set<FoodContents> results = set;
        Set<FoodContents> usersPost = new HashSet<FoodContents>();
        Set<FoodContents> containAllergy = new HashSet<FoodContents>();
        Set<FoodContents> meetRestrict = new HashSet<FoodContents>();
        Set<FoodContents> inCuisine = new HashSet<FoodContents>();

        for (FoodContents item : results) {
            String seller = item.getSellerUserName();

            if (seller.equals(user.getUsername())) {
                usersPost.add(item);
            }
        }

        results.removeAll(usersPost);

        for (FoodContents item : results) {
            if (!item.isAvailable()) {
                results.remove(item);
            }
        }

        for (FoodContents item : results) {
            String[] ingreds = item.getIngredients();
            for (String s : ingreds) {
                String ingredient = s.trim();

                for (String a : allergies) {
                    if (ingredient.equals(a)) {
                        containAllergy.add(item);
                        break;
                    }
                }
            }
        }

        results.removeAll(containAllergy);

        if (!restrictions.isEmpty()) {
            for (FoodContents item : results) {
                String[] restricts = item.getRestrictions();
                for (String r : restricts) {

                    for (String res : restrictions) {
                        if (r.equals(res)) {
                            meetRestrict.add(item);
                            break;
                        }
                    }
                }
            }

            results = meetRestrict;
        }

        if (!cuisines.isEmpty()) {
            for (FoodContents item : results) {
                String[] cuises = item.getCuisines();
                for (String c : cuises) {
                    String cuisine = c;

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

                //feed.clear();
                Set<FoodContents> newFeed = filterByParam(feed);

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

                //feed.clear();
                Set<FoodContents> newFeed = filterByParam(feed);

                updateFeed(newFeed);
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }
}
