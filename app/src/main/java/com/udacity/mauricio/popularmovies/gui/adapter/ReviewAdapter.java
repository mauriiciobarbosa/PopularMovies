package com.udacity.mauricio.popularmovies.gui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.mauricio.popularmovies.R;
import com.udacity.mauricio.popularmovies.models.ReviewDTO;

import java.util.List;

/**
 * Created by mauricio-MTM on 2/7/2017.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewDTO> items;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        ReviewDTO review = items.get(position);
        String author = context.getString(R.string.label_by) + " " + review.author;
        holder.author.setText(author);
        holder.content.setText(review.content);
        holder.link.setPaintFlags(holder.link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.link.setText(R.string.label_see_more);
        holder.link.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(review.url));
            context.startActivity(intent);
        });

    }

    public List<ReviewDTO> getItems() {
        return items;
    }

    public void setItems(List<ReviewDTO> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView author, content, link;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.tvAuthor);
            content = (TextView) itemView.findViewById(R.id.tvContent);
            link = (TextView) itemView.findViewById(R.id.tvLink);
        }

    }
}
