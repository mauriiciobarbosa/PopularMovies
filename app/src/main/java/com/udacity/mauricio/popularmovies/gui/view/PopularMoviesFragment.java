package com.udacity.mauricio.popularmovies.gui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.gui.adapter.MovieAdapter;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.PageDTO;
import com.udacity.mauricio.popularmovies.tasks.LoadMovieTask;
import com.udacity.mauricio.popularmovies.utils.AppUtils;
import com.udacity.mauricio.popularmovies.utils.EndlessRecyclerViewScrollListener;


public class PopularMoviesFragment extends Fragment implements LoadMovieTask.LoadMovieListener, View.OnClickListener {

    protected RecyclerView rvMovies;
    protected ProgressBar progressBar;
    protected TextView tvMessage;

    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;

    protected MovieAdapter adapter;

    protected LoadMovieTask task;
    protected boolean isSync;

    private int currentPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_popular_movies, container, false);

        rvMovies = (RecyclerView) viewRoot.findViewById(R.id.rvMovies);
        progressBar = (ProgressBar) viewRoot.findViewById(R.id.progressBar);
        tvMessage = (TextView) viewRoot.findViewById(R.id.tvMessage);
        Toolbar toolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        adapter = new MovieAdapter(getContext(), this);

        rvMovies.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMovies.setLayoutManager(linearLayoutManager);

        final Context context = getContext();

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                task = new LoadMovieTask(context, PopularMoviesFragment.this);
                task.execute(String.valueOf(page + 1));
            }
        };

        // Adds the scroll listener to RecyclerView
        rvMovies.addOnScrollListener(scrollListener);

        return viewRoot;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!AppUtils.hasInternetConnection(getContext())) {
            showMessage();
            return;
        }

        if (!isSync) {
            loadFirstMoviesPage();
        }
    }

    private void loadFirstMoviesPage() {
        // 1. First, clear the array of data
        //listOfItems.clear();
        // 2. Notify the adapter of the update
        //adapter.notifyDataSetChanged(); // or notifyItemRangeRemoved
        // 3. Reset endless scroll listener when performing a new search
       // scrollListener.resetState();
        task = new LoadMovieTask(getContext(), this);
        task.execute("1");
        isSync = true;
    }

    private void showMessage() {
        rvMovies.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPreExecute() {
        boolean isFirstLoad = (currentPage == 0);
        if (isFirstLoad) {
            rvMovies.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadMovie(PageDTO page) {

        boolean hasNoMoreMovies = page == null || page.movies == null || page.movies.isEmpty();

        if (hasNoMoreMovies && currentPage == 0) {
            showMessage();
            return;
        }

        adapter.setMovies(page.movies);

        boolean isFirstLoad = (currentPage == 0);

        currentPage = page.page;

        if (isFirstLoad) {
            rvMovies.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            tvMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int position = rvMovies.getChildLayoutPosition(view);
        MovieDTO movie = adapter.getMovies().get(position);

        Activity activity = getActivity();

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                Pair.create(view.findViewById(R.id.ivPoster), getString(R.string.movie_poster_trasition_name)),
                Pair.create(view.findViewById(R.id.tvDescription), getString(R.string.movie_overview_trasition_name)));

        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}
