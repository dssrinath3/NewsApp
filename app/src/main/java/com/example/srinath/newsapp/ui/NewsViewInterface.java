package com.example.srinath.newsapp.ui;

import com.example.srinath.newsapp.model.News;

public interface NewsViewInterface {
    void showProgressBar();
    void hideProgressBar();
    void loadFirstPageNewsArticle(News newsResponse);
    void loadNextPageNewsArticle(News newsResponse);
    void displayError(String s);
}
