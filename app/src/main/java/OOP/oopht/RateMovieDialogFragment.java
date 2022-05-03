package OOP.oopht;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
// dialog fragment for movie rating popup
public class RateMovieDialogFragment extends DialogFragment {
    MovieManager movieManager = MovieManager.getInstance();
    TheaterManager theaterManager;
    {
        try {
            theaterManager = new TheaterManager();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    ArrayList<Theater> theatersList = theaterManager.getTheaterList();
    ArrayList<Movie> moviesList = movieManager.getMovieList(theatersList.get(theaterManager.getTheaterChoice()));



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_rate2, null));

        builder.setMessage("Rate the movie:")
                .setPositiveButton("Save rating", (dialogInterface, i) -> rateMovie())
                .setNegativeButton("Cancel", (dialogInterface, i) -> {});
        return builder.create();
    }

    // saves movie rating
    private void rateMovie() {
        RatingBar ratingBar = getDialog().findViewById(R.id.ratingBar);
        EditText ratingText = getDialog().findViewById(R.id.ratingText);
        Movie movie = moviesList.get(movieManager.getMovieChoice());

        MovieRating rating = new MovieRating(ratingBar.getRating(), ratingText.getText().toString(), new Date());
        Log.v("SYSOUT", String.format("Rating: '%f' '%s'", ratingBar.getRating(), ratingText.getText().toString()));
        movie.setPersonalRating(rating);
        try {
            movieManager.addMovieToFile(movie);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}

