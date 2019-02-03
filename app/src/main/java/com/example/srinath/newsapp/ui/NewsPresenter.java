package com.example.srinath.newsapp.ui;

import android.util.Log;

import com.example.srinath.newsapp.model.News;
import com.example.srinath.newsapp.network.NetworkClient;
import com.example.srinath.newsapp.network.NetworkInterface;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsPresenter implements NewsPresenterInterface {
    private NewsViewInterface newsViewInterface;
    private String TAG = "NewsPresenter";


    public NewsPresenter(NewsViewInterface newsViewInterface) {
        this.newsViewInterface = newsViewInterface;
    }
    @Override
    public void getNewsFirstPage(int page_size, int page_number) {
        if (newsViewInterface!=null){
            newsViewInterface.showProgressBar();
            getObservable(page_size,page_number).subscribeWith(getObserver());
        }
    }

    @Override
    public void getNewsLoadingNextPage(int page_size, int page_number) {
        if (newsViewInterface!=null){
          //  newsViewInterface.showProgressBar();
            getObservable(page_size,page_number).subscribeWith(getObserverNext());
        }
    }

    public Observable<News> getObservable(int page_size, int page_number) {
        Log.e("page_size", String.valueOf(page_size)+"page no "+page_number);
        return NetworkClient.getRetrofit().create(NetworkInterface.class)
                .getNews("apple",page_size,page_number,"popularity","3363a374df9f4660a260a187915f0937")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public DisposableObserver<News> getObserver(){
        return new DisposableObserver<News>() {

            @Override
            public void onNext(@NonNull News news) {
                Log.d(TAG,"OnNext"+news.getArticles());
                if (newsViewInterface!=null) {
                    newsViewInterface.loadFirstPageNewsArticle(news);
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                newsViewInterface.displayError("Error fetching news Data");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
                newsViewInterface.hideProgressBar();
            }
        };
    }

    public DisposableObserver<News> getObserverNext(){
        return new DisposableObserver<News>() {

            @Override
            public void onNext(@NonNull News news) {
                Log.d(TAG,"OnNext"+news.getArticles());
                if (newsViewInterface!=null) {
                    newsViewInterface.loadNextPageNewsArticle(news);
                }

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG,"Error"+e);
                e.printStackTrace();
                newsViewInterface.displayError("Error fetching news Data");
            }

            @Override
            public void onComplete() {
                Log.d(TAG,"Completed");
                newsViewInterface.hideProgressBar();
            }
        };
    }
}
