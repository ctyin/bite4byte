package com.example.bite4byte.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class FoodContents {
    @SerializedName("id")
    private String id;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("sellerUserName")
    private String sellerUserName;

    @SerializedName("description")
    private String description;

    @SerializedName("ingredients")
    private String[] ingredients;

    @SerializedName("restrictions")
    private String[] restrictions;

    @SerializedName("cuisines")
    private String[] cuisines;

    @SerializedName("picture")
    private String picture;

    @SerializedName("picturePath")
    private String picturePath;

    @SerializedName("isAvailable")
    private boolean isAvailable;

    @SerializedName("location")
    private String location;

    @SerializedName("date")
    private Date date;

    @SerializedName("group")
    private boolean group;

    public String getID() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public String getDescription() {
        return description;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String[] getRestrictions() {
        return restrictions;
    }

    public String[] getCuisines() {
        return cuisines;
    }

    public String getPicture() {
        return picture;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setIsAvailable() {
        isAvailable = !isAvailable;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public boolean isInGroup() { return group; }

}
