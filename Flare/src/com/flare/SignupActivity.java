package com.flare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.flare.validator.DateValidator;
import com.flare.validator.EmailValidator;
import com.flare.validator.Form;
import com.flare.validator.NotEmptyValidator;
import com.flare.validator.PhoneValidator;
import com.flare.validator.Validate;
import com.parse.ParseObject;

public class SignupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.signup_container, new PlaceholderFragment()).commit();
		}
	}

	public static class PlaceholderFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

			//Get EditTexts:
			final EditText firstnameView = (EditText) rootView.findViewById(R.id.signup_txtview_firstname);
			final EditText lastnameView = (EditText) rootView.findViewById(R.id.signup_txtview_lastname);
			final EditText emailView = (EditText) rootView.findViewById(R.id.signup_txtview_email);
			final EditText phoneView = (EditText) rootView.findViewById(R.id.signup_txtview_phone);
			final EditText birthdayView = (EditText) rootView.findViewById(R.id.signup_txtview_birthday);

			//Set Validator objects:
			Validate firstnameField = new Validate(firstnameView);
			firstnameField.addValidator(new NotEmptyValidator("Field cannot be empty."));
			Validate lastnameField = new Validate(lastnameView);
			lastnameField.addValidator(new NotEmptyValidator("Field cannot be empty."));
			Validate emailField = new Validate(emailView);
			emailField.addValidator(new NotEmptyValidator("Field cannot be empty."));
			emailField.addValidator(new EmailValidator("Invalid email."));
			Validate phoneField = new Validate(phoneView);
			phoneField.addValidator(new NotEmptyValidator("Field cannot be empty."));
			phoneField.addValidator(new PhoneValidator("Invalid phone number."));
			Validate birthdayField = new Validate(birthdayView);
			birthdayField.addValidator(new NotEmptyValidator("Field cannot be empty."));
			birthdayField.addValidator(new DateValidator("Invalid birthday."));
			
			//Set Form:
			final Form form = new Form();
			form.addValidates(firstnameField);
			form.addValidates(lastnameField);
			form.addValidates(emailField);
			form.addValidates(phoneField);
			form.addValidates(birthdayField);
			
			Button submitButton = (Button) rootView.findViewById(R.id.signup_btn_submit);
			submitButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (form.validate()) {
						//Get fields and format:
						String device = Secure.getString(getActivity().getContentResolver(), Secure.ANDROID_ID);
						String firstname = "" + firstnameView.getText();
						String lastname = "" + lastnameView.getText();
						String email = "" + emailView.getText();
						String phone = "" + phoneView.getText();
						String birthday = "" + birthdayView.getText();

						//Put fields in shared preferences:
						SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();

						editor.putBoolean(MainActivity.HAS_SIGNED_UP, true);
						editor.putString(MainActivity.USER_FIRSTNAME, firstname);
						editor.putString(MainActivity.USER_LASTNAME, lastname);
						editor.putString(MainActivity.USER_EMAIL, email);
						editor.putString(MainActivity.USER_PHONE, phone);
						editor.putString(MainActivity.USER_BIRTHDAY, birthday);

						editor.commit();
						
						//Push to Parse:
						new SaveDeviceIdTask().execute(device, firstname, lastname, email, phone, birthday);

						//Restart the application:
						Intent intent = new Intent(getActivity(), MainActivity.class);
						getActivity().startActivity(intent);

						getActivity().finish();
					}
				}
			});

			return rootView;
		}
	}
	
	private static class SaveDeviceIdTask extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... strings) {
			ParseObject parseObject = new ParseObject("UserAccount");
			parseObject.put("deviceId", strings[0]);
			parseObject.put("firstname", strings[1]);
			parseObject.put("lastname", strings[2]);
			parseObject.put("email", strings[3]);
			parseObject.put("phone", strings[4]);
			parseObject.put("birthday", strings[5]);
			parseObject.saveInBackground();

			return null;
		}
	}
}