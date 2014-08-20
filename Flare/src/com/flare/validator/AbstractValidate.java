package com.flare.validator;

import android.widget.TextView;

public abstract class AbstractValidate {

	/*
	 * Add a new validator for fields attached.
	 * 
	 * @param validator:
	 * 		The validator to attach.
	 */
	public abstract void addValidator(AbstractValidator validator);
	
	/*
	 * Function called when the form validates.
	 * 
	 * @param value:
	 * 		Value to validate.
	 * @return:
	 * 		True if all validators are valid.
	 * 		False if a validator is invalid.
	 */
	public abstract boolean isValid(String value);

	public final boolean isValid() {
	        boolean valid = isValid(getSource().getText().toString());
	        if(valid)
	            getSource().setError(null);
	        else
	            getSource().setError(getMessages());
	        return valid;
    	}
    	
	public abstract String getMessages();
	
	/*
	 * Function recovering the field attached to our validator.
	 * 
	 * @return:
	 * 		The fields attached.
	 */
	public abstract TextView getSource();
}
