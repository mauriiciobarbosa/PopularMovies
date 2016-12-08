package com.udacity.mauricio.popularmovies.gui.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.udacity.mauricio.popularmovies.R;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "com.udacity.mauricio.popularmovies.gui.view.DetailActivity.EXTRA_MOVIE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}
