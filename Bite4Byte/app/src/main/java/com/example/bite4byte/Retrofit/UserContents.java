package com.example.bite4byte.Retrofit;

public class UserContents {
    protected String username;
    protected String firstname;
    protected String lastname;
    protected String[] restrictions;
    protected String[] allergies;
    protected String[] friends;

    public String getUsername() {
        return username;
    }

    public String getFirstName() { return firstname; }

    public String getLastName() { return lastname; }

    public String[] getRestrictions() { return restrictions; }

    public String[] getAllergies() { return allergies; }

    public String[] getFriendsList() { return friends; }
}
