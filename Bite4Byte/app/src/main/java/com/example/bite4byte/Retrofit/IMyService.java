package com.example.bite4byte.Retrofit;

import com.example.bite4byte.Messaging.ChatBubble;
import com.example.bite4byte.Messaging.ConversationResult;

import java.util.List;
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

    @POST("convos")
    @FormUrlEncoded
    Call<List<ConversationResult>> getConversations(@Field("username") String username);

    @POST("singleconvo")
    @FormUrlEncoded
    Call<List<ChatBubble>> getSingleCorr(@Field("convo_id") String convo_id);

    @POST("saveMessage")
    @FormUrlEncoded
    Call<String> saveMessage(@Field("sender") String sender, @Field("convo_id") String convo_id,
                             @Field("contents") String contents, @Field("created_at") Long created_at);
}
