package com.example.bite4byte.account;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;

import java.util.HashSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CreateAccPreferencesActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    HashSet<String> preferences = new HashSet<String>();
    HashSet<String> allergies = new HashSet<String>();
    String username;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getIntent().getStringExtra("username");
        setContentView(R.layout.activity_create_acc_preferences);

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onCreatePrefSubmitClick(View view) {
        CheckBox pescatarianCheck = (CheckBox) findViewById(R.id.pescatarian_check);
        if (pescatarianCheck.isChecked()) {
            preferences.add("pescatarian");
        }

        CheckBox vegetarianCheck = (CheckBox) findViewById(R.id.vegetarian_check);
        if (vegetarianCheck.isChecked()) {
            preferences.add("vegetarian");
        }

        CheckBox veganCheck = (CheckBox) findViewById(R.id.vegan_check);
        if (veganCheck.isChecked()) {
            preferences.add("vegan");
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

        String[] preferenceArr = new String[preferences.size()];
        int i = 0;
        for (String s : preferences) {
            preferenceArr[i] = s;
            i++;
        }

        String[] allergyArr = new String[allergies.size()];
        i = 0;
        for (String s : allergies) {
            allergyArr[i] = s;
            i++;
        }

        compositeDisposable.add(iMyService.foodPref(username, preferenceArr, allergyArr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(
                                CreateAccPreferencesActivity.this,
                                ""+response,
                                Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
