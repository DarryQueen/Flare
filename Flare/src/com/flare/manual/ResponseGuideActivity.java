package com.flare.manual;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flare.R;

public class ResponseGuideActivity extends Activity {
	private static final String RESPONSELISTITEM_REQUEST = "com.flare.manual.ResponseGuideActivity.RESPONSELISTITEM_REQUEST";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_response_guide);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		//Load Intent:
		Intent intent = getIntent();
		ResponseListItem item = intent.getExtras().getParcelable(ManualFragment.RESPONSELISTITEM_REQUEST);
		setTitle(item.getTitle() + " Response Guide");
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.response_guide_container, PlaceholderFragment.newInstance(item)).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}
		
		public static PlaceholderFragment newInstance(ResponseListItem item) {
			PlaceholderFragment f = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putParcelable(RESPONSELISTITEM_REQUEST, item);
			f.setArguments(args);
			return f;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_response_guide, container, false);

			//Load Intent:
			Bundle args = getArguments();
			ResponseListItem item = args.getParcelable(RESPONSELISTITEM_REQUEST);
			String loadString;
			
			//Set title text:
			((TextView) rootView.findViewById(R.id.response_guide_title)).setText(item.getTitle());

			//Set Before text:
			if ((loadString = item.getBefore()) == null || loadString.equals("")) {
				(rootView.findViewById(R.id.response_guide_before)).setVisibility(View.GONE);
			} else {
				((TextView) rootView.findViewById(R.id.response_guide_before_body)).setText(Html.fromHtml(loadString, imageGetter, null));
			}

			//Set During text:
			if ((loadString = item.getDuring()) == null || loadString.equals("")) {
				(rootView.findViewById(R.id.response_guide_during)).setVisibility(View.GONE);
			} else {
				((TextView) rootView.findViewById(R.id.response_guide_during_body)).setText(Html.fromHtml(loadString, imageGetter, null));
			}
			
			//Set After text:
			if ((loadString = item.getAfter()) == null || loadString.equals("")) {
				(rootView.findViewById(R.id.response_guide_after)).setVisibility(View.GONE);
			} else {
				((TextView) rootView.findViewById(R.id.response_guide_after_body)).setText(Html.fromHtml(loadString, imageGetter, null));
			}
			
			return rootView;
		}
		
		private ImageGetter imageGetter = new ImageGetter() {
			@Override
			public Drawable getDrawable(String source) {
				int imageResource = getActivity().getResources().getIdentifier(source, null, getActivity().getPackageName());
				if (imageResource == 0) {
					return null;
				}
				Drawable d = getActivity().getResources().getDrawable(imageResource);
				d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
				return d;
			}
		};
	}
}