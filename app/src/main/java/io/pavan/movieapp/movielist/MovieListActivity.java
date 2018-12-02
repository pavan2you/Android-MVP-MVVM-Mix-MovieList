package io.pavan.movieapp.movielist;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.pavan.movieapp.R;
import io.pavan.movieapp.arch.mvp.MvpCompatActivity;
import io.pavan.movieapp.data.model.Movie;
import io.pavan.movieapp.movie.MovieActivity;

import static io.pavan.movieapp.movielist.MovieListContract.IMovieListPresenter;
import static io.pavan.movieapp.movielist.MovieListContract.IMovieListView;

/**
 * Created by pavan on 01/12/18
 *
 * A ZERO business condition free (no if's w.r.t business logic) View Implementation. The left over
 * conditions are Infra/platform specific.
 */
public class MovieListActivity extends MvpCompatActivity implements IMovieListView,
        View.OnClickListener {

    private IMovieListPresenter mThisPresenter;

    private RecyclerView mMovieListView;
    private MovieListAdapter mMovieListAdapter;
    private TextView mNoDataLabel;
    private ContentLoadingProgressBar mMovieListProgressBar;


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////  View Setup  ///////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreatePresenter(String presenterClassName, Bundle savedInstanceState) {
        super.onCreatePresenter(presenterClassName, savedInstanceState);
        mThisPresenter = (IMovieListPresenter) mPresenter;
    }

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);

        setContentView(R.layout.movie_list_view);
        setMovieListView();
    }

    private void setMovieListView() {
        mMovieListView = findViewById(R.id.movie_list);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        mMovieListAdapter = new MovieListAdapter(getLayoutInflater(), this);
        mMovieListView.setLayoutManager(llm);
        mMovieListView.setAdapter(mMovieListAdapter);

        mNoDataLabel = findViewById(R.id.movie_list_nda_text);
        mMovieListProgressBar = findViewById(R.id.movie_list_progress_bar);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  View Interaction  ////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.movie_list_item) {
            MovieListItemViewHolder vh = (MovieListItemViewHolder) v.getTag(
                    MovieListItemViewHolder.VH_TAG_ID);
            Movie model = vh.getTag();
            mThisPresenter.onMovieListItemClick(model);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////  View Moving Out  /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDestroy() {
        if (mMovieListAdapter != null) {
            mMovieListAdapter.unbind(mMovieListView);
        }

        mMovieListView = null;
        mMovieListAdapter = null;
        mThisPresenter = null;

        super.onDestroy();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////  Movie List View Contract  ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void showDataLoading() {
        mMovieListProgressBar.show();
    }

    @Override
    public void hideDataLoading() {
        mMovieListProgressBar.hide();
    }

    @Override
    public void setDataModel(List<Movie> modelList) {
        mMovieListAdapter.setDataModel(modelList);
    }

    @Override
    public void setNoDataAvailableMessage(String noDataMessage) {
        mNoDataLabel.setText(noDataMessage);
    }

    @Override
    public void showNoDataMessage() {
        mNoDataLabel.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoDataMessage() {
        mNoDataLabel.setVisibility(View.GONE);
    }

    @Override
    public void showDataList() {
        mMovieListView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDataList() {
        mMovieListView.setVisibility(View.INVISIBLE);
    }

    @Override
    public MovieListViewModel getMovieListViewModel() {
        return ViewModelProviders.of(this).get(MovieListViewModel.class);
    }

    @Override
    public LifecycleOwner getLifeCycleOwner() {
        return this;
    }

    @Override
    public void showErrorMessage(String message, boolean withRetryOption) {
        View contentView = findViewById(android.R.id.content);

        if (withRetryOption) {
            Snackbar.make(contentView, message, Snackbar.LENGTH_INDEFINITE).setAction(
                    getString(R.string.movie_list_retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mThisPresenter.onErrorRetryClick();
                        }
                    }).show();
        }
        else {
            Snackbar.make(contentView, message, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showMovieView(Movie movie) {
        Intent intent = new Intent(getApplicationContext(), MovieActivity.class);
        intent.putExtra(MovieActivity.EXTRA_MOVIE, movie);
        startActivity(intent);
    }

    @Override
    public void exitApplication() {
        /*
         * Soft exit
         */
        finish();
    }
}
