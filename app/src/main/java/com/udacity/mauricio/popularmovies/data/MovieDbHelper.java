package com.udacity.mauricio.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.mauricio.popularmovies.data.MovieContract.MovieEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.ReviewEntry;
import com.udacity.mauricio.popularmovies.data.MovieContract.VideoEntry;


/**
 * Created by mauricio-MTM on 2/9/2017.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final StringBuilder SQL_CREATE_TABLE_MOVIE = new StringBuilder()
                .append("CREATE TABLE " + MovieEntry.TABLE_NAME + " (")
                .append(MovieEntry._ID + " INTEGER PRIMARY KEY, ")
                .append(MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, ")
                .append(MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, ")
                .append(MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, ")
                .append(MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, ")
                .append(MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, ")
                .append(MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, ")
                .append(MovieEntry.COLUMN_IS_FAVORITE + " INTEGER NOT NULL, ")
                .append(MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL); ");

        final StringBuilder SQL_CREATE_TABLE_VIDEO = new StringBuilder()
                .append("CREATE TABLE " + VideoEntry.TABLE_NAME + " (")
                .append(VideoEntry._ID + " TEXT PRIMARY KEY, ")
                .append(VideoEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, ")
                .append(VideoEntry.COLUMN_KEY + " TEXT NOT NULL, ")
                .append(VideoEntry.COLUMN_NAME + " TEXT NOT NULL, ")
                .append("FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_KEY + ") ")
                .append("REFERENCES " + MovieEntry.TABLE_NAME + "( " + MovieEntry._ID + "));");

        final StringBuilder SQL_CREATE_TABLE_REVIEW = new StringBuilder()
                .append("CREATE TABLE " + ReviewEntry.TABLE_NAME + " (")
                .append(ReviewEntry._ID + " TEXT PRIMARY KEY, ")
                .append(ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, ")
                .append(ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, ")
                .append(ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, ")
                .append(ReviewEntry.COLUMN_URL + " TEXT NOT NULL, ")
                .append("FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") ")
                .append("REFERENCES " + MovieEntry.TABLE_NAME + "( " + MovieEntry._ID + "));");

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_MOVIE.toString());
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_VIDEO.toString());
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_REVIEW.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
    }

}
