package io.pavan.movieapp.movielist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import io.pavan.movieapp.R;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 01/12/18
 */
class MovieListItemViewHolder extends RecyclerView.ViewHolder {

    static final int VH_TAG_ID = -999;

    private Movie mModel;

    private ImageView mPosterView;
    private TextView mTitleView;

    MovieListItemViewHolder(@NonNull View itemView) {
        super(itemView);
        onCreate(itemView);
    }

    private void onCreate(View view) {
        mPosterView = view.findViewById(R.id.movie_poster_image);
        mTitleView = view.findViewById(R.id.movie_title_text);
    }

    private void tagViewAndHolder(Movie movie) {
        setTag(movie);
        itemView.setTag(VH_TAG_ID, this);
    }

    private void setTag(Movie model) {
        mModel = model;
    }

    Movie getTag() {
        return mModel;
    }

    private String getString(int resId) {
        return itemView.getContext().getString(resId);
    }

    private void setPosterImage(String posterUrl) {
        try {
            Glide.with(mPosterView.getContext()).load(posterUrl).thumbnail(0.1f)
                    .placeholder(android.R.drawable.gallery_thumb).into(mPosterView);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPosterImage(int resId) {
        mPosterView.setImageResource(resId);
    }

    private void setTitle(String title) {
        mTitleView.setText(title);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////  DATA BINDER LOGIC  //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Ideally this logic should be view-free.
     *
     * Like how screens (activities / fragments) are having their own specific presenters, the
     * same can be applied at cell level or any complex layout / section.
     *
     * for more info please refer :
     *
     * https://github.com/pavan2you/HomeAwayPlaces/blob/master/app/src/main/java/com/homeaway/homeawayplaces/binders/PlaceListItemDataBinder.java
     *
     */
    void onBind(Movie movie) {
        if (movie == null) {
            return;
        }

        tagViewAndHolder(movie);

        String poster = movie.getPoster();
        if (isValidPosterUrl(movie, poster)) {
            setPosterImage(poster);
        }
        else {
            setPosterImage(android.R.drawable.gallery_thumb);
        }

        String title = movie.getTitle();
        if (isValidTitle(title)) {
            setTitle(title);
        }
        else {
            setTitle(getString(R.string.movie_info_no_title));
        }
    }

    private boolean isValidPosterUrl(Movie movie, String poster) {
        return poster != null && (poster.startsWith("http"));
    }

    private boolean isValidTitle(String title) {
        return title != null && !title.isEmpty();
    }
}
