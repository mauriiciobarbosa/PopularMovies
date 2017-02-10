package com.udacity.mauricio.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.annimon.stream.Stream;
import com.udacity.mauricio.popularmovies.data.MovieContract.MovieEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.ReviewEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.VideoEntry;
import com.udacity.mauricio.popularmovies.models.MovieDTO;
import com.udacity.mauricio.popularmovies.models.ReviewDTO;
import com.udacity.mauricio.popularmovies.models.VideoDTO;
import com.udacity.mauricio.popularmovies.utils.AppUtils;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Created by mauricio on 09/02/17.
 */
@EBean(scope = EBean.Scope.Singleton)
public class MovieRepository {

    @RootContext
    protected Context context;

    private MovieDbHelper dbHelper;

    @AfterInject
    protected void init() {
        dbHelper = new MovieDbHelper(context);
    }

    public void saveMovie(MovieDTO movie) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues movieValues = AppUtils.getMovieValues(movie);
            db.insert(MovieEntry.TABLE_NAME, null, movieValues);

            if (movie.videos != null && movie.videos.size() > 0) {
                Stream.of(movie.videos)
                        .map(v -> {
                            ContentValues videoValues = new ContentValues();
                            videoValues.put(VideoEntry._ID, v.remoteId);
                            videoValues.put(VideoEntry.COLUMN_MOVIE_KEY, movie.remoteId);
                            videoValues.put(VideoEntry.COLUMN_KEY, v.key);
                            videoValues.put(VideoEntry.COLUMN_NAME, v.name);
                            return videoValues;
                        })
                        .forEach(cv -> db.insert(VideoEntry.TABLE_NAME, null, cv));
            }

            if (movie.reviews != null && movie.reviews.size() > 0) {
                Stream.of(movie.reviews)
                        .map(v -> {
                            ContentValues reviewValues = new ContentValues();
                            reviewValues.put(ReviewEntry._ID, v.remoteId);
                            reviewValues.put(ReviewEntry.COLUMN_MOVIE_KEY, movie.remoteId);
                            reviewValues.put(ReviewEntry.COLUMN_AUTHOR, v.author);
                            reviewValues.put(ReviewEntry.COLUMN_CONTENT, v.content);
                            reviewValues.put(ReviewEntry.COLUMN_URL, v.url);
                            return reviewValues;
                        })
                        .forEach(cv -> db.insert(ReviewEntry.TABLE_NAME, null, cv));
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public void removeMovie(MovieDTO movie) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.beginTransaction();

            db.delete(VideoEntry.TABLE_NAME,
                    VideoEntry.COLUMN_MOVIE_KEY + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });

            db.delete(ReviewEntry.TABLE_NAME,
                    ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });


            db.delete(MovieEntry.TABLE_NAME,
                    MovieEntry._ID + " = ?",
                    new String[] { String.valueOf(movie.remoteId) });

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public boolean hasMovie(MovieDTO movie) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(
                MovieEntry.TABLE_NAME,
                new String[] {MovieEntry._ID},
                MovieEntry._ID + " = ?",
                new String[] {String.valueOf(movie.remoteId)},
                null, null, null, "1").moveToNext();
    }

    public List<MovieDTO> getMovies() {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                MovieContract.MOVIE_COLUMNS,
                null,
                null,
                null,
                null,
                null,
                null);

        List<MovieDTO> movies = AppUtils.getMoviesFromCursor(cursor);

//        Stream.of(movies).forEach(m -> {
//            m.videos = getVideosFromMovie(m);
//            m.reviews = getReviewsFromMovie(m);
//        });

        return movies;
    }

    public List<ReviewDTO> getReviewsFromMovie(MovieDTO movie) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                ReviewEntry.TABLE_NAME,
                MovieContract.REVIEW_COLUMNS,
                ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[] { String.valueOf(movie.remoteId) },
                null,
                null,
                null,
                null);

        return AppUtils.getReviewsFromCursor(cursor);
    }

    public List<VideoDTO> getVideosFromMovie(MovieDTO movie) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                VideoEntry.TABLE_NAME,
                MovieContract.VIDEO_COLUMNS,
                VideoEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[] { String.valueOf(movie.remoteId) },
                null,
                null,
                null,
                null);

        return AppUtils.getVideosFromCursor(cursor);
    }

}
