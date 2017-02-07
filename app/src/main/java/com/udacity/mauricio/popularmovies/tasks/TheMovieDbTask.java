package com.udacity.mauricio.popularmovies.tasks;

import com.udacity.mauricio.popularmovies.BuildConfig;
import com.udacity.mauricio.popularmovies.conn.ConnectionHandler;
import com.udacity.mauricio.popularmovies.conn.TheMovieDbAPI;
import com.udacity.mauricio.popularmovies.models.PageDTO;
import com.udacity.mauricio.popularmovies.models.PageReviewDTO;
import com.udacity.mauricio.popularmovies.models.VideoResponseDTO;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mauricio on 06/02/17.
 */
@EBean
public class TheMovieDbTask {

    private static final String KEY_PARAM = "api_key";
    private static final String LANGUAGE_PARAM = "language";
    private static final String SORT_PARAM = "sort_by";
    private static final String PAGE_PARAM = "page";

    public static final int LANGUAGE_PARAM_POSITION = 0;
    public static final int SORT_PARAM_POSITION = 1;
    public static final int PAGE_PARAM_POSITION = 2;
    public static final int PARAM_NUMBER = 3;

    protected ConnectionHandler handler;

    protected int requestCode;

    private TheMovieDbAPI theMovieDbApi;

    private void config() {
        theMovieDbApi = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TheMovieDbAPI.class);
    }

    public void setHandler(int requestCode, ConnectionHandler handler) {

        if (requestCode <= 0 || handler == null)
            throw new InvalidParameterException();

        this.requestCode = requestCode;
        this.handler = handler;
    }

    /**
     * This service's method will returns a PageDTO.
     *
     * @param params query parameters.
     */
    @Background
    public void getMovies(String... params) {

        if (params.length < PARAM_NUMBER)
            throw new InvalidParameterException("This task must be executed with "
                    + PARAM_NUMBER + " parameters: language, sortBy and page number.");

        onPreExecute(requestCode);

        String language = params[LANGUAGE_PARAM_POSITION];
        String sortBy = params[SORT_PARAM_POSITION];
        String page = params[PAGE_PARAM_POSITION];

        config();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(LANGUAGE_PARAM, language);
        queryParams.put(SORT_PARAM, sortBy);
        queryParams.put(PAGE_PARAM, page);
        queryParams.put(KEY_PARAM, BuildConfig.API_KEY);

        try {
            PageDTO result = theMovieDbApi.getMovies(queryParams).execute().body();
            onConnectionSuccess(requestCode, result);
        } catch (Exception e) {
            onConnectionError(requestCode, e);
        }

    }

    /**
     * This service's method will returns a PageReviewDTO.
     *
     * @param params query parameters.
     */
    @Background
    public void getReviews(int movieId, Map<String, String> params) {
        if (params == null || !params.containsKey(PAGE_PARAM))
            throw new InvalidParameterException("This task must be executed with at least "
                    + PAGE_PARAM + " parameter.");

        onPreExecute(requestCode);

        config();

        params.put(KEY_PARAM, BuildConfig.API_KEY);

        try {
            PageReviewDTO result = theMovieDbApi.getReviews(movieId, params).execute().body();
            onConnectionSuccess(requestCode, result);
        } catch (Exception e) {
            onConnectionError(requestCode, e);
        }
    }

    /**
     * This service's method will returns a VideoResponseDTO.
     *
     * @param params query parameters.
     */
    @Background
    public void getVideos(int movieId, Map<String, String> params) {
        onPreExecute(requestCode);

        config();

        if (params == null)
            params = new HashMap<>();

        params.put(KEY_PARAM, BuildConfig.API_KEY);

        try {
            VideoResponseDTO result = theMovieDbApi.getVideos(movieId, params).execute().body();
            onConnectionSuccess(requestCode, result);
        } catch (Exception e) {
            onConnectionError(requestCode, e);
        }
    }

    @UiThread
    protected void onPreExecute(int requestCode) {
        handler.onPreExecute(requestCode);
    }

    @UiThread
    protected void onConnectionSuccess(int requestCode, Object result) {
        handler.onConnectionSucess(requestCode, result);
    }

    @UiThread
    protected void onConnectionError(int requestCode, Exception error) {
        handler.onConnectionError(requestCode, error);
    }

}
