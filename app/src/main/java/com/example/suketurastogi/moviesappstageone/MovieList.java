package com.example.suketurastogi.moviesappstageone;

public class MovieList {

    private String mMovieImagePath;
    private String mMovieTitle;
    private String mMovieSynopsis;
    private String mMovieRating;
    private String mMovieReleaseDate;

    public MovieList(String movieImagePath, String movieTitle, String movieSynopsis, String movieRating, String movieReleaseDate) {

        mMovieImagePath = movieImagePath;
        mMovieTitle = movieTitle;
        mMovieSynopsis = movieSynopsis;
        mMovieRating = movieRating;
        mMovieReleaseDate = movieReleaseDate;
    }

    public String getMovieImagePath() {
        return mMovieImagePath;
    }

    public String getMovieTitle() {
        return mMovieTitle;
    }

    public String getMovieSynopsis() {
        return mMovieSynopsis;
    }

    public String getMoviRating() {
        return mMovieRating;
    }

    public String getMovieReleaseDate() {
        return mMovieReleaseDate;
    }
}
