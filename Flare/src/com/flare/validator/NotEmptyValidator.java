package com.flare.validator;


public class NotEmptyValidator extends AbstractValidator {

    public NotEmptyValidator(String errorMessage) {
        super(errorMessage);
    }

	@Override
	public boolean isValid(String value) {
		if (value != null){
			if(value.length() > 0)
				return true;
			else
				return false;
		} else {
			return false;
		}
	}
}