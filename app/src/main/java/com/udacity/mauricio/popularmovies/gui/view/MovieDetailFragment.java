package com.udacity.mauricio.popularmovies.gui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.utils.AppBarStateChangeListener;
import com.udacity.mauricio.popularmovies.utils.AppUtils;


public class MovieDetailFragment extends Fragment {

    protected ImageView image;
    protected TextView tvOriginalTitle, tvOverview, tvGender, tvReleaseDate;
    protected RatingBar rbMovieStars;
    protected Toolbar toolbar;
    protected CollapsingToolbarLayout collapsingToolbar;
    protected AppBarLayout appBarLayout;

    protected MovieDTO movie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View viewRoot = inflater.inflate(R.layout.fragment_detail_movie, container, false);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        toolbar = (Toolbar) viewRoot.findViewById(R.id.toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) viewRoot.findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) viewRoot.findViewById(R.id.appbar);

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

        configureTransactionNames();
        configureToolbarColor();

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

    private void configureTransactionNames() {
        ViewCompat.setTransitionName(image, getString(R.string.movie_poster_trasition_name));
        ViewCompat.setTransitionName(tvOverview, getString(R.string.movie_overview_trasition_name));
    }

    private void configureToolbarColor() {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        Palette palette = Palette.from(bitmap).generate();
        int primaryColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        final int toolbarColor = palette.getMutedColor(primaryColor);
        collapsingToolbar.setContentScrimColor(toolbarColor);

        appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                int statusBarColor = (state == AppBarStateChangeListener.State.COLLAPSED) ?
                        AppUtils.getDarketColor(toolbarColor) : Color.TRANSPARENT;
                changeStatusBarColor(statusBarColor);
            }

        });
    }

    @TargetApi(21)
    private void changeStatusBarColor(int statusBarColor) {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);
    }


}
