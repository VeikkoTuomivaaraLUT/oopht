package OOP.oopht;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;


import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class MainActivity extends AppCompatActivity {


    TheaterManager theaterManager;
    MovieManager movieManager;
    ArrayList<Theater> theaterList;
    ArrayList<String> theaterNamesList;
    ArrayList<String> movieNamesList;
    ArrayList<Movie> moviesList;
    ArrayAdapter<String> theatersAdapter;
    ArrayAdapter<String> movieNamesAdapter;
    Spinner spinnerTheater;
    ListView listViewMovies;
    AlertDialog.Builder alertDialogBuilderMovieList;
    EditText ratingText;
    RatingBar ratingBar;
    Button buttonResetRatings;
    Button buttonShowRatingRanking;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieManager = MovieManager.getInstance();

        try {
            theaterManager = new TheaterManager();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            Log.e("WHAT", "Something went wrong: " + e);
            e.printStackTrace();
        }

        try {
            theaterNamesList = theaterManager.getTheaterNamesList();
        } catch (NullPointerException e) {
            Log.e("NPE", "WHAT 1");
        }
        try {
            theaterList = theaterManager.getTheaterList();
        } catch (NullPointerException e) {
            Log.e("NPE", "WHAT 2");
        }


        buttonShowRatingRanking = findViewById(R.id.buttonShowRatingRanking);
        buttonResetRatings = findViewById(R.id.buttonResestRatings);
        spinnerTheater = findViewById(R.id.spinnerTheater);
        listViewMovies = findViewById(R.id.listViewMovies);
        movieNamesList = new ArrayList<>();
        moviesList = new ArrayList<>();
        alertDialogBuilderMovieList = new AlertDialog.Builder(this);
        ratingBar = findViewById(R.id.ratingBar);
        ratingText = findViewById(R.id.ratingText);





        theatersAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, theaterNamesList);
        theatersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        try {
            spinnerTheater.setAdapter(theatersAdapter);
        } catch (NullPointerException e) {
            Log.e("NPE", "WHAT 3");
        }

        movieNamesAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, movieNamesList);

        try {
            listViewMovies.setAdapter(movieNamesAdapter);
        } catch (NullPointerException e) {
            Log.e("NPE", "WHAT 4");
        }


        setDefaultTexts();


        // updates movie list based on selected location
        spinnerTheater.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                theaterManager.setTheaterChoice(i);
                movieNamesList = movieManager.getMovieNamesList(theaterList.get(i));
                moviesList = movieManager.getMovieList(theaterList.get(i));
                movieNamesAdapter.addAll(movieNamesList);
                movieNamesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // shows some basic info about movie, as well as own rating
        // more info easily added if needed (and only if)
        listViewMovies.setOnItemClickListener((adapterView, view, i, l) -> {
            ArrayList<Movie> ratedMovies = null;
            try {
                ratedMovies = movieManager.getMoviesFromDatabase();
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
                Log.e("Unspecified error", "Error: Something went wrong while getting movies from database.");
            }
            movieManager.setMovieChoice(i);

            String movieInfo;
            Movie movie = moviesList.get(movieManager.getMovieChoice());
            String movieTitle = movie.getName();
            if (ratedMovies != null) {
                for (Movie movie_ : ratedMovies) {
                    if (movieTitle.equals(movie_.getName())) {
                        movie = movie_;
                        break;
                    }
                }
            }

            String movieName = movie.getName();
            String movieNameOrig = movie.getNameOriginal();
            if (!movieName.equals(movieNameOrig)) {
                movieNameOrig = "(Orig: " + movieNameOrig + ")";
            } else {
                movieNameOrig = "";
            }
            String movieWatchTime = movie.getWatchTimeString();
            MovieRating personalRating = movie.getPersonalRating();
            float ratingStars;
            String ratingComment;

            if (personalRating != null) {
                ratingStars = personalRating.getStars();
                ratingComment = personalRating.getComment();
            } else {
                ratingStars = -1;
                ratingComment = "Not rated.";
            }


            movieInfo = String.format("%s %s\nLength: %s\nYour rating:\n%.1f/5\n\"%s\"",
                    movieName, movieNameOrig, movieWatchTime, ratingStars, ratingComment);



            alertDialogBuilderMovieList.setMessage(movieInfo);
            alertDialogBuilderMovieList.setCancelable(true);
            alertDialogBuilderMovieList.setPositiveButton("Close", (dialogInterface, i1) -> {});
            alertDialogBuilderMovieList.setNeutralButton("Rate movie", (dialogInterface, i12) -> {
                RateMovieDialogFragment alert = new RateMovieDialogFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                alert.show(ft, "");

            });
            AlertDialog alert = alertDialogBuilderMovieList.create();
            alert.setTitle("title test");
            alert.show();
        });



    }

    @SuppressLint("SetTextI18n")
    private void setDefaultTexts() {
        buttonResetRatings.setText("Reset movie ratings database");
        buttonShowRatingRanking.setText("List of your ratings");
    }


    // clears rating list after asking for confirmation
    public void onButtonResetRatingsClick(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Reset ratings database")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes I'm very sure", (dialog, whichButton) -> {
                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    try {
                        movieManager.resetDatabase();
                    } catch (FileNotFoundException | ParserConfigurationException | TransformerException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Nah", null).show();

    }

    // displays rated movies in order by rating
    public void onButtonShowRatingRankingClick(View v) throws ParserConfigurationException, IOException, SAXException {
        ListView ranking;
        RankingListAdapter rankingListAdapter;

        ArrayList<Movie> movies = movieManager.getMoviesFromDatabase();
        for (Movie movie:movies) {
            if (movie.getName().equals("Test name")) {
                movies.remove(movie);
            }
        }
        if (movies.isEmpty()) {
            Toast.makeText(this, "No rated movies.", Toast.LENGTH_SHORT).show();
        } else {
            Collections.sort(movies, (o1, o2) -> {
                float o1s = o1.getPersonalRating().getStars();
                float o2s = o2.getPersonalRating().getStars();
                return -Float.compare(o1s, o2s);
            });


            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_ranking_list_);
            rankingListAdapter = new RankingListAdapter(this, movies);
            ranking = (ListView) dialog.findViewById(R.id.List);
            ranking.setAdapter(rankingListAdapter);
            rankingListAdapter.addAll(movies);
            dialog.setCancelable(true);
            dialog.setTitle("Your ratings");
            dialog.show();
        }
    }
}