package com.udacity.mauricio.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mauricio on 06/02/17.
 */

public class VideoResponseDTO implements Serializable {

    @SerializedName("results")
    @Expose
    public List<VideoDTO> videos;
}
