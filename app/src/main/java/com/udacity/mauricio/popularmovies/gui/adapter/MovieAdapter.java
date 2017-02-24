package com.udacity.mauricio.popularmovies.gui.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.BuildConfig;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.MovieDTO;

import java.util.ArrayList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_2, parent, false);
        view.setOnClickListener(listener);
        return new MovieViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieDTO movie = movies.get(position);
        String thumb = BuildConfig.BASE_URL_IMAGES + movie.posterPath;
        changeProgressBarVisibility(holder.progress, View.VISIBLE);
        Picasso.with(context).load(thumb).into(holder.poster, new Callback() {
            @Override
            public void onSuccess() {
                changeProgressBarVisibility(holder.progress, View.GONE);
            }
            @Override
            public void onError() {
                holder.poster.setImageResource(R.drawable.ic_movie_error);
                changeProgressBarVisibility(holder.progress, View.GONE);
            }
        });
        holder.title.setText(movie.title);
        //holder.overview.setText(movie.overview);
    }

    public void changeProgressBarVisibility(ProgressBar progress, int visibility) {
        //if (progress != null) progress.setVisibility(visibility);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public List<MovieDTO> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieDTO> newMovies) {
        if (movies == null)
            movies = new ArrayList<>(newMovies.size());
        int start = movies.size() - 1;
        movies.addAll(newMovies);
        notifyItemRangeInserted(start, newMovies.size());
    }

    public void clear() {
        if (movies != null)
            movies.clear();
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title;
        TextView overview;
        protected ProgressBar progress;

        MovieViewHolder(Context context, View itemView) {
            super(itemView);
            progress = (ProgressBar) itemView.findViewById(R.id.pbImage);
            poster = (ImageView) itemView.findViewById(R.id.ivPoster);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
            overview = (TextView) itemView.findViewById(R.id.tvDescription);
            ViewCompat.setTransitionName(poster, context.getString(R.string.movie_poster_trasition_name));
//            ViewCompat.setTransitionName(overview, context.getString(R.string.movie_overview_trasition_name));
        }
    }

}
