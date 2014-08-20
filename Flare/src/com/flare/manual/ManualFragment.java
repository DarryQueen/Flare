package com.flare.manual;

import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.flare.R;

//Note: In order for the ListView to work, there must be a certain number of elements, depending on each element's size! Fucking piece of shit.

public class ManualFragment extends Fragment {
	private View rootView;
	
	public final static String RESPONSELISTITEM_REQUEST = "com.flare.home.ManualFragment.RESPONSELISTITEM_REQUEST";
	
	private ListAdapter mAdapter;
	private ListView mListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView != null) {
			return rootView;
		}
		rootView = inflater.inflate(R.layout.fragment_manual, container, false);

		//Create ResponseListItem objects:
		List<ResponseListItem> responseItems = ResponseRetriever.getItems(getActivity());;

		//Create adapters and ListViews:
		mAdapter = new ResponseArrayAdapter(getActivity(), R.layout.listview_manual, responseItems);
		mListView = (ListView) rootView.findViewById(R.id.lv_manual);
		mListView.setAdapter(mAdapter);

		return rootView;
	}
}