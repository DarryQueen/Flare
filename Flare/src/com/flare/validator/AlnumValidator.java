package com.flare.validator;

import java.util.regex.Pattern;

/*
 * Validator to check if a field contains only numbers and letters.
 * Avoids having special characters like accents.
 */
public class AlnumValidator extends AbstractValidator{
    //Alnum pattern to verify value:
    private static final Pattern mPattern = Pattern.compile("^[A-Za-z0-9]+$");

    public AlnumValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(String value) {
        return mPattern.matcher(value).matches();
    }
}
