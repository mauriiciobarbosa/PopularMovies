package com.udacity.mauricio.popularmovies.gui.view;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.conn.ConnectionHandler;
import com.udacity.mauricio.popularmovies.gui.adapter.MovieAdapter;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.PageDTO;
import com.udacity.mauricio.popularmovies.tasks.TheMovieDbTask;
import com.udacity.mauricio.popularmovies.utils.AppUtils;
import com.udacity.mauricio.popularmovies.utils.EndlessRecyclerViewScrollListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.BooleanRes;

import java.util.ArrayList;
import java.util.List;

import static com.udacity.mauricio.popularmovies.tasks.TheMovieDbTask.GET_MOVIES_REQUEST_CODE;

@OptionsMenu(R.menu.main)
@EFragment(R.layout.frag_list_movie)
public class MovieListFragment extends Fragment
    implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @ViewById
    protected RecyclerView rvMovies;

    @ViewById
    protected TextView tvMessage;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected SwipeRefreshLayout swRefresh;

    @BooleanRes
    protected boolean isLand;

    @Bean
    protected TheMovieDbTask task;

    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected MovieAdapter adapter;

    private PageDTO currentPage;
    private String sort, language;

    private BaseActivity activity;

    private BaseActivity.Callback listener;

    private ConnectionHandler movieHandler;

    @AfterViews
    public void init() {
        swRefresh.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary, R.color.colorPrimaryDark);
        swRefresh.setOnRefreshListener(this);

        toolbar.setTitle(R.string.app_name);

        activity = (BaseActivity) getActivity();

        if (!(activity instanceof BaseActivity.Callback))
            throw new IllegalStateException("Activity must implement MovieListFragment.Callback");

        listener = (BaseActivity.Callback) activity;
        activity.setSupportActionBar(toolbar);

        adapter = new MovieAdapter(getContext(), this);

        rvMovies.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMovies.setLayoutManager(linearLayoutManager);

        if (isLand)
            rvMovies.addItemDecoration(new DividerItemDecoration(getContext(),
                    linearLayoutManager.getOrientation()));

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (currentPage.totalPages > page) {
                    List<String> params = new ArrayList<>();
                    params.add(TheMovieDbTask.LANGUAGE_PARAM_POSITION, language);
                    params.add(TheMovieDbTask.SORT_PARAM_POSITION, sort);
                    params.add(TheMovieDbTask.PAGE_PARAM_POSITION, String.valueOf(page + 1));
                    loadMovies(params.toArray(new String[TheMovieDbTask.PARAM_NUMBER]));
                } else {
                    Snackbar.make(rvMovies, R.string.msg_has_no_more_data, Snackbar.LENGTH_SHORT).show();
                }
            }
        };

        // Adds the scroll listener to RecyclerView
        rvMovies.addOnScrollListener(scrollListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!AppUtils.hasInternetConnection(getContext()) && adapter.getItemCount() == 0) {
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

    @OptionsItem
    protected void actionRefresh() {
        loadFirstMoviesPage();
    }

    @OptionsItem
    protected void actionSettings() {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        Intent intent = new Intent(activity, SettingsActivity_.class);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private void loadFirstMoviesPage() {
        adapter.clear();
        scrollListener.resetState();

        sort = AppUtils.getPreferenceValue(activity, getString(R.string.pref_sort_key), getString(R.string.pref_sort_default_value));
        language = AppUtils.getPreferenceValue(activity, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));

        List<String> params = new ArrayList<>(TheMovieDbTask.PARAM_NUMBER);
        params.add(TheMovieDbTask.LANGUAGE_PARAM_POSITION, language);
        params.add(TheMovieDbTask.SORT_PARAM_POSITION, sort);
        params.add(TheMovieDbTask.PAGE_PARAM_POSITION, "1");

        loadMovies(params.toArray(new String[TheMovieDbTask.PARAM_NUMBER]));
    }

    public void loadMovies(String... params) {
        task.setHandler(GET_MOVIES_REQUEST_CODE, getMovieHandler());
        task.getMovies(params);
    }

    private void showMessage() {
        rvMovies.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int position = rvMovies.getChildLayoutPosition(view);
        MovieDTO movie = adapter.getMovies().get(position);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                Pair.create(view.findViewById(R.id.ivPoster), getString(R.string.movie_poster_trasition_name)),
                Pair.create(view.findViewById(R.id.tvDescription), getString(R.string.movie_overview_trasition_name)));

        listener.onItemSelected(movie, options);
    }

    @Override
    public void onRefresh() {
        loadFirstMoviesPage();
    }

    private ConnectionHandler getMovieHandler() {
        if (movieHandler == null) {
            movieHandler = new ConnectionHandler() {
                @Override
                public void onPreExecute(int requestCode) {
                    swRefresh.setRefreshing(true);
                }

                @Override
                public void onConnectionSucess(int requestCode, Object result) {
                    PageDTO page = (PageDTO) result;

                    boolean hasNoMoreMovies = page == null || page.movies == null || page.movies.isEmpty();

                    swRefresh.setRefreshing(false);

                    if (hasNoMoreMovies && currentPage == null) {
                        showMessage();
                        return;
                    }

                    currentPage = page;
                    adapter.setMovies(currentPage.movies);
                }

                @Override
                public void onConnectionError(int requestCode, Exception e) {
                    Log.e(LOG_TAG, "Error on getMovies: " + e.getMessage());
                }
            };
        }
        return movieHandler;
    }

}
