package com.example.srinath.newsapp.network;

import com.example.srinath.newsapp.model.News;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkInterface {
    //get news article
    @GET("/v2/everything")
    Observable<News> getNews(@Query("q") String q,@Query("pageSize") int pageSize,@Query("page") int page,@Query("sortBy") String sortBy,
                             @Query("apiKey") String apiKey);
}
