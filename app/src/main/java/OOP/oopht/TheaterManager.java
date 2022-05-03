package OOP.oopht;

import android.os.StrictMode;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TheaterManager {
    private int theaterChoice; // index for theaterList and theaterNamesList, same as movieChoice

    public TheaterManager() throws ParserConfigurationException, IOException, SAXException {
        // cba to mess with async stuff
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getTheatersFromFinnkino();
    }

    // separate names for theaters and names, similar to movie lists
    private ArrayList<Theater> theaterList = new ArrayList<>();
    private ArrayList<String> theaterNamesList = new ArrayList<>();

    public ArrayList<Theater> getTheaterList() {
        if (theaterList == null) {
            Log.v("SYSOUT", "Theater names list null.");
            addTheater("Blank", 0);
        }
        return theaterList;
    }

    public ArrayList<String> getTheaterNamesList() {
        if (theaterNamesList == null) {
            Log.v("SYSOUT", "Theater names list null.");
            addTheater("Blank", 0);
        }
        return theaterNamesList;
    }

    public void addTheater(String name, int id) {
        Theater theater = new Theater(name, id);
        theaterList.add(theater);
        theaterNamesList.add(theater.getName());
    }

    // gets theaters from Finnkino and adds them to the theater lists
    public void getTheatersFromFinnkino() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        URL url = new URL("https://www.finnkino.fi/xml/TheatreAreas/");
        InputStream stream = url.openStream();
        Document doc = docBuilder.parse(stream);

        NodeList theaters = doc.getElementsByTagName("TheatreArea");

        for (int i = 0, len = theaters.getLength(); i < len; i++) {
            Node theater = theaters.item(i);
            Element element = (Element) theater;
            String name = getString("Name", element);
            int id = Integer.parseInt(getString("ID", element));
            addTheater(name, id);
        }
    }

    // gets xml tag values
    protected String getString(String tagName, Element element) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(0).getChildNodes();

            if (subList != null && subList.getLength() > 0) {
                return subList.item(0).getNodeValue();
            }
        }
        return null;
    }

    public int getTheaterChoice() {
        return theaterChoice;
    }

    public void setTheaterChoice(int theaterChoice) {
        this.theaterChoice = theaterChoice;
    }
}
