package com.flare.validator;

import java.util.regex.Pattern;

import android.util.Patterns;

public class UrlValidator extends AbstractValidator {
    private static Pattern mPattern = Patterns.WEB_URL;

    public UrlValidator(String errorMessage) {
        super(errorMessage);
    }

	@Override
	public boolean isValid(String url) {
		return mPattern.matcher(url).matches();
	}
}