package com.example.bite4byte.InternalData;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.example.bite4byte.MainActivity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Data implements Serializable {
    /*
    * JSONObject Fields
    * "username"
    * "firstname"
    * "lastname"
    * "password"
    * "preferences"
    * "allergies"
    */

    Map<String, JSONObject> accountMap;
    String accountFileName = "Accounts.json";


    public Data(Context context) {
        try {

            JSONParser parser = new JSONParser();
            JSONArray accounts = (JSONArray) parser.parse(loadJSONFromAsset(context));

            Iterator<JSONObject> iter = accounts.iterator();
            accountMap = new HashMap<String, JSONObject>();
            while (iter.hasNext()) {
                JSONObject a = (JSONObject) iter.next();
                try {
                    accountMap.put((String) a.get("username"), a);
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println(e);

        }
    }

    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(accountFileName);
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

    //for initial validation of username when account is created
    public boolean verifyAvailableUsername(String username) {
        System.out.println("reached");
        if (accountMap.containsKey(username)) {
            return false;
        }
        return true;
    }

    //returns true if account is successfully created
    public boolean createAccount(String username, String firstname, String lastname, String password, String[] preferences, String[] allergies) {
        JSONObject newAccount = new JSONObject();
        newAccount.put("username", username);
        newAccount.put("firstname", firstname);
        newAccount.put("lastname", lastname);
        newAccount.put("password", password);
        newAccount.put("preferences", preferences);
        newAccount.put("allergies", allergies);
        return true;
    }

    //returns the JSONObject for the username and null if username, pswd is invalid
    public JSONObject login(String usernameIn, String passwordIn) {
        if (!accountMap.containsKey(usernameIn)) {
            return null;
        }
        if (accountMap.get(usernameIn).get("password") == passwordIn) {
            return accountMap.get(usernameIn);
        }
        return null;
    }


}
