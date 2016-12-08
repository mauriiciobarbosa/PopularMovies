package com.udacity.mauricio.popularmovies.gui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;

import java.text.NumberFormat;


public class MovieDetailFragment extends Fragment {

    protected ImageView image;
    protected TextView tvOriginalTitle, tvOverview, tvGender, tvReleaseDate;
    protected RatingBar rbMovieStars;

    protected MovieDTO movie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Toolbar toolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) viewRoot.findViewById(R.id.collapsing_toolbar);

        image = (ImageView) viewRoot.findViewById(R.id.image);
        tvOriginalTitle = (TextView) viewRoot.findViewById(R.id.tvOriginalTitle);
        tvOverview = (TextView) viewRoot.findViewById(R.id.tvOverview);
        tvGender = (TextView) viewRoot.findViewById(R.id.tvGender);
        tvReleaseDate = (TextView) viewRoot.findViewById(R.id.tvReleaseDate);
        rbMovieStars = (RatingBar) viewRoot.findViewById(R.id.rbMovieStars);

        movie = (MovieDTO) getActivity().getIntent().getSerializableExtra(DetailActivity.EXTRA_MOVIE);

        fillMovieInfo(activity, movie);

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set Collapsing Toolbar layout to the screen
        // Set title of Detail page
        collapsingToolbar.setTitle(movie.title);

        return viewRoot;
    }

    private void fillMovieInfo(Context context, MovieDTO movie) {
        Picasso.with(context).load(context.getString(R.string.baseUrl_image) + movie.posterPath).into(image);
        tvOriginalTitle.setText(movie.originalTitle);
        tvOverview.setText(movie.overview);
        //tvGender.setText(movie.overview);
        tvReleaseDate.setText(movie.releaseDate);

        float voteAverage = Double.valueOf(movie.voteAverage).intValue();
        rbMovieStars.setRating(voteAverage / 2);
    }

}
