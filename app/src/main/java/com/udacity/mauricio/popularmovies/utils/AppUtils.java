package com.udacity.mauricio.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.udacity.mauricio.popularmovies.data.MovieContract;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.ReviewDTO;
import com.udacity.mauricio.popularmovies.models.VideoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mauricio-MTM on 12/6/2016.
 */
public final class AppUtils {

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getDarketColor(int toolbarColor) {
        float[] hsv = new float[3];
        int color =toolbarColor;
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        color = Color.HSVToColor(hsv);
        return color;
    }

    public static void setLocale(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static String getPreferenceValue(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static ContentValues getMovieValues(MovieDTO movie) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, movie.remoteId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.title);
        movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.originalTitle);
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, movie.popularity);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.voteAverage);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.posterPath);
        movieValues.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITE, movie.isFavorite);
        return movieValues;
    }

    public static List<MovieDTO> getMoviesFromCursor(Cursor cursor) {
        List<MovieDTO> movies = new ArrayList<>();

        while (cursor.moveToNext()) {
            MovieDTO movie = new MovieDTO();
            movie.remoteId = cursor.getInt(MovieContract.COL_MOVIE_ID);
            movie.title = cursor.getString(MovieContract.COL_MOVIE_TITLE);
            movie.originalTitle = cursor.getString(MovieContract.COL_MOVIE_ORIGINAL_TITLE);
            movie.popularity = cursor.getDouble(MovieContract.COL_MOVIE_POPULARITY);
            movie.overview = cursor.getString(MovieContract.COL_MOVIE_OVERVIEW);
            movie.releaseDate = cursor.getString(MovieContract.COL_MOVIE_RELEASE_DATE);
            movie.voteAverage = cursor.getDouble(MovieContract.COL_MOVIE_VOTE_AVERAGE);
            movie.posterPath = cursor.getString(MovieContract.COL_MOVIE_POSTER_PATH);
            movie.isFavorite = cursor.getInt(MovieContract.COL_MOVIE_IS_FAVORITE) == 1;
            movies.add(movie);
        }

        cursor.close();

        return movies;
    }

    public static List<ReviewDTO> getReviewsFromCursor(Cursor cursor) {
        List<ReviewDTO> reviews = new ArrayList<>();

        while (cursor.moveToNext()) {
            ReviewDTO review = new ReviewDTO();
            review.remoteId = cursor.getString(MovieContract.COL_REVIEW_ID);
            review.author = cursor.getString(MovieContract.COL_REVIEW_AUTHOR);
            review.content = cursor.getString(MovieContract.COL_REVIEW_CONTENT);
            review.url = cursor.getString(MovieContract.COL_REVIEW_URL);
            reviews.add(review);
        }

        cursor.close();

        return reviews;
    }

    public static List<VideoDTO> getVideosFromCursor(Cursor cursor) {
        List<VideoDTO> videos = new ArrayList<>();

        while (cursor.moveToNext()) {
            VideoDTO video = new VideoDTO();
            video.remoteId = cursor.getString(MovieContract.COL_VIDEO_ID);
            video.key = cursor.getString(MovieContract.COL_VIDEO_KEY);
            video.name = cursor.getString(MovieContract.COL_VIDEO_NAME);
            videos.add(video);
        }

        cursor.close();

        return videos;
    }
}
