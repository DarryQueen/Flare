package com.flare.location;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class CurrentLocationRetriever implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		com.google.android.gms.location.LocationListener {

	//Context to pass in:
	private static Activity mActivity;

	//Declare API Client to manage connection with Google Play:
	private static LocationClient mLocationClient;
	private static Location currentLocation;
	private static LocationRequest mLocationRequest;

	//Code for when connection fails.
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static boolean isEnabled;
	
	//List of all listeners:
	private static List<LocationChangeListener> listeners;
	
	//Current ping status:
	private static LocationStatus mStatus;

	//Update times. Default fastest is 1 minute, default slowest is 5 minutes.
	public static final long FASTEST_INTERVAL = 60 * 1000;
	public static final long NORMAL_INTERVAL = 5 * FASTEST_INTERVAL;
	
	//Name for log file and persistent data:
	public static final String LOG_FILE_NAME = "pings.log";
	public static final String PREFS_LOCATION_STATUS_FILE = "com.flare.location.CurrentLocationRetriever.PREFS_LOCATION_STATUS_FILE";
	public static final String STATUS_SEVERITY = "com.flare.location.CurrentLocationRetriever.STATUS_SEVERITY";
	public static final String STATUS_POPULATION = "com.flare.location.CurrentLocationRetriever.STATUS_POPULATION";
	public static final String STATUS_MESSAGE = "com.flare.location.CurrentLocationRetriever.STATUS_MESSAGE";
	
	//Constructors:
	public static void initialize(Activity activity) {
		SharedPreferences currentStatusPrefs = activity.getSharedPreferences(PREFS_LOCATION_STATUS_FILE, Activity.MODE_PRIVATE);
		int severity = currentStatusPrefs.getInt(STATUS_SEVERITY, LocationStatus.STATUS_CLEAR);
		int population = currentStatusPrefs.getInt(STATUS_POPULATION, -1);
		String message = currentStatusPrefs.getString(STATUS_MESSAGE, "");
		
		LocationStatus status = new LocationStatus(severity, population, message);
		initialize(activity, status);
	}
	
	public static void initialize(Activity activity, LocationStatus status) {
		mActivity = activity;
		mStatus = status;
		listeners = new ArrayList<LocationChangeListener>();
		
		//Check if Google Play Services installed correctly and show error dialog accordingly:
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
		if (resultCode == ConnectionResult.SUCCESS){
			//Make LocationClient that will access locations:
			mLocationClient = new LocationClient(mActivity, new CurrentLocationRetriever(), new CurrentLocationRetriever());
			
			//Make LocationRequest that defines the intervals to update location:
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
			//Alternatively, could be PRIORITY_HIGH_ACCURACY for more GPS location sensing:
			mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			mLocationRequest.setInterval(NORMAL_INTERVAL);

			isEnabled = true;
		} else if (resultCode == ConnectionResult.SERVICE_MISSING || resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED || resultCode == ConnectionResult.SERVICE_DISABLED){
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			dialog.show();
			
			isEnabled = false;
			
			Toast.makeText(mActivity, "Error getting location services. Please fix and restart.", Toast.LENGTH_SHORT).show();
		} else {
			isEnabled = false;
			
			Toast.makeText(mActivity, "Unknown error in location services.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static Location getLocation() {
		return currentLocation;
	}
	
	public static void addListener(LocationChangeListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void onLocationChanged(final Location location) {
		currentLocation = location;

		//Make listeners do something:
		for (LocationChangeListener listener : listeners) {
			listener.onLocationUpdate(location);
		}
		
		//Send location to server:
		saveAndSubmitLocation("Your location has been logged.");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		//Google Play services can resolve some errors it detects.
		//If the error has a resolution, try sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {
                //Start an Activity that tries to resolve the error:
                connectionResult.startResolutionForResult(mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                //Thrown if Google Play services canceled the original PendingIntent:
            } catch (IntentSender.SendIntentException e) {
                //Log the error:
                e.printStackTrace();
            }
        } else {
            //If no resolution is available, display a Dialog to the user with the error. Note: update this Dialog.
            mActivity.showDialog(connectionResult.getErrorCode());
        }
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(mActivity, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	public void start() {
		if (isEnabled) {
			mLocationClient.connect();
		}
    }
	
	public void stop() {
		if (isEnabled) {
			mLocationClient.disconnect();
			isEnabled = false;
		}
    }
	
	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setStatus(LocationStatus status) {
		mStatus = status;
		
		SharedPreferences currentStatusPrefs = mActivity.getSharedPreferences(PREFS_LOCATION_STATUS_FILE, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = currentStatusPrefs.edit();
		
		editor.putInt(STATUS_SEVERITY, status.getStatus());
		editor.putInt(STATUS_POPULATION, status.getPopulation());
		editor.putString(STATUS_MESSAGE, status.getMessage());
		
		editor.commit();
	}
	
	public static void saveAndSubmitLocation(String message) {
		new PingLocationTask().execute(currentLocation, message);
	}
	private static class PingLocationTask extends AsyncTask<Object, Void, Void> {
		private String message;
		
		@Override
		protected Void doInBackground(Object... objects) {
			final Location location = (Location) objects[0];
			message = (String) objects[1];

			String device = Secure.getString(mActivity.getContentResolver(), Secure.ANDROID_ID);
			ParseGeoPoint userParseLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

			ParseObject parseObject = new ParseObject("Location");
			parseObject.put("deviceId", device);
			parseObject.put("location", userParseLocation);
			parseObject.put("severity", mStatus.getStatus());
			parseObject.put("population", mStatus.getPopulation());
			parseObject.put("message", mStatus.getMessage());
			parseObject.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException arg0) {
					//Log ping:
					DateFormat dateFormat = new SimpleDateFormat("h:mm a, MM/dd/yy", Locale.getDefault());
					String currentDate = dateFormat.format(new Date());
					String pingLog = currentDate + " (" + location.getLatitude() + ", " + location.getLongitude() + ")";
					
					FileOutputStream fos = null;
					try {
						fos = mActivity.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE);
						fos.write(pingLog.getBytes());
						fos.close();
					} catch (Exception e) {}
				}
			});
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
		}
	}
}