package com.flare.validator;

import java.util.ArrayList;
import java.util.Iterator;

import android.widget.TextView;

public class Validate extends AbstractValidate{
    protected ArrayList<AbstractValidator> _validators = new ArrayList<AbstractValidator>();
    protected String _message = "";
    protected TextView _source;
    
    public Validate(TextView source){
    	this._source = source;
    }

    public void addValidator(AbstractValidator validator)
    {
    	this._validators.add(validator);
    	return;
    }
    
    public boolean isValid(String value) {
    	boolean result = true;
    	this._message = new String();
    	
    	Iterator<AbstractValidator> it = this._validators.iterator();
    	while(it.hasNext()) {
    		AbstractValidator validator = it.next();
            try {
                if (!validator.isValid(value)) {
                    this._message = validator.getMessage();
                    result = false;
                    break;
                }
            } catch(ValidatorException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getStackTrace());
                this._message = e.getMessage();
                result = false;
                break;
            }
    	}
    	return result;
    }
    
    public String getMessages(){
    	return this._message;
    }
    
    public TextView getSource(){
    	return this._source;
    }
}