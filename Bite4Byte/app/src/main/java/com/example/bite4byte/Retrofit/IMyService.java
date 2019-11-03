package com.example.bite4byte.Retrofit;

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
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password);

    // Body HTTP example requires single json object
//    @POST("example")
//    Observable<String> exampleBody(@Body RequestBody exampleRequest);

    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("name") String name,
                                 @Field("password") String password);
}
