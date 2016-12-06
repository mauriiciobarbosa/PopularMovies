package com.udacity.mauricio.popularmovies.gui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;

import java.util.List;

/**
 * Created by mauricio-MTM on 12/6/2016.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<MovieDTO> movies;
    private Context context;

    public MovieAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(viewRoot);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieDTO movie = movies.get(position);
        String thumb = context.getString(R.string.baseUrl_image) + movie.posterPath;
        Picasso.with(context).load(thumb).into(holder.poster);
        holder.title.setText(movie.originalTitle);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public List<MovieDTO> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieDTO> movies) {
        this.movies = movies;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title;

        MovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.ivPoster);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
