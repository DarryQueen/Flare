package com.flare.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator extends AbstractValidator {
	private String mDomainName = "";

    public EmailValidator(String errorMessage) {
        super(errorMessage);
    }

	@Override
	public boolean isValid(String charseq) {
		if (charseq.length() > 0 ){
			boolean matchFound = false;

			//Input the string for validation:
			String email = charseq.toString();

			if (mDomainName != null && mDomainName.length() > 0){
				//Test without the domain:

			    //Set the email pattern string:
			    Pattern p = Pattern.compile(".+@"+mDomainName);
			    //Match the given string with the pattern:
			    Matcher m = p.matcher(email);
			    //Check if match is found:
			    matchFound = m.matches();

			    if (matchFound)
			        return true;
			    else
			        return false;
			} else {
				//Test without the domain:

			    //Set the email pattern string:
			    Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
			    //Match the given string with the pattern:
			    Matcher m = p.matcher(email);
			    //Check if match is found:
			    matchFound = m.matches();
			}

			if (matchFound)
		        return true;
		    else
		        return false;
		} else {
			return true;
		}
	}

	/*
	 * Lets say that the email address must be valid for such domain.
     * This function only accepts strings of Regexp.
     * 
	 * @param name:
	 * 		Regexp domain name (like gmail.com).
	 */
	public void setDomainName(String name) {
		mDomainName = name;
	}
}