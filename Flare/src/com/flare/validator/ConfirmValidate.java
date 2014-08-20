package com.flare.validator;

import android.widget.TextView;

public class ConfirmValidate extends AbstractValidate {

	private TextView _field1;
	private TextView _field2;
	private TextView source;
	private String _errorMessage = "Fields are not identical.";
	
	public ConfirmValidate(TextView field1, TextView field2) {
		this._field1 = field1;
		this._field2 = field2;
		source = _field2;
	}

	@Override
	public boolean isValid(String value) {
		if(_field1.getText().toString().length() > 0 && _field1.getText().toString().equals(_field2.getText().toString())){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public String getMessages() {
		return _errorMessage;
	}

	@Override
	public void addValidator(AbstractValidator validator) {
	}

	@Override
	public TextView getSource() {
		return source;
	}
}
