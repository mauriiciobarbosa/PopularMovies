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

/**
 * Created by mauricio-MTM on 12/6/2016.
 */

public class LoadMovieTask extends AsyncTask<String, Void, PageDTO> {

    private static final String LOG_TAG = LoadMovieTask.class.getSimpleName();
    private static final String KEY_PARAM = "api_key";
    private static final String PAGE_PARAM = "page";

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

        String page = params[0];

        Uri builtUri = Uri.parse(context.getString(R.string.baseUrl)).buildUpon()
                .appendQueryParameter(PAGE_PARAM, page)
                .appendQueryParameter(KEY_PARAM, context.getString(R.string.api_key))
                .build();

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
