package com.example.bite4byte.InternalData;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static android.content.Context.MODE_PRIVATE;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Data {
    /*
    * JSONObject Fields
    * "username"
    * "firstname"
    * "lastname"
    * "password"
    * "preferences"
    * "allergies"
    */
    private Data instance = new Data();
    Map<String, JSONObject> accountMap;
    String accountFileName = "Accounts.json";

    public Data getInstance() {
        return instance;
    }

    private Data() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONArray viols = (JSONArray)parser.parse(new FileReader(accountFileName));

        Iterator <JSONObject> iter = viols.iterator();
        accountMap = new HashMap<String, JSONObject>();
        while (iter.hasNext()) {
            JSONObject a = (JSONObject) iter.next();
            try {
                accountMap.put((String) a.get("username"), a);
            } catch (Exception e) {
                continue;
            }
        }
    }

    //for initial validation of username when account is created
    public boolean createUser(String username, String firstname, String lastname, String password) {
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
