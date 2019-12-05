package com.example.bite4byte.Retrofit;

import java.io.Serializable;

public class UserContents implements Serializable {
    protected String username;
    protected String password;
    protected String firstname;
    protected String lastname;
    protected double rating;
    protected int numRatedBy;
    protected String[] restrictions;
    protected String[] allergies;
    protected String[] friends;
    protected String[] orders;
    protected String[] friend_requests;
    protected String [] groupNames;

    public String getUsername() {
        return username;
    }

    public String getPassword() { return password; }

    public String getFirstName() { return firstname; }

    public String getLastName() { return lastname; }

    public String[] getRestrictions() { return restrictions; }

    public String[] getAllergies() { return allergies; }

    public String[] getOrders() { return orders; }

    public double getRating() { return rating; }

    public double getNumRatedBy() { return numRatedBy; }

    public String[] getFriendsList() { return friends; }

    public String[] getFriendRequests() { return friend_requests; }

    public String[] getGroupNames() { return groupNames; }

    public void setName(String name) {
        username = name;
    }
}
