package com.udacity.mauricio.popularmovies.gui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;


public class MovieListFragment extends Fragment
        implements LoadMovieTask.LoadMovieListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    protected RecyclerView rvMovies;
    protected TextView tvMessage;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout swRefresh;

    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected MovieAdapter adapter;
    protected LoadMovieTask task;

    protected boolean isSync;
    private int currentPage;
    private String sort, language;

    private BaseActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.frag_list_movie, container, false);

        rvMovies = (RecyclerView) viewRoot.findViewById(R.id.rvMovies);
        tvMessage = (TextView) viewRoot.findViewById(R.id.tvMessage);

        swRefresh = (SwipeRefreshLayout) viewRoot.findViewById(R.id.swRefresh);
        swRefresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swRefresh.setOnRefreshListener(this);

        toolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);

        activity = (BaseActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        adapter = new MovieAdapter(getContext(), this);

        rvMovies.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMovies.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                task = new LoadMovieTask(activity, MovieListFragment.this);
                List<String> params = new ArrayList<>();
                params.add(LoadMovieTask.LANGUAGE_PARAM_POSITION, language);
                params.add(LoadMovieTask.SORT_PARAM_POSITION, sort);
                params.add(LoadMovieTask.PAGE_PARAM_POSITION, String.valueOf(page + 1));
                task.execute(params.toArray(new String[LoadMovieTask.PARAM_NUMBER]));
            }
        };

        // Adds the scroll listener to RecyclerView
        rvMovies.addOnScrollListener(scrollListener);

        return viewRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppUtils.hasInternetConnection(getContext()) && !isSync) {
            showMessage();
            return;
        }

        if (!isSync()) {
            String languagePref = AppUtils.getPreferenceValue(activity, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));
            boolean hasLanguageChanged = language != null && !TextUtils.equals(language, languagePref);
            if (hasLanguageChanged)
                activity.restart(); // To update menu language
            else
                loadFirstMoviesPage();
        }
    }

    private boolean isSync() {
        String sortByPref = AppUtils.getPreferenceValue(activity, getString(R.string.pref_sort_key), getString(R.string.pref_sort_default_value));
        String languagePref = AppUtils.getPreferenceValue(activity, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));
        return TextUtils.equals(sort, sortByPref) && TextUtils.equals(language, languagePref);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                loadFirstMoviesPage();
                break;
            case R.id.action_settings:
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
                Intent intent = new Intent(activity, SettingsActivity.class);
                ActivityCompat.startActivity(activity, intent, options.toBundle());
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFirstMoviesPage() {
        adapter.clear();
        scrollListener.resetState();

        sort = AppUtils.getPreferenceValue(activity, getString(R.string.pref_sort_key), getString(R.string.pref_sort_default_value));
        language = AppUtils.getPreferenceValue(activity, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));

        List<String> params = new ArrayList<>(LoadMovieTask.PARAM_NUMBER);
        params.add(LoadMovieTask.LANGUAGE_PARAM_POSITION, language);
        params.add(LoadMovieTask.SORT_PARAM_POSITION, sort);
        params.add(LoadMovieTask.PAGE_PARAM_POSITION, "1");

        task = new LoadMovieTask(getContext(), this);
        task.execute(params.toArray(new String[LoadMovieTask.PARAM_NUMBER]));
        isSync = true;
    }


    private void showMessage() {
        rvMovies.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPreExecute() {
        swRefresh.setRefreshing(true);
    }

    @Override
    public void onLoadMovie(PageDTO page) {

        boolean hasNoMoreMovies = page == null || page.movies == null || page.movies.isEmpty();

        if (hasNoMoreMovies && currentPage == 0) {
            showMessage();
            return;
        }

        adapter.setMovies(page.movies);

        currentPage = page.page;

        swRefresh.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        int position = rvMovies.getChildLayoutPosition(view);
        MovieDTO movie = adapter.getMovies().get(position);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                Pair.create(view.findViewById(R.id.ivPoster), getString(R.string.movie_poster_trasition_name)),
                Pair.create(view.findViewById(R.id.tvDescription), getString(R.string.movie_overview_trasition_name)));

        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);

        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    public void onRefresh() {
        loadFirstMoviesPage();
    }
}
