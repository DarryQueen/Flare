package com.flare.validator;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateValidator extends AbstractValidator {
	public DateValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	public boolean isValid(String value) throws ValidatorException {
		if (value == null) {
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
		sdf.setLenient(false);

		try {
			sdf.parse(value);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
}