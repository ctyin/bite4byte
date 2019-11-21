package com.example.bite4byte;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.account.CreateAccountActivity;
import com.example.bite4byte.account.LoginActivity;

import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public static final int LoginActivityActivity_ID = 1;
    public static final int CreateAccountActivity_ID = 2;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    Data manageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        create("accounts.json", "[]"); //Overwrite File and make it blank
//        create("foods.json", "[]");
//        create("loggedin.json", "{}");

        boolean isAccountsFilePresent = isFilePresent("accounts.json");
        boolean isFoodItemsFilePresent = isFilePresent("foods.json");
        boolean isLoggedInFilePresent = isFilePresent("loggedin.json");
        if(!isAccountsFilePresent) {
            boolean isFileCreated = create("accounts.json", "[]");
            if(!isFileCreated) {
                System.out.println("Failed File Creation");
            } else {
                System.out.println("File Created");
            }
        } else {
            System.out.println("File exists");
        }

        if(!isFoodItemsFilePresent) {
            boolean isFileCreated = create("foods.json", "[]");
            if(!isFileCreated) {
                System.out.println("Failed File Creation");
            } else {
                System.out.println("File Created");
            }
        } else {
            System.out.println("File exists");
        }

        if(!isLoggedInFilePresent) {
            boolean isFileCreated = create("loggedin.json", "{}");
            if(!isFileCreated) {
                System.out.println("Failed File Creation");
            } else {
                System.out.println("File Created");
            }
        } else {
            System.out.println("File exists");
        }


        manageData = new Data(this);
        /*if (manageData.userLoggedIn()) {
            JSONObject currentUser = manageData.getLoggedInUser();
            Intent i = new Intent(this, UserFeedActivity.class);
            i.putExtra("manageData", manageData);
            i.putExtra("user", (String) currentUser.get("username"));
            startActivity(i);
        }*/


    }

    public boolean isFilePresent(String fileName) {
        String path = this.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    private boolean create(String fileName, String jsonString){
        String FILENAME = "accounts.json";
        try {
            FileOutputStream fos = this.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public void onLoginClick(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("manageData", manageData);
        startActivityForResult(i, LoginActivityActivity_ID);
    }

    public void onCreateAccClick(View view) {
        try {
            Intent i = new Intent(this, CreateAccountActivity.class);
            i.putExtra("manageData", manageData);
            startActivityForResult(i, CreateAccountActivity_ID);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
