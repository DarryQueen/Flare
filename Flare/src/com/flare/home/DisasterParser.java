package com.flare.home;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class DisasterParser extends AsyncTask<Object, Void, List<Disaster>> {
	public static final String EARTHQUAKE_FEED_URL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_day.atom";
	//Radius around current location to search:
	public static final int SEARCH_RADIUS = 30;

	private OnPostExecuteListener mListener;
	private Geocoder mGeocoder;
	private Location mLocation;

	private List<Disaster> pullData() {
		List<Disaster> disasters = pullEarthquakeData();

		//Get rid of emergencies not in past 24 hours:
		return disasters;
	}

	private List<Disaster> pullEarthquakeData() {
		URL url;
		try {
			url = new URL(EARTHQUAKE_FEED_URL);
		} catch (MalformedURLException e) {
			return new LinkedList<Disaster>();
		}
		return getWebData(url);
	}

	private List<Disaster> getWebData(URL url) {
		InputStream in;
		try {
			in = url.openStream();
		} catch (IOException e1) {
			Log.i("DisasterParser", "Couldn't open URL");
			return new LinkedList<Disaster>();
		}
		List<Disaster> ans = new LinkedList<Disaster>();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			ans = readFeed(parser);
		} catch (XmlPullParserException e) {
			Log.i("DisasterParser", "Couldn't pull data using XML Parser");
			e.printStackTrace();
		} catch (IOException e) {
			Log.i("DisasterParser", "Couldn't find nextTag or close InputStream");
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				Log.i("DisasterParser", "Could not close InputStream");
				e.printStackTrace();
			}
		}
		return ans;
	}

	private List<Disaster> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		List<Disaster> entries = new LinkedList<Disaster>();

		Integer counter = 0;
		parser.require(XmlPullParser.START_TAG, null, "feed");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			Log.i("DisasterParser", "Name of tag: " + name);
			//Starts by looking for the entry tag:
			if (name.equals("entry")) {
				Disaster dis = readEntry(parser);
				if(dis != null){
					entries.add(dis);
				}
				counter++;
			} else {
				skip(parser);
			}
			Log.i("DisasterParser", "Number of entries parsed: " + counter.toString() + ".");
		}
		Log.i("DisasterParser", "Number entries in readFeed list: " + Integer.valueOf(entries.size()));
		return entries;
	}

	private Disaster readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "entry");
		//Generalize this:
		String disaster = "Earthquake";
		Date date = new Date();
		Location loc = new Location("GPS");
		parser.next();
		while (parser.getEventType() != XmlPullParser.END_TAG || !parser.getName().equals("entry")) { //try breakpoint here
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				parser.next();
				continue;
			}
			String name = parser.getName();
			Log.i("DisasterParser", "Name of tag in entry: " + name + ".");
			if (name.equals("georss:point")) {
				if (parser.next() == XmlPullParser.TEXT) {
					String longlat = parser.getText();
					StringTokenizer toks = new StringTokenizer(longlat);
					Double latitude = Double.parseDouble(toks.nextToken());
					Double longitude = Double.parseDouble(toks.nextToken());
					loc.setLatitude(latitude);
					loc.setLongitude(longitude);
				}
			} else {
				skip(parser);
			}
		}
		return new Disaster(disaster, loc, date, mGeocoder);
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	@Override
	protected List<Disaster> doInBackground(Object... params) {
		mListener = (OnPostExecuteListener) params[0];
		mLocation = (Location) params[1];
		mGeocoder = (Geocoder) params[2];

		//Scrape data:
		//List<Disaster> disasters = pullData();
		
		//Shitty test fillers:
		List<Disaster> disasters = new ArrayList<Disaster>();
		Date date = new Date();
		Location walnutCreek = new Location("GPS"), oakland = new Location("GPS"), berkeley = mLocation, cupertino = new Location("GPS");
		walnutCreek.setLatitude(37.9064); walnutCreek.setLongitude(-122.065);
		oakland.setLatitude(37.8044); oakland.setLongitude(-122.2708);
		cupertino.setLatitude(37.3175); cupertino.setLongitude(-122.0419);
		disasters.add(new Disaster("Hurricane", walnutCreek, date, mGeocoder));
		disasters.add(new Disaster("Earthquake", oakland, date, mGeocoder));
		disasters.add(new Disaster("Death Drizzle", cupertino, date, mGeocoder));
		disasters.add(new Disaster("Forest Fire", berkeley, date, mGeocoder));
		
		for(Disaster d: disasters){
			Log.i("DisasterParser", d.getLocation().toString());
		}
		return disasters;
	}

	@Override
	protected void onPostExecute(List<Disaster> returns) {
		mListener.onPostExecute(returns);
	}

	public static interface OnPostExecuteListener {
		public void onPostExecute(List<Disaster> disasters);
	}
}
