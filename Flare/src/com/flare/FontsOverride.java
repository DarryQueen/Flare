package com.flare;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Typeface;

public final class FontsOverride {

	public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(), "font/" + fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
		try {
			final Field StaticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
			StaticField.setAccessible(true);
			StaticField.set(null, newTypeface);
			
			//Log possible fields:
			/*
			Field[] fields = Typeface.class.getDeclaredFields();
			String s = "";
			for (Field field: fields) {
				s += field.toString() + " ";
			}
			Log.i("Strings", s);
			*/
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}