package com.udacity.mauricio.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mauricio on 06/02/17.
 */

public class ReviewDTO implements Serializable {

    @SerializedName("id")
    @Expose
    public String remoteId;

    @Expose
    public String author;

    @Expose
    public String content;

    @Expose
    public String url;

}
