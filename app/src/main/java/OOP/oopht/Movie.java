package OOP.oopht;

import android.annotation.SuppressLint;

public class Movie {
    private String name;
    private String nameOriginal; // original name, usually in english
    private int watchTime; // in minutes
    private MovieRating personalRating;

    public Movie(String name, String nameOriginal) {
        setName(name);
        setNameOriginal(nameOriginal);
        setWatchTime(0);
    }

    public Movie(String name, String nameOriginal, int watchTime) {
        setName(name);
        setNameOriginal(nameOriginal);
        setWatchTime(watchTime);
    }

    public Movie(String name, String nameOriginal, int watchTime, MovieRating movieRating) {
        setName(name);
        setNameOriginal(nameOriginal);
        setWatchTime(watchTime);
        setPersonalRating(movieRating);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressLint("DefaultLocale")
    // formats watch time to hh:mm:ss (seconds always zero since it's measured in minutes)
    public String getWatchTimeString() {
        int h = watchTime / 60;
        int m = watchTime % 60;
        return String.format("%d:%d:00", h, m);
    }


    public int getWatchTime() {
        return this.watchTime;
    }

    public void setWatchTime(int watchTime) {
        this.watchTime = watchTime;
    }

    public String getNameOriginal() {
        return nameOriginal;
    }

    public void setNameOriginal(String nameOriginal) {
        this.nameOriginal = nameOriginal;
    }

    public MovieRating getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(MovieRating personalRating) {
        this.personalRating = personalRating;
    }

}
