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
import android.view.View;
import android.widget.TextView;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.gui.adapter.MovieAdapter;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.PageDTO;
import com.udacity.mauricio.popularmovies.tasks.LoadMovieTask;
import com.udacity.mauricio.popularmovies.utils.AppUtils;
import com.udacity.mauricio.popularmovies.utils.EndlessRecyclerViewScrollListener;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.BooleanRes;

import java.util.ArrayList;
import java.util.List;

@OptionsMenu(R.menu.main)
@EFragment(R.layout.frag_list_movie)
public class MovieListFragment extends Fragment
        implements LoadMovieTask.LoadMovieListener,
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    @ViewById
    protected RecyclerView rvMovies;

    @ViewById
    protected TextView tvMessage;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected SwipeRefreshLayout swRefresh;

    @BooleanRes
    protected boolean twoPane;


    // Store a member variable for the listener
    protected EndlessRecyclerViewScrollListener scrollListener;
    protected MovieAdapter adapter;
    protected LoadMovieTask task;

    private PageDTO currentPage;
    private String sort, language;

    private BaseActivity activity;

    private Callback listener;

    @AfterViews
    public void init() {
        swRefresh.setColorSchemeResources(R.color.colorAccent,
                R.color.colorPrimary, R.color.colorPrimaryDark);
        swRefresh.setOnRefreshListener(this);

        toolbar.setTitle(R.string.app_name);

        activity = (BaseActivity) getActivity();

        if (!(activity instanceof Callback))
            throw new IllegalStateException("Activity must implement MovieListFragment.Callback");

        listener = (Callback) activity;
        activity.setSupportActionBar(toolbar);

        adapter = new MovieAdapter(getContext(), this);

        rvMovies.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMovies.setLayoutManager(linearLayoutManager);

        if (twoPane)
            rvMovies.addItemDecoration(new DividerItemDecoration(getContext(),
                    linearLayoutManager.getOrientation()));

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                if (currentPage.totalPages > page) {
                    task = new LoadMovieTask(activity, MovieListFragment.this);
                    List<String> params = new ArrayList<>();
                    params.add(LoadMovieTask.LANGUAGE_PARAM_POSITION, language);
                    params.add(LoadMovieTask.SORT_PARAM_POSITION, sort);
                    params.add(LoadMovieTask.PAGE_PARAM_POSITION, String.valueOf(page + 1));
                    task.execute(params.toArray(new String[LoadMovieTask.PARAM_NUMBER]));
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

        List<String> params = new ArrayList<>(LoadMovieTask.PARAM_NUMBER);
        params.add(LoadMovieTask.LANGUAGE_PARAM_POSITION, language);
        params.add(LoadMovieTask.SORT_PARAM_POSITION, sort);
        params.add(LoadMovieTask.PAGE_PARAM_POSITION, "1");

        task = new LoadMovieTask(getContext(), this);
        task.execute(params.toArray(new String[LoadMovieTask.PARAM_NUMBER]));
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

        if (hasNoMoreMovies && currentPage == null) {
            showMessage();
            return;
        }

        currentPage = page;

        adapter.setMovies(currentPage.movies);

        swRefresh.setRefreshing(false);
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

    public interface Callback {
        void onItemSelected(MovieDTO movie, ActivityOptionsCompat options);
    }
}
