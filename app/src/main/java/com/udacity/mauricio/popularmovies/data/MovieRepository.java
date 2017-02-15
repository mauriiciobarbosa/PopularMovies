package com.udacity.mauricio.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.udacity.mauricio.popularmovies.data.MovieContract.MovieEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.ReviewEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.VideoEntry;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.ReviewDTO;
import com.udacity.mauricio.popularmovies.models.VideoDTO;
import com.udacity.mauricio.popularmovies.utils.AppUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by mauricio on 09/02/17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MovieRepository {

    private static final String LOG_TAG = MovieRepository.class.getSimpleName();

    @RootContext
    protected Context context;

    public void saveMovie(MovieDTO movie) {
        try {
            ContentResolver contentResolver = context.getContentResolver();

            ContentValues movieValues = AppUtils.getMovieValues(movie);
            contentResolver.insert(MovieEntry.CONTENT_URI, movieValues);

            if (movie.videos != null && movie.videos.size() > 0) {
                ContentValues[] videoValues = AppUtils.getVideoValues(movie.videos, movie.remoteId);
                contentResolver.bulkInsert(VideoEntry.CONTENT_URI, videoValues);
            }

            if (movie.reviews != null && movie.reviews.size() > 0) {
                ContentValues[] reviewValues = AppUtils.getReviewValues(movie.reviews, movie.remoteId);
                contentResolver.bulkInsert(ReviewEntry.CONTENT_URI, reviewValues);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public void removeMovie(MovieDTO movie) {
        try {

            ContentResolver contentResolver = context.getContentResolver();

            contentResolver.delete(VideoEntry.CONTENT_URI,
                    VideoEntry.COLUMN_MOVIE_KEY + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });

            contentResolver.delete(ReviewEntry.CONTENT_URI,
                    ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });

            contentResolver.delete(MovieEntry.CONTENT_URI,
                    MovieEntry._ID + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean hasMovie(MovieDTO movie) {
        return context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[] {MovieEntry._ID},
                MovieEntry._ID + " = ?",
                new String[] { String.valueOf(movie.remoteId) },
                null).moveToNext();
    }

    public List<MovieDTO> getMovies() {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MOVIE_COLUMNS,
                null,
                null,
                null);

        return AppUtils.getMoviesFromCursor(cursor);
    }

    public List<ReviewDTO> getReviewsFromMovie(MovieDTO movie) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                MovieContract.REVIEW_COLUMNS,
                ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[] { String.valueOf(movie.remoteId) },
                null);

        return AppUtils.getReviewsFromCursor(cursor);
    }

    public List<VideoDTO> getVideosFromMovie(MovieDTO movie) {
        Cursor cursor = context.getContentResolver().query(
                MovieContract.VideoEntry.CONTENT_URI,
                MovieContract.VIDEO_COLUMNS,
                VideoEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[] { String.valueOf(movie.remoteId) },
                null);

        return AppUtils.getVideosFromCursor(cursor);
    }

}
