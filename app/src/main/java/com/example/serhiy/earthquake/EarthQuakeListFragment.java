package com.example.serhiy.earthquake;


import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



/**
 * A simple {@link Fragment} subclass.
 */
public class EarthQuakeListFragment extends ListFragment {
    ArrayAdapter<Quake> aa;
    ArrayList<Quake> earthquakes = new ArrayList<Quake>();

    private static final String TAG = "EARTHQUAKE";
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(getActivity(),
                layoutID, earthquakes);

        setListAdapter(aa);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                refreshEarthQuakes();
            }
        });
        t.start();


    }

    public void refreshEarthQuakes() {
        //Get a XML
        URL url;
        try {
            String quakeFeed = getString(R.string.quake_feed);
            url = new URL(quakeFeed);

            URLConnection connecrion;
            connecrion = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connecrion;
            int reaponceCode = httpConnection.getResponseCode();

            if (reaponceCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document dom = db.parse(in);
                Element docElement = dom.getDocumentElement();

                earthquakes.clear();

                NodeList nl = docElement.getElementsByTagName("entry");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element title = (Element)entry.getElementsByTagName("title").item(0);
                        Element g = (Element)entry.getElementsByTagName("georss:point").item(0);
                        Element when = (Element)entry.getElementsByTagName("updated").item(0);
                        Element link = (Element)entry.getElementsByTagName("link").item(0);

                        String details = title.getFirstChild().getNodeValue();
                        String hostname = "http://earthquake.usgs.gov";
                        String linkString = hostname + link.getAttribute("href");

                        String point = g.getFirstChild().getNodeValue();
                        String dt = when.getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
                        Date qdate = new GregorianCalendar(0,0,0).getTime();

                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }

                        String[] location = point.split(" ");
                        Location l = new Location("dummyGPS");
                        l.setLatitude(Double.parseDouble(location[0]));
                        l.setLongitude(Double.parseDouble(location[1]));

                        String magnitudeString = details.split(" ")[1];
                        int end = magnitudeString.length()-1;
                        double magnitude = Double.parseDouble(magnitudeString.substring(0, end));

                        details = details.split(",")[1].trim();

                        final Quake quake = new Quake(qdate, details, l, magnitude, linkString);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                addNewQuake(quake);
                            }
                        });
                    }
                }
            }

        } catch (MalformedURLException e1) {
            Log.d(TAG, "MalformedURLException");
        } catch (IOException e1) {
            Log.d(TAG, "IOException");
        } catch (ParserConfigurationException e1) {
            Log.d(TAG, "ParserConfigurationException");
        } catch (SAXException e) {
            Log.d(TAG, "SAXException");
        }

    }

    private void addNewQuake(Quake _quake) {
        Earthquake earthquakeActivity = (Earthquake)getActivity();
        if(_quake.getMagnitude() > earthquakeActivity.minMagnitude) {
            earthquakes.add(_quake);
        }
        aa.notifyDataSetChanged();
    }
}
