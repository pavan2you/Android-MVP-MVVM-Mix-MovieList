package io.pavan.movieapp.movielist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.List;

import io.pavan.movieapp.R;
import io.pavan.movieapp.data.model.Movie;

/**
 * Created by pavan on 01/12/18
 */
class MovieListAdapter extends RecyclerView.Adapter<MovieListItemViewHolder> {

    private LayoutInflater mInflater;
    private WeakReference<View.OnClickListener> mClickListenerWeakReference;

    private List<Movie> mModelList;

    public MovieListAdapter(LayoutInflater inflater, View.OnClickListener clickListener) {
        mInflater = inflater;
        mClickListenerWeakReference = new WeakReference<>(clickListener);
    }

    public void setDataModel(List<Movie> modelList) {
        mModelList = modelList;
        notifyDataSetChanged();
    }

    private Movie getItem(int position) {
        return mModelList.get(position);
    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    @NonNull
    @Override
    public MovieListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.movie_list_item_view, null, false);
        view.setOnClickListener(mClickListenerWeakReference.get());

        return new MovieListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListItemViewHolder viewHolder, int position) {
        viewHolder.onBind(getItem(position));
    }

    void unbind(RecyclerView list) {
        for (int i = 0; i < getItemCount(); i++) {
            View view = list.getChildAt(i);
            if (view != null) {
                view.setOnClickListener(null);
            }
        }

        list.setAdapter(null);
        list.setLayoutManager(null);
    }
}
