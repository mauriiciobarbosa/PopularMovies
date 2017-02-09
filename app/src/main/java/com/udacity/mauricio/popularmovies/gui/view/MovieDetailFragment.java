package com.udacity.mauricio.popularmovies.gui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.BuildConfig;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.conn.ConnectionHandler;
import com.udacity.mauricio.popularmovies.gui.adapter.ReviewAdapter;
import com.udacity.mauricio.popularmovies.gui.adapter.VideoAdapter;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.PageReviewDTO;
import com.udacity.mauricio.popularmovies.models.VideoDTO;
import com.udacity.mauricio.popularmovies.models.VideoResponseDTO;
import com.udacity.mauricio.popularmovies.tasks.TheMovieDbTask;
import com.udacity.mauricio.popularmovies.utils.AppBarStateChangeListener;
import com.udacity.mauricio.popularmovies.utils.AppUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.udacity.mauricio.popularmovies.tasks.TheMovieDbTask.GET_REVIEWS_REQUEST_CODE;
import static com.udacity.mauricio.popularmovies.tasks.TheMovieDbTask.GET_VIDEOS_REQUEST_CODE;

@EFragment(R.layout.frag_detail_movie)
public class MovieDetailFragment extends Fragment implements ConnectionHandler, Callback {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @ViewById
    protected ImageView image;

    @ViewById
    protected TextView tvOriginalTitle, tvOverview, tvGender, tvReleaseDate, tvPopularity, tvVideos,
            tvReviews;

    @ViewById
    protected RatingBar rbMovieStars;

    @ViewById
    protected FloatingActionButton fbFavorite;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected CollapsingToolbarLayout collapsingToolbar;

    @ViewById(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @ViewById
    protected RecyclerView rvVideos;

    @ViewById
    protected RecyclerView rvReviews;

    @ViewById
    protected ProgressBar pbImage;

    @FragmentArg(DetailActivity.EXTRA_MOVIE)
    protected MovieDTO movie;

    @Bean
    protected TheMovieDbTask task;

    protected VideoAdapter videoAdapter;

    protected ReviewAdapter reviewAdapter;

    private String language;

    private boolean isMarkedAsFavorite;

    @AfterViews
    protected void init() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        if (movie != null) {

            language = AppUtils.getPreferenceValue(activity, getString(R.string.pref_language_key), getString(R.string.pref_language_default_value));

            fillMovieInfo(activity, movie);

            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            collapsingToolbar.setTitle(movie.title);

            configureTransactionNames();
            //configureToolbarColor();
        }

    }

    private void fillMovieInfo(Context context, MovieDTO movie) {
        Picasso.with(context).load(BuildConfig.BASE_URL_IMAGES + movie.posterPath).into(image, this);
        tvOriginalTitle.setText(movie.originalTitle);
        tvOverview.setText(movie.overview);
        tvPopularity.setText(String.format("%.2f%%", movie.popularity));
        tvReleaseDate.setText(movie.releaseDate);
        rbMovieStars.setRating(Double.valueOf(movie.voteAverage).intValue() / 2);
        //tvGender.setText(movie.overview);
        fbFavorite.setImageResource(isMarkedAsFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_empty);
        loadVideos();
        loadReviews();
    }

    private void loadVideos() {
        videoAdapter = new VideoAdapter(getContext(), view -> {
            int position = rvVideos.getChildLayoutPosition(view);
            VideoDTO video = videoAdapter.getItems().get(position);
            showVideo(video);
        });
        rvVideos.setAdapter(videoAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvVideos.setLayoutManager(linearLayoutManager);
        task.setHandler(GET_VIDEOS_REQUEST_CODE, this);
        task.getVideos(movie.remoteId, new HashMap<String, String>() {{put(TheMovieDbTask.LANGUAGE_PARAM, language);}});
    }

    private void loadReviews() {
        reviewAdapter = new ReviewAdapter(getContext());
        rvReviews.setAdapter(reviewAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvReviews.setLayoutManager(linearLayoutManager);

        Map<String, String> params = new HashMap<String, String>() {{
            put(TheMovieDbTask.LANGUAGE_PARAM, language);
            put(TheMovieDbTask.PAGE_PARAM, "1");
        }};

        task.setHandler(GET_REVIEWS_REQUEST_CODE, this);
        task.getReviews(movie.remoteId, params);
    }

    private void configureTransactionNames() {
        ViewCompat.setTransitionName(image, getString(R.string.movie_poster_trasition_name));
        ViewCompat.setTransitionName(tvOverview, getString(R.string.movie_overview_trasition_name));
    }

    private void configureToolbarColor() {
        if (image != null && image.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            Palette palette = Palette.from(bitmap).generate();
            int primaryColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
            final int toolbarColor = palette.getMutedColor(primaryColor);
            collapsingToolbar.setContentScrimColor(toolbarColor);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                    @Override
                    public void onStateChanged(AppBarLayout appBarLayout, State state) {
                        int statusBarColor = (state == AppBarStateChangeListener.State.COLLAPSED) ?
                                AppUtils.getDarketColor(toolbarColor) : Color.TRANSPARENT;
                        changeStatusBarColor(statusBarColor);
                    }

                });
            }

        }
    }

    @TargetApi(21)
    private void changeStatusBarColor(int statusBarColor) {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusBarColor);
    }

    @Click(R.id.fbFavorite)
    public void onFavoriteButtonClick() {
        isMarkedAsFavorite = !isMarkedAsFavorite;

        animateFab();

        String message = getString(isMarkedAsFavorite ? R.string.msg_movie_marked_as_favorite : R.string.msg_movie_unmarked_as_favorite);
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    private void animateFab() {
        Drawable source = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_empty);
        Drawable target = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_filled);
        Drawable backgrounds[] = new Drawable[2];
        backgrounds[0] = source;
        backgrounds[1] = target;

        TransitionDrawable crossfader = new TransitionDrawable(backgrounds);

        fbFavorite.setImageDrawable(crossfader);

        int duration = (int) TimeUnit.SECONDS.toMillis(1);

        if (isMarkedAsFavorite) {
            crossfader.startTransition(duration);
        } else {
            crossfader.startTransition(0);
            crossfader.reverseTransition(duration);
        }
    }

    private void showVideo(VideoDTO video) {
        String url = BuildConfig.YOUTUBE_URL.replace("#{KEY}", video.key);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onPreExecute(int requestCode) {}

    @Override
    public void onConnectionSucess(int requestCode, Object result) {
        if (isAdded()) {
            if (requestCode == GET_REVIEWS_REQUEST_CODE) {
                PageReviewDTO pageReview = (PageReviewDTO) result;

                if (pageReview == null || pageReview.reviews == null || pageReview.reviews.isEmpty())
                    return;

                reviewAdapter.setItems(pageReview.reviews);
                tvReviews.setVisibility(View.VISIBLE);
                rvReviews.setVisibility(View.VISIBLE);
            } else if (requestCode == GET_VIDEOS_REQUEST_CODE) {
                VideoResponseDTO videoResponse = (VideoResponseDTO) result;

                if (videoResponse == null || videoResponse.videos == null || videoResponse.videos.isEmpty())
                    return;

                videoAdapter.setItems(videoResponse.videos);
                tvVideos.setVisibility(View.VISIBLE);
                rvVideos.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onConnectionError(int requestCode, Exception e) {
        Log.e(LOG_TAG, e.getMessage());
    }

    @Override
    public void onSuccess() {
        configureToolbarColor();
        pbImage.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        image.setImageResource(R.drawable.ic_movie_error);
        pbImage.setVisibility(View.GONE);
    }
}
