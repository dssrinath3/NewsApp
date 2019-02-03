package com.example.srinath.newsapp.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit = null;

    private static OkHttpClient buildClient() {
        return new OkHttpClient
                .Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }


    public static Retrofit getRetrofit(){

        if(retrofit==null){

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://newsapi.org")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(buildClient())
                    .build();

        }

        return retrofit;
    }
}
