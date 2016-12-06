package com.udacity.mauricio.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mauricio-MTM on 12/6/2016.
 */
public class MovieDTO implements Serializable {

    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("original_title")
    @Expose
    public String originalTitle;

    @SerializedName("original_language")
    @Expose
    public String originalLanguage;

    @SerializedName("popularity")
    @Expose
    public Double popularity;

    @SerializedName("poster_path")
    @Expose
    public String posterPath;

    @SerializedName("overview")
    @Expose
    public String overview;

    @SerializedName("release_date")
    @Expose
    public String releaseDate;

    @SerializedName("vote_average")
    @Expose
    public Double voteAverage;

}
