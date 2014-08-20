package com.flare.pingreport;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.flare.R;
import com.flare.location.CurrentLocationRetriever;
import com.flare.location.LocationStatus;

public class PingReportActivity extends Activity {
	private static int green, yellow, red;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		red = getResources().getColor(R.color.red);
		green = getResources().getColor(R.color.green);
		yellow = getResources().getColor(R.color.yellow);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.signup_container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_pingreport, container, false);

			//Update first slider text:
			final TextView populationText = (TextView) rootView.findViewById(R.id.pingreport_textview_population);
			populationText.setText("1");
			final SeekBar populationBar = (SeekBar) rootView.findViewById(R.id.pingreport_slider_population);
			populationBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					int p = progress + 1;
					populationText.setText("" + p);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
			
			//Update second slider text:
			final TextView severityText = (TextView) rootView.findViewById(R.id.pingreport_textview_severity);
			severityText.setText(Html.fromHtml("<font color=\"" + green + "\">Clear</font>"));
			final SeekBar severityBar = (SeekBar) rootView.findViewById(R.id.pingreport_slider_severity);
			severityBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					int p = progress / 33;
					
					int color = 0;
					String severity = "";
					if (p == 0) {
						color = green;
						severity = "Clear";
					} else if (p == 1) {
						color = yellow;
						severity = "Danger";
					} else {
						color = red;
						severity = "Emergency";
					}
					
					String s = "<font color=\"" + color + "\">" + severity + "</font>";
					severityText.setText(Html.fromHtml(s));
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
			
			Button reportButton = (Button) rootView.findViewById(R.id.pingreport_btn_submit);
			reportButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int population = populationBar.getProgress();
					int severity = severityBar.getProgress() / 33;
					LocationStatus status = new LocationStatus(severity, population, "");
					CurrentLocationRetriever.setStatus(status);
					CurrentLocationRetriever.saveAndSubmitLocation("Your location has been logged. We will notify an EMT as soon as possible.");
					
					Activity activity = getActivity();
					activity.setResult(RESULT_OK);
					activity.finish();
				}
			});

			return rootView;
		}
	}
}