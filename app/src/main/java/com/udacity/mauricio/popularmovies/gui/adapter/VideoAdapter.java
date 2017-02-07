package com.udacity.mauricio.popularmovies.gui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.mauricio.popularmovies.BuildConfig;
import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.VideoDTO;

import java.util.List;

/**
 * Created by mauricio-MTM on 2/7/2017.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoDTO> items;
    private Context context;
    private View.OnClickListener listener;

    public VideoAdapter(Context context, View.OnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        view.setOnClickListener(listener);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoDTO video = items.get(position);
        String thumb =  BuildConfig.YOUTUBE_URL_IMAGES.replace("#{KEY}", video.key);
        Picasso.with(context).load(thumb).into(holder.video);
        holder.title.setText(video.name);
    }

    public List<VideoDTO> getItems() {
        return items;
    }

    public void setItems(List<VideoDTO> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView video;
        TextView title;

        public VideoViewHolder(View itemView) {
            super(itemView);
            video = (ImageView) itemView.findViewById(R.id.ivVideo);
            title = (TextView) itemView.findViewById(R.id.tvTitle);
        }

    }
}
