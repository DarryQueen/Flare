package com.flare.manual;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flare.R;

public class ResponseArrayAdapter extends ArrayAdapter<ResponseListItem> {
	public static final int EXPAND_ANIMATION_DURATION = 330;
	public static final int SCROLL_ANIMATION_DURATION = 100;

	private List<ResponseListItem> mData;
	private int mLayoutViewResourceId;

	public ResponseArrayAdapter(Context context, int layoutViewResourceId, List<ResponseListItem> data) {
		super(context, layoutViewResourceId, data);
		mData = data;
		mLayoutViewResourceId = layoutViewResourceId;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		final ResponseListItem object = mData.get(position);

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
			
			//Random fix for first click. Not sure what the significance of 366 is.
			View initialExpandLayout = (LinearLayout) convertView.findViewById(R.id.response_expanding_layout);
			initialExpandLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			MarginLayoutParams layoutParams = (MarginLayoutParams) initialExpandLayout.getLayoutParams();
			layoutParams.bottomMargin = -1 * initialExpandLayout.getMeasuredHeight() - 366;
			initialExpandLayout.setLayoutParams(layoutParams);
			initialExpandLayout.setVisibility(View.GONE);
		}

		final TextView titleView = (TextView) convertView.findViewById(R.id.response_title);
		final TextView textView = (TextView) convertView.findViewById(R.id.response_body);
		final Button buttonMore = (Button) convertView.findViewById(R.id.response_btn_more);

		titleView.setText(object.getTitle());
		textView.setText(object.getDescription());

		//Set onClick() for More button:
		buttonMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getContext(), ResponseGuideActivity.class);
				intent.putExtra(ManualFragment.RESPONSELISTITEM_REQUEST, object);
				getContext().startActivity(intent);
			}
		});

		//Set onClick() for View:
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final View layout = v.findViewById(R.id.response_expanding_layout);
				final boolean visibleAfter = layout.getVisibility() == View.GONE;
				
				//Animating expansion:
				ExpandAnimation animation = new ExpandAnimation(layout, EXPAND_ANIMATION_DURATION);
				animation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						v.setEnabled(false);
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						//Scroll the ListView:
						if (visibleAfter) {
							((ListView) parent).smoothScrollToPositionFromTop(position, 0, SCROLL_ANIMATION_DURATION);
						}

						v.setEnabled(true);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
				layout.startAnimation(animation);
			}
		});

		return convertView;
	}
	
	@Override
	public int getViewTypeCount() {
		return getCount();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}
}