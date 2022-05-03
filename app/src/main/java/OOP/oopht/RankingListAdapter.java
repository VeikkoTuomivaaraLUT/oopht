package OOP.oopht;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

// adapter for movie ranking list
public class RankingListAdapter extends ArrayAdapter<Movie> {
    private final Activity context;
    private final ArrayList<Movie> movies;
    public RankingListAdapter(Activity context, ArrayList<Movie> movies) {
        super(context, R.layout.dialog_ranking_list_item);
        this.context = context;
        this.movies = movies;
    }


    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.dialog_ranking_list_item, null, true);

        TextView titleText = (TextView) rowView.findViewById(R.id.textViewName);
        RatingBar ratingBar = (RatingBar) rowView.findViewById(R.id.ratingBar);
        TextView textViewComment = (TextView) rowView.findViewById(R.id.textViewComment);
        titleText.setText(movies.get(position).getName());
        ratingBar.setRating(movies.get(position).getPersonalRating().getStars());
        textViewComment.setText(movies.get(position).getPersonalRating().getComment());
        return rowView;
    }
}
