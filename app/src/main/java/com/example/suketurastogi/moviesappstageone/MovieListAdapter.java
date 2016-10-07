package com.example.suketurastogi.moviesappstageone;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends ArrayAdapter<MovieList> {

    public MovieListAdapter(Activity context, ArrayList<MovieList> movie) {
        super(context, 0, movie);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_list_item, parent, false);
        }

        MovieList currentMovie = getItem(position);

        ImageView movieImage = (ImageView) listItemView.findViewById(R.id.movie_list_item_image_view);
        movieImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (currentMovie != null) {
            Picasso.with(getContext()).load(currentMovie.getMovieImagePath()).into(movieImage);
        }

        return listItemView;
    }
}
