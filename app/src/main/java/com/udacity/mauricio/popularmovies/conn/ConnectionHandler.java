package com.udacity.mauricio.popularmovies.conn;

/**
 * Created by mauricio on 06/02/17.
 */

public interface ConnectionHandler {

    void onPreExecute(int requestCode);

    void onConnectionSucess(int requestCode, Object Result);

    void onConnectionError(int requestCode, Exception e);

}
