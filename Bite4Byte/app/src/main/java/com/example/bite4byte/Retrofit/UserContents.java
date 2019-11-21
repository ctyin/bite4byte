package com.example.bite4byte.Retrofit;

import java.io.Serializable;

public class UserContents implements Serializable {
    protected String username;
    protected String password;
    protected String firstname;
    protected String lastname;
    protected String[] restrictions;
    protected String[] allergies;
    protected String[] friends;
    protected String[] orders;

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public String getFirstName() { return firstname; }

    public String getLastName() { return lastname; }

    public String[] getRestrictions() { return restrictions; }

    public String[] getAllergies() { return allergies; }

    public String[] getOrders() { return orders; }

    public String[] getFriendsList() { return friends; }

    public void setName(String name) {
        username = name;
    }
}
