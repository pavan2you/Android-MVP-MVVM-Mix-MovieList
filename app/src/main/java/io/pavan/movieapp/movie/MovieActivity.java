package io.pavan.movieapp.movie;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import io.pavan.movieapp.R;
import io.pavan.movieapp.arch.mvp.MvpCompatActivity;
import io.pavan.movieapp.data.model.Movie;

public class MovieActivity extends MvpCompatActivity implements MovieContract.IMovieView {

    public static final String EXTRA_MOVIE = "io.pavan.movielist.extra.MOVIE";

    private MovieContract.IMoviePresenter mThisPresenter;

    private ImageView mPosterView;
    private LinearLayout mPropertyListView;

    @Override
    protected void onCreatePresenter(String presenterClassName, Bundle savedInstanceState) {
        super.onCreatePresenter(presenterClassName, savedInstanceState);
        mThisPresenter = (MovieContract.IMoviePresenter) mPresenter;

        Movie movie = (Movie) getIntent().getSerializableExtra(EXTRA_MOVIE);
        mThisPresenter.loadWith(movie);
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.movie_view);
        setPropertyListView();
    }

    private void setPropertyListView() {
        mPosterView = findViewById(R.id.movie_poster_image);
        mPropertyListView = findViewById(R.id.movie_info_list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mThisPresenter.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mThisPresenter = null;
        super.onDestroy();
    }

    @Override
    public void setTitleWith(String title) {
        setTitle(title);
    }

    @Override
    public void setPosterImage(int resId) {
        mPosterView.setImageResource(resId);
    }

    @Override
    public void setPosterImage(String posterUrl) {
        try {
            Glide.with(mPosterView.getContext()).load(posterUrl).thumbnail(0.1f)
                    .placeholder(android.R.drawable.gallery_thumb).into(mPosterView);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDataModel(List<Pair<String, String>> dataModel) {
        /*
         * As it is a finite set of properties and very few in size-wise, opted layout over
         * adapter based lists.
         */
        mPropertyListView.removeAllViews();

        LayoutInflater inflater = getLayoutInflater();

        for (Pair<String, String> movieProperty : dataModel) {
            addMoviePropertyItemView(movieProperty, inflater);
        }
    }

    private void addMoviePropertyItemView(Pair<String, String> property, LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.movie_info_list_item_view, mPropertyListView,
                false);
        mPropertyListView.addView(view);

        TextView propertyName = view.findViewById(R.id.movie_property_name);
        TextView propertyValue = view.findViewById(R.id.movie_property_value);

        propertyName.setText(property.first);
        propertyValue.setText(property.second);
    }

    @Override
    public void showCallerView() {
        finish();
    }
}
