package com.flare;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import com.flare.home.HomeFragment;
import com.flare.home.TabListener;
import com.flare.location.CurrentLocationRetriever;
import com.flare.location.LocationChangeListener;
import com.flare.log.LogFragment;
import com.flare.manual.ManualFragment;
import com.parse.Parse;

public class MainActivity extends Activity {
	//Shared preferences:
	public static final String PREFS_NAME = "com.flare.MainActivity.PREFS_FILES";
	public static final String HAS_SIGNED_UP = "com.flare.MainActivity.HAS_SIGNED_UP";
	public static final String USER_FIRSTNAME = "com.flare.MainActivity.USER_FIRSTNAME";
	public static final String USER_LASTNAME = "com.flare.MainActivity.USER_LASTNAME";
	public static final String USER_EMAIL = "com.flare.MainActivity.USER_EMAIL";
	public static final String USER_PHONE = "com.flare.MainActivity.USER_PHONE";
	public static final String USER_BIRTHDAY = "com.flare.MainActivity.USER_BIRTHDAY";
	
	//Request codes:
	public static final int PING_REQUEST_CODE = 1;

	//Declare tab variables:
	private ActionBar actionBar;
	private ActionBar.Tab homeTab, manualTab, logTab;
	private Fragment homeFragment = new HomeFragment();
	private Fragment manualFragment = new ManualFragment();
	private Fragment logFragment = new LogFragment();

	//Other variables:
	private CurrentLocationRetriever mLocationRetriever;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Begin Parse:
		new ParseInitializeTask().execute(this);
		
		//Set universal fonts:
		FontsOverride.setDefaultFont(this, "DEFAULT", "BebasNeue.otf");
		FontsOverride.setDefaultFont(this, "SANS_SERIF", "BebasNeue.otf");
		FontsOverride.setDefaultFont(this, "SERIF", "RobotoSlab-Regular.ttf");

		//Check if user has signed up:
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		boolean hasSignedUp =  settings.getBoolean(HAS_SIGNED_UP, false);
		
		if (!hasSignedUp) {
			Intent intent = new Intent(this, SignupActivity.class);
			startActivity(intent);
			finish();
		} else {
			//Standard start:
			setContentView(R.layout.activity_main);
	
			//Get ActionBar and set font for title:
			actionBar = getActionBar();
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			
			int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
			Typeface font = Typeface.createFromAsset(getAssets(), "font/BebasNeue.otf");
			((TextView) findViewById(titleId)).setTypeface(font);
	
			//Set titles for tabs (can also set icons):
			homeTab = actionBar.newTab().setCustomView(R.layout.tab_home);
			manualTab = actionBar.newTab().setCustomView(R.layout.tab_manual);
			logTab = actionBar.newTab().setCustomView(R.layout.tab_log);
	
			//Set listeners for each tab:
			homeTab.setTabListener(new TabListener(homeFragment));
			manualTab.setTabListener(new TabListener(manualFragment));
			logTab.setTabListener(new TabListener(logFragment));
	
			//Add tabs to ActionBar:
			actionBar.addTab(homeTab);
			actionBar.addTab(manualTab);
			actionBar.addTab(logTab);
			
			mLocationRetriever = new CurrentLocationRetriever();
			//Create LocationRetriever if not already initialized:
			if (!CurrentLocationRetriever.isEnabled()) {
				CurrentLocationRetriever.initialize(this);
				mLocationRetriever.start();
			}
			CurrentLocationRetriever.addListener((LocationChangeListener) logFragment);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PING_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				((HomeFragment) homeFragment).switchButtonVisible();
			}
		}
	}

    @Override
    protected void onStop() {
		mLocationRetriever.stop();
        super.onStop();
    }
    
    private class ParseInitializeTask extends AsyncTask<Activity, Void, Void> {
    	protected Void doInBackground(Activity... activities) {
    		Parse.initialize(activities[0], getString(R.string.key_parse_application), getString(R.string.key_parse_client));
    		return null;
    	}
    }
}