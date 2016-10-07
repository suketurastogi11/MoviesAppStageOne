package com.example.suketurastogi.moviesappstageone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MovieListActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    //Array list in which all movies are fetched from server and saved.
    final ArrayList<MovieList> movieArrayList = new ArrayList<>();

    //Url to hit for Getting movies.
    String MovieListUrl;

    //Initializing server results to null.
    String resultMovieListServer = null;

    //GridView in which all the movies will be visible.
    GridView movieList;
    TextView noData;

    //Custom Adapter to show movies list.
    MovieListAdapter movieListAdapter;
    SharedPreferences prefs;
    //To Swipe And Refresh Whole movie List.
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list);

        movieList = (GridView) findViewById(R.id.movie_list_grid_view);

        movieListAdapter = new MovieListAdapter(MovieListActivity.this, movieArrayList);

        noData = (TextView) findViewById(R.id.no_data_text_view);
        movieList.setEmptyView(noData);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_movie_list_view);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();

                MovieListUrl = prefs.getString(getString(R.string.setting_menu_option_movie_sort_order_key),
                        getString(R.string.setting_menu_option_movie_sort_order_default_value));

                //Call this method to Check for Internet Connectivity to get the news list.
                generateMovieList();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    protected void onStart() {
        super.onStart();

        MovieListUrl = prefs.getString(getString(R.string.setting_menu_option_movie_sort_order_key),
                getString(R.string.setting_menu_option_movie_sort_order_default_value));

        generateMovieList();
    }

    public void generateMovieList() {

        if (isNetworkAvailable()) {
            // Perform the network request
            MovieAsyncTask task = new MovieAsyncTask();
            task.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection Available", Toast.LENGTH_LONG).show();
        }
    }

    //Method Returns Connectivity to Internet in Boolean Value.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onMovieItemSelection() {

        movieList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MovieList movie = movieArrayList.get(position);

                String imagePath = movie.getMovieImagePath();
                String title = movie.getMovieTitle();
                String synopsis = movie.getMovieSynopsis();
                String userRating = movie.getMoviRating();
                String releaseDate = movie.getMovieReleaseDate();

                Log.e("imagePath : ", "" + imagePath);
                Log.e("title : ", "" + title);
                Log.e("synopsis : ", "" + synopsis);
                Log.e("userRating : ", "" + userRating);
                Log.e("releaseDate : ", "" + releaseDate);

                Intent detailsIntent = new Intent(MovieListActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("imagePath", imagePath);
                detailsIntent.putExtra("title", title);
                detailsIntent.putExtra("synopsis", synopsis);
                detailsIntent.putExtra("userRating", userRating);
                detailsIntent.putExtra("releaseDate", releaseDate);
                startActivity(detailsIntent);
            }
        });
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
                Intent settingsIntent = new Intent(MovieListActivity.this, SettingActivity.class);
                startActivity(settingsIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MovieAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // Create URL object
            URL url = createUrl(MovieListUrl);

            Log.v("url", "" + url);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            resultMovieListServer = jsonResponse;

            Log.v("resultNewsListServer ", "" + resultMovieListServer);

            return resultMovieListServer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (resultMovieListServer == null) {
                Log.v("No Data ", "No Data");
                Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_LONG).show();

            } else {
                try {

                    movieArrayList.clear();

                    swipeContainer.setRefreshing(false);

                    String MOVIE_LIST_JSON_RESPONSE = resultMovieListServer;

                    // Parse the response given by the MOVIE_LIST_JSON_RESPONSE string
                    // and build up a list of Movie objects with the corresponding data.

                    JSONObject movieJsonResponse = new JSONObject(MOVIE_LIST_JSON_RESPONSE);

                    JSONArray movieResultsJsonArray = movieJsonResponse.getJSONArray("results");

                    for (int i = 0; i < movieResultsJsonArray.length(); i++) {

                        JSONObject currentItem = movieResultsJsonArray.getJSONObject(i);

                        String imagePath = currentItem.getString("poster_path");
                        String title = currentItem.getString("title");
                        String synopsis = currentItem.getString("overview");
                        String userRating = currentItem.getString("vote_average");
                        String releaseDate = currentItem.getString("release_date");

                        imagePath = getString(R.string.movie_list_image_path) + imagePath;

                        movieArrayList.add(new MovieList(imagePath, title, synopsis, userRating, releaseDate));

                        movieList.setAdapter(movieListAdapter);

                        onMovieItemSelection();

                    }

                } catch (JSONException e) {
                    // If an error is thrown when executing any of the above statements in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("Exception : ", "Problem parsing the newsList JSON results", e);
                }
            }
        }

        /**
         * Returns new URL object from the given string URL.
         */

        private URL createUrl(String stringUrl) {
            URL url;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                Log.v("ResponseCode : ", "" + urlConnection.getResponseCode());

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }

            } catch (IOException e) {
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
