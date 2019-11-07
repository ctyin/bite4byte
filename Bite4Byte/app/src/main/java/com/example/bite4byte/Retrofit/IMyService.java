package com.example.bite4byte.Retrofit;

import java.util.Set;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMyService {

    // arbitrary end point based on the YouTube Tutorial
    @POST("register")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("username") String username,
                                    @Field("firstname") String firstname,
                                    @Field("lastname") String lastname,
                                    @Field("password") String password);

    @POST("food_preferences")
    @FormUrlEncoded
    Observable<String> foodPref(@Field("username") String username,
                                @Field("restrictions") String[] restrictions,
                                @Field("allergies") String[] allergies);

    // Body HTTP example requires single json object
//    @POST("example")
//    Observable<String> exampleBody(@Body RequestBody exampleRequest);

    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("username") String username,
                                 @Field("password") String password);
}
