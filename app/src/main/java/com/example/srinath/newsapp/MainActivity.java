package com.example.srinath.newsapp;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.srinath.newsapp.adapter.NewsAdapter;
import com.example.srinath.newsapp.model.Article;
import com.example.srinath.newsapp.model.News;
import com.example.srinath.newsapp.ui.NewsPresenter;
import com.example.srinath.newsapp.ui.NewsViewInterface;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NewsViewInterface {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerNews)
    RecyclerView recyclerNews;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    private List<Article> newsList;
    private int page_size=10;
    private boolean isLoading=false;
    private LinearLayoutManager linearLayoutManager;
    private static final int PAGE_NUMBER = 1;
    private int currentPage = PAGE_NUMBER;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerNews.setLayoutManager(linearLayoutManager);
        recyclerNews.setItemAnimator(new DefaultItemAnimator());


        newsPresenter = new NewsPresenter(this);
        newsPresenter.getNewsFirstPage(page_size,currentPage);

    }
    private void initScrollListener(RecyclerView recyclerNews) {

        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == newsList.size() - 1) {

                        isLoading = true;
                        currentPage += 1;
                        newsPresenter.getNewsLoadingNextPage(page_size, currentPage);
                        Log.e(TAG, "loadNextPage: " + currentPage);
                    }
                }
            }
        });


    }




    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void loadFirstPageNewsArticle(News newsResponse) {
        if (newsResponse != null) {
            newsList = newsResponse.getArticles();
            Log.v(TAG, newsResponse.getStatus());

            if (newsList!=null && newsList.size()>0){
                newsAdapter = new NewsAdapter(newsList, MainActivity.this);
                recyclerNews.setAdapter(newsAdapter);
                initScrollListener(recyclerNews);

            }else{
                Toast.makeText(this, "No Articles Found.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "News response null");
        }
    }


    @Override
    public void displayError(String s) {
        Toast.makeText(this, "server failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadNextPageNewsArticle(News newsResponse) {

        if (isLoading) {
            if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == newsList.size() - 1) {
                //bottom of list!
                Toast.makeText(this, "Page "+currentPage, Toast.LENGTH_SHORT).show();
                loadMore(newsResponse);
                isLoading = true;
            }
        }

    }

    private void loadMore(final News newsResponse) {
        Log.e(TAG, String.valueOf(newsResponse.getArticles().size()));

        newsList.add(null);
        newsAdapter.notifyItemInserted(newsList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newsList.remove(newsList.size() - 1);
                int scrollPosition = newsList.size();
                newsAdapter.notifyItemRemoved(scrollPosition);

                if (newsResponse != null && newsResponse.getArticles().size()>0) {
                    for (Article article :newsResponse.getArticles()){
                        newsList.add(article);
                    }
                } else {
                    Log.d(TAG, "No News Found.");
                }

                isLoading = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                newsAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                newsAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

}
