package com.flare.validator;

import java.util.regex.Pattern;

/*
 * Validator to check if Phone number is correct.
 */
public class PhoneValidator extends AbstractValidator{
    public PhoneValidator(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public boolean isValid(String value) throws ValidatorException {
        return Pattern.matches("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", value);
    }
}