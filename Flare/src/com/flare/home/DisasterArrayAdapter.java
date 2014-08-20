package com.flare.home;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.flare.R;

public class DisasterArrayAdapter extends ArrayAdapter<Disaster> {
	private List<Disaster> mData;
	private int mLayoutViewResourceId;
	
	public DisasterArrayAdapter(Context context, int layoutViewResourceId, List<Disaster> data) {
		super(context, layoutViewResourceId, data);
		mData = data;
		mLayoutViewResourceId = layoutViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Disaster object = mData.get(position);
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			convertView = inflater.inflate(mLayoutViewResourceId, parent, false);
		}
		
		String disaster = object.getDisaster();
		String time = object.getDate();
		String city = object.getPlace();

		TextView textView = (TextView) convertView.findViewById(R.id.disaster_text);
		textView.setText(Html.fromHtml(disaster + "<br><small>" + time + " @ " + city + "</small>"));

		return convertView;
	}
	
	public void setData(List<Disaster> data) {
		mData.clear();
		mData.addAll(data);
		notifyDataSetChanged();
	}
}