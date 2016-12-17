package com.udacity.mauricio.popularmovies.tasks;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.PageDTO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;

/**
 * Created by mauricio-MTM on 12/6/2016.
 */
public class LoadMovieTask extends AsyncTask<String, Void, PageDTO> {

    private static final String LOG_TAG = LoadMovieTask.class.getSimpleName();
    private static final String KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String SORT_PARAM = "sort_by";
    private static final String PAGE_PARAM = "page";

    public static final int LANGUAGE_PARAM_POSITION = 0;
    public static final int SORT_PARAM_POSITION = 1;
    public static final int PAGE_PARAM_POSITION = 2;
    public static final int PARAM_NUMBER = 3;

    private LoadMovieListener listener;
    private Context context;

    public LoadMovieTask(Context context, LoadMovieListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onPreExecute();
    }

    @Override
    protected PageDTO doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        PageDTO result = null;

        if (params.length < PARAM_NUMBER)
            throw new InvalidParameterException("This task must be executed with " + PARAM_NUMBER + " parameters: language, sortBy and page number.");

        String language = params[LANGUAGE_PARAM_POSITION];
        String sortBy = params[SORT_PARAM_POSITION];
        String page = params[PAGE_PARAM_POSITION];

        Uri builtUri = Uri.parse(context.getString(R.string.baseUrl)).buildUpon()
                .appendQueryParameter(LANGUAGE_PARAM, language)
                .appendQueryParameter(SORT_PARAM, sortBy)
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(KEY_PARAM, context.getString(R.string.api_key))
                .build();

        Log.i(LOG_TAG, "Uri: " + builtUri.toString());

        try {
            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() > 0) {
                    String moviesJson = buffer.toString();
                    result = new Gson().fromJson(moviesJson, PageDTO.class);
                }

            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(PageDTO pageDTO) {
        listener.onLoadMovie(pageDTO);
    }

    public interface LoadMovieListener {
        void onPreExecute();
        void onLoadMovie(PageDTO page);
    }
}
