package com.udacity.mauricio.popularmovies.gui.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.res.BooleanRes;

@EActivity(R.layout.actv_main)
public class MainActivity extends BaseActivity implements MovieListFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    @FragmentById
    protected MovieListFragment fragPopularMovies;

    @BooleanRes
    protected boolean twoPane;

    @AfterViews
    protected void init() {
        if (twoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MovieDetailFragment_(), DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(MovieDTO movie, ActivityOptionsCompat options) {
        if (twoPane) {
            Bundle args = new Bundle();
            args.putSerializable(DetailActivity.EXTRA_MOVIE, movie);
            MovieDetailFragment fragment = new MovieDetailFragment_();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity_.class);
            intent.putExtra(DetailActivity.EXTRA_MOVIE, movie);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
    }
}
