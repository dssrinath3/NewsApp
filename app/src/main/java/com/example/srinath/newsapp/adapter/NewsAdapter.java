package com.example.srinath.newsapp.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.srinath.newsapp.R;
import com.example.srinath.newsapp.model.Article;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<Article> newsList;
    private Context context;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private List<Article> newsListFiltered;

    public NewsAdapter(List<Article> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
        this.newsListFiltered = newsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_news_articles, parent, false);
            return new NewsHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof NewsHolder) {
            populateItemRows((NewsHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    private void populateItemRows(NewsHolder holder, int position) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),"font/RobotoCondensed-Regular.ttf");
        holder.tvNewsTitle.setTypeface(tf,Typeface.BOLD);
        if (newsList.get(position)!=null){
            if (newsList.get(position).getTitle()!=null){
                holder.tvNewsTitle.setText(newsList.get(position).getTitle());
            }
            if (newsList.get(position).getDescription()!=null){
                holder.tvNewsDescription.setText(newsList.get(position).getDescription());
            }
            if (newsList.get(position).getUrl()!=null){
                holder.tvNewsUrl.setText(newsList.get(position).getUrl());
                Linkify.addLinks(holder.tvNewsUrl, Linkify.ALL);
            }
            if (newsList.get(position).getPublishedAt()!=null){
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
                DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy KK:mm a");
                try {
                    String datePublished = outputFormat.format(inputFormat.parse(newsList.get(position).getPublishedAt()));
                    holder.tvPublishedDate.setText(datePublished);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }


            Glide.with(context).load(newsList.get(position).getUrlToImage()).fitCenter().into(holder.imageNews);
        }
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed


    }


    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsListFiltered.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return newsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    newsListFiltered = newsList;
                } else {
                    List<Article> filteredList = new ArrayList<>();
                    for (Article row : newsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for title or date match
                        //Log.e("title",row.getTitle());
                        //Log.e("date",row.getPublishedAt());
                       // Log.e("charString",charString);
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getPublishedAt().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    newsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = newsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                newsListFiltered = (ArrayList<Article>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class NewsHolder extends RecyclerView.ViewHolder {

        TextView tvNewsTitle,tvNewsUrl,tvPublishedDate,tvNewsDescription;
        ImageView imageNews;

        public NewsHolder(View v) {
            super(v);
            tvNewsTitle = (TextView) v.findViewById(R.id.tvNewsTitle);
            tvNewsDescription = (TextView) v.findViewById(R.id.tvNewsDescription);
            tvNewsUrl = (TextView) v.findViewById(R.id.tvNewsUrl);
            tvPublishedDate = (TextView) v.findViewById(R.id.tvPublishedDate);
            imageNews = (ImageView) v.findViewById(R.id.imageNews);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }



}
