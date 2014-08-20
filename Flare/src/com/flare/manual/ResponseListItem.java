package com.flare.manual;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponseListItem implements Parcelable {
	private String mTitle;
	private String mDescription;
	private String mBefore;
	private String mDuring;
	private String mAfter;

	public ResponseListItem(String t, String desc, String b, String d, String a) {
		mTitle = t;
		mDescription = desc;
		mBefore = b;
		mDuring = d;
		mAfter = a;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getDescription() {
		return mDescription;
	}
	
	public String getBefore() {
		return mBefore;
	}
	
	public String getDuring() {
		return mDuring;
	}
	
	public String getAfter() {
		return mAfter;
	}

	//Methods needed to pass in as Parcelable:
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(mTitle);
		out.writeString(mDescription);
		out.writeString(mBefore);
		out.writeString(mDuring);
		out.writeString(mAfter);
	}
	
	public static final Parcelable.Creator<ResponseListItem> CREATOR = new Parcelable.Creator<ResponseListItem>() {
		public ResponseListItem createFromParcel(Parcel in) {
			return new ResponseListItem(in);
		}

		public ResponseListItem[] newArray(int size) {
			return new ResponseListItem[size];
		}
	};

	private ResponseListItem(Parcel in) {
		mTitle = in.readString();
		mDescription = in.readString();
		mBefore = in.readString();
		mDuring = in.readString();
		mAfter = in.readString();
	}
}