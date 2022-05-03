package OOP.oopht;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MovieManager {
    // MovieManager is singleton for easy access to movie list and choice index
    private static MovieManager instance = null;

    String databaseFilename = "OOPHT_movie_database.xml";
    // creating file if it doesn't exist.
    private MovieManager() {
        try {
            File file = new File(MyApp.getContext().getFilesDir(), databaseFilename);
            if (file.isFile()) {
                Log.v("sysout", "file is file");
            } else {
                Log.v("sysout", "file isn't file");
                initFile();
            }
        } catch (ParserConfigurationException | TransformerException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static MovieManager getInstance() {
        if (instance == null) {
            instance = new MovieManager();
        }
        return instance;
    }




    private int movieChoice; // index used to get same movie from movieList and movieNamesList

    private ArrayList<Movie> movieList= new ArrayList<>();
    private ArrayList<String> movieNamesList = new ArrayList<>();

    public ArrayList<Movie> getMovieList(Theater theater) {
        try {
            getMoviesFromFinnkino(theater);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        if (movieList == null) {
            Log.v("SYSOUT", "Movie list null.");
            addMovie("None", "None");
        }
        return movieList;
    }
    // separate list with just the names, used for movie display at the bottom
    public ArrayList<String> getMovieNamesList(Theater theater) {
        try {
            getMoviesFromFinnkino(theater);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        if (movieNamesList == null) {
            Log.v("SYSOUT", "Movie names list null.");
            addMovie("None", "None");
        }
        return movieNamesList;
    }

    private void addMovie(String name, String nameOriginal) {
        Movie movie = new Movie(name, nameOriginal);
        movieList.add(movie);
        movieNamesList.add(movie.getName());
    }

    private void addMovie(String name, String nameOriginal, int watchTime) {
        Movie movie = new Movie(name, nameOriginal, watchTime);
        movieList.add(movie);
        movieNamesList.add(movie.getName());
    }

    private void addMovie(Movie movie) {
        movieList.add(movie);
        movieNamesList.add(movie.getName());
    }

    @SuppressLint("DefaultLocale")
    // adds rated movies to database. takes arraylist to allow multiple at once to reduce
    public void addMoviesToFile(ArrayList<Movie> movies) throws ParserConfigurationException, IOException, SAXException {
        Log.v("sysout", "adding movies to file");
        // first getting existing data from database and backing it up
        ArrayList<Movie> moviesOld = getMoviesFromDatabase();
        for (Movie movie:moviesOld) {
            if (movie.getName() == null) {
                moviesOld.remove(movie);
            }
        }


        // combining new data with existing data, while removing duplicates (based on name)
        if (moviesOld != null) {
            movies.addAll(moviesOld);
            SortedSet<Movie> movieSet = new TreeSet<>(new Comparator<Movie>() {
                @Override
                public int compare(Movie m1, Movie m2) {

                    return m1.getName().compareTo(m2.getName());
                }
            });
            movieSet.addAll(movies);
            movies.clear();
            movies.addAll(movieSet);
        }
        // building xml
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document document = docBuilder.newDocument();
            Element rootElement = document.createElement("movies_database");
            document.appendChild(rootElement);

            for (Movie movie:movies) {
                // movie element
                Element movieElement = document.createElement("Show");

                // name
                Element movieNameE = document.createElement("Title");
                movieNameE.appendChild(document.createTextNode(movie.getName()));
                movieElement.appendChild(movieNameE);

                // original name
                Element movieNameOrigE = document.createElement("OriginalTitle");
                movieNameOrigE.appendChild(document.createTextNode(movie.getNameOriginal()));
                movieElement.appendChild(movieNameOrigE);

                // watch time
                Element movieLengthE = document.createElement("LengthInMinutes");
                movieLengthE.appendChild(document.createTextNode(String.valueOf(movie.getWatchTime())));
                movieElement.appendChild(movieLengthE);

                // user rating
                Element movieUserRatingStars = document.createElement("StarRating");
                try {
                    movieUserRatingStars.appendChild(document.createTextNode(String.valueOf(movie.getPersonalRating().getStars())));
                } catch (NullPointerException e) {
                    movieUserRatingStars.appendChild(document.createTextNode(String.valueOf(0)));
                }
                movieElement.appendChild(movieUserRatingStars);

                Element movieUserRatingComment = document.createElement("RatingComment");
                try {
                    movieUserRatingComment.appendChild(document.createTextNode(movie.getPersonalRating().getComment()));
                } catch (NullPointerException e) {
                    movieUserRatingComment.appendChild(document.createTextNode(""));
                }
                movieElement.appendChild(movieUserRatingComment);


                rootElement.appendChild(movieElement);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(MyApp.getContext().openFileOutput(databaseFilename, Context.MODE_PRIVATE));
            transformer.transform(domSource, streamResult);


        } catch (TransformerConfigurationException e) {
            Log.e("IOE", "An error occurred.");
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
    // adds a single movie to database so I don't have to make an arraylist every time
    public void addMovieToFile(Movie movie) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<Movie> movies = new ArrayList<>();
        movies.add(movie);
        addMoviesToFile(movies);
    }


    // gets movies from Finnkino and adds them to the movie list
    private void getMoviesFromFinnkino(Theater theater) throws ParserConfigurationException, IOException, SAXException {
        Date date = new Date();
        String dateS = date.toString();
        ArrayList<Movie> moviesAL = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        URL url = new URL(String.format("https://www.finnkino.fi/xml/Schedule/?area=%s&dt=päivämäärä %s", theater.getId(), dateS));
        //Log.v("SYSOUT", url.toString());
        InputStream stream = url.openStream();
        Document doc = docBuilder.parse(stream);




        NodeList movies = doc.getElementsByTagName("Show");

        for (int i = 0, len = movies.getLength(); i < len; i++) {
            Node movie = movies.item(i);
            Element element = (Element) movie;
            String name = getValue("Title", element);
            int watchTime = Integer.parseInt(getValue("LengthInMinutes", element));
            //Log.v("sysout", name);
            String nameOriginal = getValue("OriginalTitle", element);
            moviesAL.add(new Movie(name, nameOriginal, watchTime));
        }

        for (Movie movie:moviesAL) {
            addMovie(movie);
        }
    }

    // gets xml tag values
    protected String getValue(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }

    // gets movies from database. similar to getting from finnkino, except these have personal ratings in them
    public ArrayList<Movie> getMoviesFromDatabase() throws ParserConfigurationException, IOException, SAXException {
        InputStream inputStream = MyApp.getContext().openFileInput(databaseFilename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String s;
        while ((s=bufferedReader.readLine()) != null) {
            Log.v("sysout", s);
        }
        inputStream.close();

        ArrayList<Movie> movies = new ArrayList<>();

        DocumentBuilderFactory dbf_ = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder_ = dbf_.newDocumentBuilder();

        Document documentOld = docBuilder_.parse(MyApp.getContext().openFileInput(databaseFilename));

        NodeList movieNodes = documentOld.getElementsByTagName("Show");
        Log.v("sysout node len", String.valueOf(movieNodes.getLength()));
        if (movieNodes.getLength() == 0) {
            return null;
        }
        for (int i = 0, len = movieNodes.getLength(); i < len; i++) {
            Node movie = movieNodes.item(i);
            Element element = (Element) movie;

            String ratingComment = getValue("RatingComment", element);
            float ratingStars;
            try {
                ratingStars = Float.parseFloat(getValue("StarRating", element));
            } catch (NumberFormatException e) {
                ratingStars = -1;
            }
            String name = getValue("Title", element);
            int watchTime;
            try {
                watchTime = Integer.parseInt(getValue("LengthInMinutes", element));
            } catch (NumberFormatException e) {
                watchTime = -1;
            }
            String nameOriginal = getValue("OriginalTitle", element);


            MovieRating rating = new MovieRating(ratingStars, ratingComment, new Date());
            movies.add(new Movie(name, nameOriginal, watchTime, rating));
        }
        return movies;
    }


    public int getMovieChoice() {
        return movieChoice;
    }

    public void setMovieChoice(int movieChoice) {
        this.movieChoice = movieChoice;
    }

    // resets file with some default values to prevent null point errors
    // default values are ignored when listing rated movies
    private void initFile() throws ParserConfigurationException, TransformerException, FileNotFoundException {
        File path = MyApp.getContext().getFilesDir();
        File database = new File(path, databaseFilename);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();
        Element rootElement = document.createElement("movies_database");
        document.appendChild(rootElement);

        // movie element
        Element movieElement = document.createElement("Show");

        // name
        Element movieNameE = document.createElement("Title");
        movieNameE.appendChild(document.createTextNode("Test name"));
        movieElement.appendChild(movieNameE);

        // original name
        Element movieNameOrigE = document.createElement("OriginalTitle");
        movieNameOrigE.appendChild(document.createTextNode("Test name orig"));
        movieElement.appendChild(movieNameOrigE);

        // watch time
        Element movieLengthE = document.createElement("LengthInMinutes");
        movieLengthE.appendChild(document.createTextNode("6969"));
        movieElement.appendChild(movieLengthE);


        // user rating
        Element movieUserRatingStars = document.createElement("StarRating");
        movieUserRatingStars.appendChild(document.createTextNode("2"));
        movieElement.appendChild(movieUserRatingStars);

        Element movieUserRatingComment = document.createElement("RatingComment");
        movieUserRatingComment.appendChild(document.createTextNode("test comment"));
        movieElement.appendChild(movieUserRatingComment);


        rootElement.appendChild(movieElement);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(MyApp.getContext().openFileOutput(databaseFilename, Context.MODE_PRIVATE));
        transformer.transform(domSource, streamResult);
    }

    public void resetDatabase() throws FileNotFoundException, ParserConfigurationException, TransformerException {
        initFile();
    }
}
