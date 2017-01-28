package com.udacity.mauricio.popularmovies.gui.view;

import android.os.Bundle;
import android.view.MenuItem;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;

@EActivity(R.layout.actv_detail)
public class DetailActivity extends BaseActivity {

    public static final String EXTRA_MOVIE = "com.udacity.mauricio.popularmovies.gui.view.DetailActivity.EXTRA_MOVIE";

    @Extra(DetailActivity.EXTRA_MOVIE)
    protected MovieDTO movie;

    @AfterViews
    protected void init() {
        Bundle args = new Bundle();
        args.putSerializable(DetailActivity.EXTRA_MOVIE, movie);
        MovieDetailFragment fragment = new MovieDetailFragment_();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
