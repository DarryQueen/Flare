package com.flare.log;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flare.R;
import com.flare.location.LocationChangeListener;
import com.flare.location.LocationStatus;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class LogFragment extends Fragment implements LocationChangeListener {
	private View rootView;
	private GoogleMap mMap;

	private Location userLocation;
	private static final float DEFAULT_ZOOM = 18.0f;
	private boolean initialZoomed = false;
	private boolean initialMarked = false;
	
	BitmapDescriptor userDescriptor;
	BitmapDescriptor clearDescriptor;
	BitmapDescriptor mediumDescriptor;
	BitmapDescriptor severeDescriptor;
	
	//For query, how far in the past to search in milliseconds:
	private static final int TIME_RANGE = 30 * 60000;
	//For query, how far around user location to search in kilometers:
	private static final int RADIUS = 3;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (parent != null)
				parent.removeView(rootView);
		}
		try {
			rootView = inflater.inflate(R.layout.fragment_log, container, false);
		} catch (InflateException e) {
			return rootView;
		}

		//Set images for markers:
		userDescriptor = BitmapDescriptorFactory.defaultMarker(33);
		clearDescriptor = BitmapDescriptorFactory.defaultMarker(145);
		mediumDescriptor = BitmapDescriptorFactory.defaultMarker(55);
		severeDescriptor = BitmapDescriptorFactory.defaultMarker(10);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		MapsInitializer.initialize(getActivity());

		if (!initialZoomed) {
			zoomToLocation();
		}
		if (!initialMarked) {
			putMarkers();
		}

		return rootView;
	}
	
	private void zoomToLocation() {
		if (userLocation != null) {
			LatLng coordinates = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(coordinates, DEFAULT_ZOOM);
			mMap.moveCamera(update);
			initialZoomed = true;
		}
	}

	private void putMarkers() {
		if (userLocation != null) {
			// Put own location on map:
			LatLng position = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
			mMap.addMarker(new MarkerOptions()
					.position(position)
					.icon(userDescriptor)
					.title("You"));
			
			//Put other locations on map:
			new AddMarkersTask().execute();
			
			initialMarked = true;
		}
	}

	@Override
	public void onLocationUpdate(Location location) {
		userLocation = location;

		if (mMap != null) {
			if (!initialZoomed) {
				zoomToLocation();
			}
			if (!initialMarked) {
				putMarkers();
			}
		}
	}
	
	private class AddMarkersTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			ParseGeoPoint userParseLocation = new ParseGeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
			Date timeRange = new Date((new Date()).getTime() - TIME_RANGE);
			String device = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
			
			//Query relevant locations:
			ParseQuery<ParseObject> query = ParseQuery.getQuery("Location");
			query.whereWithinKilometers("location", userParseLocation, RADIUS);
			query.whereGreaterThan("createdAt", timeRange);
			query.whereNotEqualTo("deviceId", device);
			query.orderByDescending("createdAt");
			query.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> parseObjects, ParseException e) {
					if (e != null) {
						//There was an error:
						return;
					}
					
					//Filter unique devices and add a marker:
					HashSet<String> uniqueDevices = new HashSet<String>();
					for (ParseObject parseObject : parseObjects) {
						String deviceId = parseObject.getString("deviceId");
						//Add a marker if device is unique:
						if (!uniqueDevices.contains(deviceId)) {
							ParseGeoPoint location = parseObject.getParseGeoPoint("location");
							
							//Specify status of marker:
							int severity = parseObject.getInt("severity");
							int population = parseObject.getInt("population");
							BitmapDescriptor descriptor = null;
							String title = "Status: ";
							String snippet = null;
							if (severity == LocationStatus.STATUS_CLEAR) {
								descriptor = clearDescriptor;
								title += "Clear";
							} else {
								if (severity == LocationStatus.STATUS_MEDIUM) {
									descriptor = mediumDescriptor;
									title += "Danger";
								} else if (severity == LocationStatus.STATUS_SEVERE) {
									descriptor = severeDescriptor;
									title += "Emergency";
								}
								snippet = population == 1 ? "1 person" : population + " people";
								snippet += " in the vicinity.";
							}

							LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
							mMap.addMarker(new MarkerOptions()
									.position(position)
									.icon(descriptor)
									.title(title)
									.snippet(snippet));
							uniqueDevices.add(deviceId);
						}
					}
				}
			});
			
			return null;
		}
	}
}