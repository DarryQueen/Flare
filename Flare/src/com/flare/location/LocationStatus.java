package com.flare.location;

public class LocationStatus {
	private int mStatus;
	private int mPopulation;
	private String mMessage;
	
	//Levels of severity:
	public static final int STATUS_CLEAR = 0;
	public static final int STATUS_MEDIUM = 1;
	public static final int STATUS_SEVERE = 2;
	
	public LocationStatus() {
		this(STATUS_CLEAR, -1, "");
	}
	
	public LocationStatus(int s, int p, String m) {
		mStatus = s;
		mPopulation = p;
		mMessage = m;
	}
	
	public int getStatus() {
		return mStatus;
	}
	public int getPopulation() {
		return mPopulation;
	}
	public String getMessage() {
		return mMessage;
	}
}