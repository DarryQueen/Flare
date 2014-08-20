package com.flare.validator;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Form validation class.
 * As of right now, only works with EditText.
 */
public class Form {
	protected ArrayList<AbstractValidate> _validates = new ArrayList<AbstractValidate>();
	
	/*
	 * Function adding Validate objects to our form.
	 * 
	 * @param validate:
	 * 		Validate to add.
	 */
	public void addValidates(AbstractValidate validate) {
		_validates.add(validate);
	}

	/*
	 * Called to validate our form.
     * If an error is found, it will be displayed in the corresponding field.
     * 
	 * @return:
	 * 		True if the form is valid.
	 * 		False if the form is invalid.
	 */
    public boolean validate(){
        boolean result = true;
        int validator = 0;
        Iterator<AbstractValidate> it = this._validates.iterator();
        while(it.hasNext()){
            AbstractValidate validate = it.next();
            result = validate.isValid();
            if (!result){
                validator++;
            }
        }
        if (validator > 0){
            result = false;
        }
        return result;
    }
}