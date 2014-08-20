package com.flare.home;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.flare.MainActivity;
import com.flare.R;
import com.flare.location.CurrentLocationRetriever;
import com.flare.location.LocationChangeListener;
import com.flare.location.LocationStatus;
import com.flare.pingreport.PingReportActivity;

public class HomeFragment extends Fragment implements LocationChangeListener, DisasterParser.OnPostExecuteListener {
	private View rootView;
	private Geocoder geocoder;
	private DisasterArrayAdapter mAdapter;
	
	//Views to save:
	private boolean emergencyIsVisible;
	private Button emergencyButton;
	private Button clearButton;
	private TextView lastPingTextView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			return rootView;
		}
		
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		CurrentLocationRetriever.addListener(this);
		
		emergencyIsVisible = true;
		geocoder = new Geocoder(getActivity(), Locale.getDefault());
		
		emergencyButton = (Button) rootView.findViewById(R.id.home_btn_emergency);
		emergencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), PingReportActivity.class);
				getActivity().startActivityForResult(intent, MainActivity.PING_REQUEST_CODE);
			}
		});
		
		clearButton = (Button) rootView.findViewById(R.id.home_btn_clear);
		clearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LocationStatus status = new LocationStatus();
				CurrentLocationRetriever.setStatus(status);
				CurrentLocationRetriever.saveAndSubmitLocation("Phew, you made it! Hope you didn't lose a leg.");

				switchButtonVisible();
			}
		});

		//Set text for last ping:
		lastPingTextView = (TextView) rootView.findViewById(R.id.home_last_ping);
		updateLastPing();

		//Set adapter to empty list:
		mAdapter = new DisasterArrayAdapter(getActivity(), R.layout.listview_emergency_watch, new ArrayList<Disaster>());
		ListView listView = (ListView) rootView.findViewById(R.id.lv_emergencies);
		TextView emptyView = (TextView) rootView.findViewById(R.id.home_lv_empty);
		listView.setEmptyView(emptyView);
		listView.setAdapter(mAdapter);
		
		//Switch visible button depending on current persistent data:
		SharedPreferences currentStatusPrefs = getActivity().getSharedPreferences(CurrentLocationRetriever.PREFS_LOCATION_STATUS_FILE, Activity.MODE_PRIVATE);
		int severity = currentStatusPrefs.getInt(CurrentLocationRetriever.STATUS_SEVERITY, LocationStatus.STATUS_CLEAR);
		if (severity != LocationStatus.STATUS_CLEAR) {
			switchButtonVisible();
		}

		return rootView;
	}
	
	public void switchButtonVisible() {
		if (emergencyIsVisible) {
			emergencyButton.setVisibility(View.INVISIBLE);
			clearButton.setVisibility(View.VISIBLE);
		} else {
			emergencyButton.setVisibility(View.VISIBLE);
			clearButton.setVisibility(View.INVISIBLE);
		}
		emergencyIsVisible = !emergencyIsVisible;
	}
	
	private void updateLastPing() {
		FileInputStream fis = null;
		String line = "";
		try {
			fis = getActivity().openFileInput(CurrentLocationRetriever.LOG_FILE_NAME);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			if (fis != null) {
				line = reader.readLine();
				fis.close();
			}
		} catch (Exception e) {}
		
		//String variables to initialize:
		String place, time, location;
		
		if (fis != null) {
			//Extract relevant fields from log file:
			int openParen = line.indexOf('(');
			int firstComma = line.indexOf(',', openParen);
			double latitude = Double.parseDouble(line.substring(openParen + 1, firstComma));
			double longitude = Double.parseDouble(line.substring(firstComma + 1, line.length() - 1));
			location = "(" + (int) latitude + ", " + (int) longitude + ")";
			time = line.substring(0, openParen - 1);
			place = "";

			//Geocoding:
			try {
				Address address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
				String city = address.getLocality();
				if (city == null) {
					city = "Unknown City";
				}
				String state = address.getAdminArea();
				if (state == null) {
					state = "Unknown State";
				}
				place = city + ", " + state;
			} catch (IOException e) {
				place = "Unknown City";
			}

			lastPingTextView.setText(Html.fromHtml((place + "<br><small>" + time + " @ " + location + "</small>")));
		}
	}

	@Override
	public void onLocationUpdate(Location location) {
		new DisasterParser().execute((DisasterParser.OnPostExecuteListener) this, location, geocoder);
	}

	@Override
	public void onPostExecute(List<Disaster> disasters) {
		mAdapter.setData(disasters);
	}
}