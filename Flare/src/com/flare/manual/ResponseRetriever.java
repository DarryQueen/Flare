package com.flare.manual;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

public class ResponseRetriever {

	public static List<ResponseListItem> getItems(Activity a) {
		String files[] = getTitles(a);
		ArrayList<ResponseListItem> items = new ArrayList<ResponseListItem>();
		for (int i = 0; i < files.length; i++) {
			String text = getText(a, files[i]);
			
			//Get the title:
			String title = files[i];
			//Get the description:
			String descrip = getTaggedString(text, "DESCRIP");
			//Get the Before text:
			String before = getTaggedString(text, "BEFORE");
			//Get the During text:
			String during = getTaggedString(text, "DURING");
			//Get the After text:
			String after = getTaggedString(text, "AFTER");
			
			items.add(new ResponseListItem(title, descrip, before, during, after));
		}
		return items;
	}
	
	private static String getTaggedString(String text, String tag) {
		int firstIndex = text.toLowerCase(Locale.getDefault()).indexOf("<" + tag.toLowerCase() + ">");
		int secondIndex = text.toLowerCase(Locale.getDefault()).indexOf("</" + tag.toLowerCase() + ">");
		
		//Error checking:
		if (firstIndex == -1 && secondIndex == -1) {
			Log.i("Manual Parser", "Could not find tag " + tag + ".");
			return "";
		}
		if (firstIndex == -1 || secondIndex == -1) {
			Log.i("Manual Parser", "Unclosed tag " + tag + ".");
			return "";
		}
		if (secondIndex < firstIndex) {
			Log.i("Manual Parser", "Incorrect tag format " + tag + ".");
			return "";
		}
		return text.substring(firstIndex + ("<" + tag + ">").length(), secondIndex).trim();
	}

	private static String[] getTitles(Activity a) {
		String files[] = null;
		try {
			files = a.getResources().getAssets().list("manual");
		} catch (Exception e) {
		}
		return files;
	}

	private static String getText(Activity a, String fileName) {
		AssetManager assets = a.getAssets();
		String text = "";
		BufferedReader buff = null;
		try {
			buff = new BufferedReader(new InputStreamReader(assets.open("manual/" + fileName)));
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("File " + fileName + " not found.");
		}

		try {
			String inputLine = buff.readLine();
			while (inputLine != null) {
				text += inputLine + "\n";
				inputLine = buff.readLine();
			}
			text = text.trim();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not read file " + fileName + ".");
		} finally {
			try {
				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Couldn't close BufferedReader.");
			}
		}
		return text;
	}
}
