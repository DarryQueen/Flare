package com.flare.validator;

public abstract class AbstractValidator {
	protected String mErrorMessage;
	
	public AbstractValidator(String errorMessage) {
		mErrorMessage = errorMessage;
	}
	
	/*
	 * Check if the value passed in parameter is valid or not.
	 * 
	 * @param value:
	 * 		Value to validate.
	 * @return:
	 * 		True if valid.
	 * 		False otherwise.
	 */
	public abstract boolean isValid(String value) throws ValidatorException;
	
	/*
	 * Used to retrieve the error message corresponding to the validator.
	 * @return:
	 * 		The error message
	 */
	public String getMessage() {
		return mErrorMessage;
	}
}
