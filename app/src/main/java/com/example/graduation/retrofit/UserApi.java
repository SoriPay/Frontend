package com.example.graduation.retrofit;

import com.example.graduation.model.LoginRequest;
import com.example.graduation.model.LoginResponse;
import com.example.graduation.model.User;

import org.json.JSONObject;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.Call;
import retrofit2.http.Query;

public interface UserApi {

    @GET("/user/get-all")
    Call<List<User>> getAllUser();

    @POST("/user/save")
    Call<User> save(@Body User user);


    @GET("/user/getUser")
    Call<User> getUser(@Query("id") String id, @Query("password") String password);


    @POST("user/checkEmailRegistration")
    Call<User> checkEmailRegistration(@Body User user);
    }



