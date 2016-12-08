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
    private View.OnClickListener listener;

    public MovieAdapter(Context context, View.OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        view.setOnClickListener(listener);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieDTO movie = movies.get(position);
        String thumb = context.getString(R.string.baseUrl_image) + movie.posterPath;
        Picasso.with(context).load(thumb).into(holder.poster);
        holder.title.setText(movie.originalTitle);
        holder.overview.setText(movie.overview);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
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
        TextView overview;

        MovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.ivPoster);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            overview = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }

}
