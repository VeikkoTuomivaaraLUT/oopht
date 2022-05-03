package OOP.oopht;

import android.text.Editable;

import java.util.Date;

public class MovieRating {
    private float stars;
    private String comment;
    private Date date;

    public MovieRating() {
        setStars(-1);
        setDate(new Date());
        setComment("No rating");
    }

    public MovieRating(float stars, Date date) {
        setStars(stars);
        setDate(date);
        setComment("No comment");
    }

    public MovieRating(float stars, String comment, Date date) {
        setStars(stars);
        setComment(comment);
        setDate(date);
    }


    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // user can add date in comment if they wish
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
