package com.flare.home;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

public class Disaster {
	private String mDisaster;
	private Location mLocation;
	private Date mDate;
	
	//Other fields:
	private String mLocationString;

	public Disaster(String disaster, Location location, Date date, Geocoder geocoder) {
		mDisaster = disaster;
		mLocation = location;
		mDate = date;
		
		String locality, adminArea;
		Address address = null;
		try {
			address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
			locality = address.getLocality();
			if (locality == null) {
				locality = "Unknown City";
			}
			adminArea = address.getAdminArea();
			if (adminArea == null) {
				adminArea = "Unknown State";
			}
			mLocationString = locality + ", " + adminArea;
		} catch (IOException e) {
			mLocationString = "Unknown Area";
		} catch (IndexOutOfBoundsException e) {
			mLocationString = "Unknown Area";
		}
	}

	public String getDisaster() {
		return mDisaster;
	}

	public String getPlace() {
		return mLocationString;
	}

	public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
		return dateFormat.format(mDate);
	}
	
	public Location getLocation() {
		return mLocation;
	}
}