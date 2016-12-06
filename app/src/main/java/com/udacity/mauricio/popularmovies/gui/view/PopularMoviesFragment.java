package com.udacity.mauricio.popularmovies.gui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.gui.adapter.MovieAdapter;
import com.udacity.mauricio.popularmovies.models.PageDTO;
import com.udacity.mauricio.popularmovies.tasks.LoadMovieTask;
import com.udacity.mauricio.popularmovies.utils.AppUtils;


public class PopularMoviesFragment extends Fragment implements LoadMovieTask.LoadMovieListener {

    protected RecyclerView rvMovies;
    protected ProgressBar progressBar;
    protected TextView tvMessage;

    protected MovieAdapter adapter;

    protected LoadMovieTask task;
    protected boolean isSync;

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

        adapter = new MovieAdapter(getContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(layoutManager);

        task = new LoadMovieTask(getContext(), this);

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
            task.execute();
            isSync = true;
        }
    }

    private void showMessage() {
        rvMovies.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPreExecute() {
        rvMovies.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadMovie(PageDTO page) {

        if (page == null || page.movies == null || page.movies.isEmpty()) {
            showMessage();
            return;
        }

        adapter.setMovies(page.movies);
        adapter.notifyDataSetChanged();
        rvMovies.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        tvMessage.setVisibility(View.GONE);
    }
}
