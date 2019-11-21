package com.example.bite4byte.Retrofit;

import com.example.bite4byte.Messaging.ConversationResult;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import okhttp3.RequestBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
                                @Field("allergies") String[] allergies,
                                @Field("orders") String[] orders);

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


    @POST("post_food")
    @FormUrlEncoded
    Call<FoodContents> uploadFood(@Field("id") String id,
                                  @Field("quantity") int quantity,
                                  @Field("foodName") String foodName,
                                  @Field("sellerUserName") String username,
                                  @Field("description") String foodDesc,
                                  @Field("ingredients") String[] ingredientArr,
                                  @Field("restrictions") String[] restrictionArr,
                                  @Field("cuisines") String[] cuisineArr,
                                  @Field("picture") String picture,
                                  @Field("picturePath") String picPath,
                                  @Field("isAvailable") boolean isAvailable,
                                  @Field("location") String location,
                                  @Field("postDate") Date date);

    @GET("get_foods")
    Call<List<FoodContents>> getFoods();

    @POST("req_food")
    @FormUrlEncoded
    Call<FoodContents> getOneFood(@Field("id") String id);

    @POST("order_food")
    @FormUrlEncoded
    Call<UserContents> orderFood(@Field("id") String id,
                                 @Field("foodName") String foodName,
                                 @Field("username") String username);
}
