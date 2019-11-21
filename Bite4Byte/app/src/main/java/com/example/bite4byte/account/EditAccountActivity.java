package com.example.bite4byte.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditAccountActivity extends AppCompatActivity {

    //CompositeDisposable compositeDisposable = new CompositeDisposable();
    //IMyService iMyService;
    HashSet<String> restrictions = new HashSet<String>();
    HashSet<String> allergies = new HashSet<String>();
    String username;
    String firstname;
    String lastname;
    String password;
    JSONArray orders;
    Data manageData;
    IMyService iMyService;

    /*@Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manageData = (Data) getIntent().getSerializableExtra("manageData");
        username = getIntent().getStringExtra("username");
        JSONObject acct = manageData.getAccount(username);
        firstname = (String) acct.get("firstname");
        lastname = (String) acct.get("lastname");
        password = (String) acct.get("password");
        orders = (JSONArray) acct.get("orders");
        setContentView(R.layout.activity_create_acc_preferences);

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onCreatePrefSubmitClick(View view) {
        CheckBox pescatarianCheck = (CheckBox) findViewById(R.id.pescatarian_check);
        if (pescatarianCheck.isChecked()) {
            restrictions.add("pescatarian");
        }

        CheckBox vegetarianCheck = (CheckBox) findViewById(R.id.vegetarian_check);
        if (vegetarianCheck.isChecked()) {
            restrictions.add("vegetarian");
        }

        CheckBox veganCheck = (CheckBox) findViewById(R.id.vegan_check);
        if (veganCheck.isChecked()) {
            restrictions.add("vegan");
        }

        CheckBox peanutCheck = (CheckBox) findViewById(R.id.peanut_allergy);
        if (peanutCheck.isChecked()) {
            allergies.add("peanut");
        }

        CheckBox milkCheck = (CheckBox) findViewById(R.id.milk_allergy);
        if (milkCheck.isChecked()) {
            allergies.add("milk");
        }

        CheckBox glutenCheck = (CheckBox) findViewById(R.id.gluten_allergy);
        if (glutenCheck.isChecked()) {
            allergies.add("peanut");
        }

        CheckBox eggCheck = (CheckBox) findViewById(R.id.egg_allergy);
        if (eggCheck.isChecked()) {
            allergies.add("egg");
        }

        CheckBox wheatCheck = (CheckBox) findViewById(R.id.wheat_allergy);
        if (wheatCheck.isChecked()) {
            allergies.add("wheat");
        }

        CheckBox soyCheck = (CheckBox) findViewById(R.id.soy_allergy);
        if (soyCheck.isChecked()) {
            allergies.add("soy");
        }

        CheckBox fishCheck = (CheckBox) findViewById(R.id.fish_allergy);
        if (fishCheck.isChecked()) {
            allergies.add("fish");
        }

        CheckBox shellfishCheck = (CheckBox) findViewById(R.id.shellfish_allergy);
        if (shellfishCheck.isChecked()) {
            allergies.add("shellfish");
        }

        //have to implement the rest of the checkboxes.

        String[] restrictArr = new String[restrictions.size()];
        int i = 0;
        for (String s : restrictions) {
            restrictArr[i] = s;
            i++;
        }

        String[] allergyArr = new String[allergies.size()];
        i = 0;
        for (String s : allergies) {
            allergyArr[i] = s;
            i++;
        }

        Call<UserContents> call = iMyService.editAccount(username, restrictArr, allergyArr);

        call.enqueue(new Callback<UserContents>() {
            @Override
            public void onResponse(Call<UserContents> call, Response<UserContents> response) {
                UserContents user = response.body();
                String s = "Welcome " + response.body().getFirstName() + "!";
                System.out.println(s);

                Toast.makeText(EditAccountActivity.this, s, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(EditAccountActivity.this, UserProfileActivity.class);
                intent.putExtra("manageData", manageData);
                intent.putExtra("user", username);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<UserContents> call, Throwable t) {
                Toast.makeText(EditAccountActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });





        //manageData.modifyAccount(this, username, firstname, lastname, password, restrictArr, allergyArr, orders);




    }
}
