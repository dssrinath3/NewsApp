package com.example.srinath.newsapp.ui;

public interface NewsPresenterInterface {
    void getNewsFirstPage(int page_size, int currentSize);
    void getNewsLoadingNextPage(int page_size, int currentSize);
}
