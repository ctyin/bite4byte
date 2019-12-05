package com.example.bite4byte.Feed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Messaging.AllMsgActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.FoodContents;
import com.example.bite4byte.Retrofit.GroupContents;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.example.bite4byte.account.UserProfileActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GroupActivity extends AppCompatActivity {
    private View view;
    private UserContents user;
    private String groupName;
    private Set<FoodContents> feed = new HashSet<FoodContents>();
    private Set<Integer> selectedCuisines = new HashSet<Integer>();
    private Set<String> allergies = new HashSet<String>(), restrictions = new HashSet<String>();
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
        groupName = getIntent().getStringExtra("groupName");

        setContentView(R.layout.activity_group);
        feed.clear();

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

        Call<GroupContents> call = iMyService.getGroup(groupName);
        call.enqueue(new Callback<GroupContents>() {
            @Override
            public void onResponse(Call<GroupContents> call, Response<GroupContents> response) {
                for (String foodId : response.body().getPosts()) {
                    Call<FoodContents> food = iMyService.getOneFood(foodId);
                    food.enqueue(new Callback<FoodContents>() {
                        @Override
                        public void onResponse(Call<FoodContents> call, Response<FoodContents> response) {
                            System.out.println(response.body().getFoodName());
                            feed.add(response.body());

                            updateGroupFeed(filter(feed));
                        }

                        @Override
                        public void onFailure(Call<FoodContents> call, Throwable t) {
                            Toast.makeText(GroupActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GroupContents> call, Throwable t) {
                Toast.makeText(GroupActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void updateGroupFeed(Set<FoodContents> set) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.group_post_container);
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
                    Intent intent = new Intent(GroupActivity.this, PostActivity.class);

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

    public Set<FoodContents> filter(Set<FoodContents> set) {
        Set<FoodContents> results = set;
        Set<FoodContents> toRemove = new HashSet<FoodContents>();

        for (FoodContents item : results) {
            String seller = item.getSellerUserName();

            if (seller.equals(user.getUsername())) {
                toRemove.add(item);
            }
        }

        results.removeAll(toRemove);
        toRemove.clear();

        for (FoodContents item : results) {
            if (!item.isAvailable()) {
                toRemove.add(item);
            }
        }

        results.removeAll(toRemove);
        toRemove.clear();

        for (FoodContents item : results) {
            String[] ingreds = item.getIngredients();
            for (String s : ingreds) {
                String ingredient = s.trim();

                for (String a : allergies) {
                    if (ingredient.equals(a)) {
                        toRemove.add(item);
                        break;
                    }
                }
            }
        }

        results.removeAll(toRemove);
        toRemove.clear();

        if (!restrictions.isEmpty()) {
            for (FoodContents item : results) {
                String[] restricts = item.getRestrictions();
                for (String r : restricts) {

                    for (String res : restrictions) {
                        if (r.equals(res)) {
                            toRemove.add(item);
                            break;
                        }
                    }
                }
            }

            results = toRemove;
            toRemove.clear();
        }


        return results;
    }

    public void onUploadToGroupClick(View view) {
        try {
            Intent i = new Intent(this, UploadToGroupActivity.class);
            i.putExtra("user", user);
            i.putExtra("groupName", groupName);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onFeedBtnClick(View view) {
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

    public void onGroupsClick(View view) {
        Intent i = new Intent(this, GroupListActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}
