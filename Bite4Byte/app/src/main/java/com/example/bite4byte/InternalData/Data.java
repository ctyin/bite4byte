package com.example.bite4byte.InternalData;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;

import com.example.bite4byte.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    static Map<String, JSONObject> accountMap;
    String accountFileName = "accounts.json";
    JSONArray accounts;
    //Context context;


    public Data(Context context) {
        //this.context = context;
        try {
            //File internalDir = context.getFilesDir();
            //System.out.println(internalDir);
            //InputStream is = context.openFileInput(accountFileName);
            JSONParser parser = new JSONParser();
            this.accounts = (JSONArray) parser.parse(read(context, accountFileName));
            System.out.println(accounts.size());

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

    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }





    /*private String loadJSONFromAsset(Context context) {
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
    }*/

    //for initial validation of username when account is created
    public boolean verifyAvailableUsername(String username) {
        System.out.println("reached");
        if (accountMap.containsKey(username)) {
            return false;
        }
        return true;
    }

    //returns true if account is successfully created
    public boolean createAccount(Context context, String username, String firstname, String lastname, String password, String[] preferences, String[] allergies) {
        JSONObject newAccount = new JSONObject();
        newAccount.put("username", username);
        newAccount.put("firstname", firstname);
        newAccount.put("lastname", lastname);
        newAccount.put("password", password);
        //newAccount.put("preferences", preferences);
        //newAccount.put("allergies", allergies);
        accounts.add(newAccount);
        accountMap.put(username, newAccount);
        System.out.println(accountMap.keySet().size());
        String jsonString = accounts.toJSONString();
        try {
            FileOutputStream fos = context.openFileOutput(accountFileName,Context.MODE_PRIVATE);
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

    //returns the JSONObject for the username and null if username, pswd is invalid
    public JSONObject login(String usernameIn, String passwordIn) {
        System.out.println(accountMap.keySet().size());
        System.out.println(usernameIn + " " + passwordIn);
        System.out.println(accountMap.containsKey(usernameIn));
        if (!accountMap.containsKey(usernameIn)) {
            return null;
        }
        String pass = (String) accountMap.get(usernameIn).get("password");
        if (pass.equals(passwordIn)) {
            return accountMap.get(usernameIn);
        }
        System.out.println("reached");
        return null;
    }


}