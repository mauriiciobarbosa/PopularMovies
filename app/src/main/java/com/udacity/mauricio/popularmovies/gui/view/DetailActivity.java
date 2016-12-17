package com.udacity.mauricio.popularmovies.gui.view;

import android.os.Bundle;
import android.view.MenuItem;

import com.udacity.mauricio.popularmovies.R;

public class DetailActivity extends BaseActivity {

    public static final String EXTRA_MOVIE = "com.udacity.mauricio.popularmovies.gui.view.DetailActivity.EXTRA_MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actv_detail);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
