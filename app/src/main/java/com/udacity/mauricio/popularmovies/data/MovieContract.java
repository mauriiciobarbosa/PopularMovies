package com.udacity.mauricio.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mauricio-MTM on 2/9/2017.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.udacity.mauricio.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_VIDEO = "video";
    public static final String PATH_REVIEW = "review";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VideoEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEO).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEO;

        public static final String TABLE_NAME = "video";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
    }

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
    }

    public static final String[] MOVIE_COLUMNS = {
        MovieEntry._ID,
        MovieEntry.COLUMN_TITLE,
        MovieEntry.COLUMN_ORIGINAL_TITLE,
        MovieEntry.COLUMN_POPULARITY,
        MovieEntry.COLUMN_OVERVIEW,
        MovieEntry.COLUMN_RELEASE_DATE,
        MovieEntry.COLUMN_VOTE_AVERAGE,
        MovieEntry.COLUMN_POSTER_PATH,
        MovieEntry.COLUMN_IS_FAVORITE
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_ORIGINAL_TITLE = 2;
    public static final int COL_MOVIE_POPULARITY = 3;
    public static final int COL_MOVIE_OVERVIEW = 4;
    public static final int COL_MOVIE_RELEASE_DATE = 5;
    public static final int COL_MOVIE_VOTE_AVERAGE = 6;
    public static final int COL_MOVIE_POSTER_PATH = 7;
    public static final int COL_MOVIE_IS_FAVORITE = 8;

    public static final String[] VIDEO_COLUMNS = {
            VideoEntry._ID,
            VideoEntry.COLUMN_MOVIE_KEY,
            VideoEntry.COLUMN_KEY,
            VideoEntry.COLUMN_NAME
    };

    public static final int COL_VIDEO_ID = 0;
    public static final int COL_VIDEO_MOVIE_KEY = 1;
    public static final int COL_VIDEO_KEY = 2;
    public static final int COL_VIDEO_NAME = 3;

    public static final String[] REVIEW_COLUMNS = {
            ReviewEntry._ID,
            ReviewEntry.COLUMN_MOVIE_KEY,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_URL
    };

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_MOVIE_KEY = 1;
    public static final int COL_REVIEW_AUTHOR = 2;
    public static final int COL_REVIEW_CONTENT = 3;
    public static final int COL_REVIEW_URL = 4;

}
