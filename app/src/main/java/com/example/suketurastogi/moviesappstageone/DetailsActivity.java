package com.example.suketurastogi.moviesappstageone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String title = intent.getStringExtra("title");
        String synopsis = intent.getStringExtra("synopsis");
        String userRating = intent.getStringExtra("userRating");
        String releaseDate = intent.getStringExtra("releaseDate");

        userRating = "User rating : " + userRating;
        releaseDate = "Release Date : " + releaseDate;

        Log.e("imagePath : ", "" + imagePath);
        Log.e("title : ", "" + title);
        Log.e("synopsis : ", "" + synopsis);
        Log.e("userRating : ", "" + userRating);
        Log.e("releaseDate : ", "" + releaseDate);

        ImageView movieImage = (ImageView)findViewById(R.id.movie_image_view_thumbnail);
        TextView movieTitle = (TextView)findViewById(R.id.movie_title);
        TextView movieSynopsis = (TextView)findViewById(R.id.movie_synopsis_content);
        TextView movieUserRating = (TextView)findViewById(R.id.movie_rating_content);
        TextView movieReleaseDate = (TextView)findViewById(R.id.movie_release_date_content);

        Picasso.with(getApplicationContext()).load(imagePath).into(movieImage);
        movieTitle.setText(title);
        movieSynopsis.setText(synopsis);
        movieUserRating.setText(userRating);
        movieReleaseDate.setText(releaseDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings_menu_icon:
                Intent settingsIntent = new Intent(DetailsActivity.this, SettingActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
