package com.udacity.mauricio.popularmovies.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by mauricio on 06/02/17.
 */

public class VideoDTO implements Serializable {

    @Expose
    public String id;

    @Expose
    public String name;

    @Expose
    public int size;

    @Expose
    public String site;

    @Expose
    public String type;

}
