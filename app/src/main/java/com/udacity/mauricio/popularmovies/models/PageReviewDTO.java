package com.udacity.mauricio.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mauricio on 06/02/17.
 */

public class PageReviewDTO implements Serializable {

    @SerializedName("page")
    @Expose
    public Integer page;

    @SerializedName("results")
    @Expose
    public List<ReviewDTO> reviews;

    @SerializedName("total_results")
    @Expose
    public Integer totalResults;

    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;


}
