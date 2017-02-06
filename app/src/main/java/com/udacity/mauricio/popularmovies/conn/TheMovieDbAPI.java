package com.udacity.mauricio.popularmovies.conn;

import com.udacity.mauricio.popularmovies.models.PageDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by mauricio on 06/02/17.
 */

public interface TheMovieDbAPI {

    @GET("discover/movie")
    Call<PageDTO> getMovies(@QueryMap Map<String, String> options);
}
