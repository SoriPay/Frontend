package com.example.graduation.retrofit;

import com.google.gson.Gson;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private Retrofit retrofit;
    public RetrofitService() {
        initializeRetrofit();
    }
    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.228.8:8080") //연결한 와이파이 보고 다시 입력해야함
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

    }
    public Retrofit getRetrofit() {
        return retrofit;
    }
}