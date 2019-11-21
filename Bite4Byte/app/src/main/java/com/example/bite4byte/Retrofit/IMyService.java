package com.example.bite4byte.Retrofit;

import java.util.Set;

import io.reactivex.Observable;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMyService {

    // arbitrary end point based on the YouTube Tutorial
    @POST("register")
    @FormUrlEncoded
    Call<String> registerUser(@Field("username") String username);

    @POST("food_preferences")
    @FormUrlEncoded
    Call<UserContents> foodPref(@Field("username") String username,
                                @Field("firstname") String firstname,
                                @Field("lastname") String lastname,
                                @Field("password") String password,
                                @Field("restrictions") String[] restrictions,
                                @Field("allergies") String[] allergies);

    // Body HTTP example requires single json object
//    @POST("example")
//    Observable<String> exampleBody(@Body RequestBody exampleRequest);

    @POST("edit_account")
    @FormUrlEncoded
    Call<UserContents> editAccount(@Field("username") String username,
                                   @Field("restrictions") String[] restrictions,
                                   @Field("allergies") String[] allergies);

    @POST("deleteacc")
    @FormUrlEncoded
    Call<UserContents> deleteAccount(@Field("username") String username);

    @POST("login")
    @FormUrlEncoded
    Call<UserContents> loginUser(@Field("username") String username,
                                 @Field("password") String password);

    @POST("search_account")
    @FormUrlEncoded
    Call<String []> searchAccount(@Field("query") String query);

    @POST("get_account")
    @FormUrlEncoded
    Call<UserContents> getAccount(@Field("username") String username);

}
