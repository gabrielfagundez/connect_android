package com.pis.connect;

import java.util.EnumSet;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.pis.connect.LinkedinDialog.OnVerifyListener;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class LogLinkedIn extends Activity {

	LinkedInOAuthService oAuthService;
	LinkedInApiClientFactory factory;
	LinkedInRequestToken liToken;
	
	LinkedInAccessToken accessToken = null;
	LinkedInApiClient client;
	String linkedin_id="";
	Button bot;
	
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_linked_in);
		//Show the Up button in the action bar.
		//setupActionBar();
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		    
		loginLinkedIn();
		
		
	}
	
	private void loginLinkedIn(){
		ProgressDialog progressDialog = new ProgressDialog(LogLinkedIn.this);
		LinkedinDialog d = new LinkedinDialog(LogLinkedIn.this,progressDialog);
		d.show();
		
		d.setVerifierListener(new OnVerifyListener() {
			@Override
			public void onVerify(String verifier) {
				Intent intent = getIntent();
				intent.setClass(getApplicationContext(),RegistroDos.class);

				try {
					Log.i("LinkedinSample", "verifier: " + verifier);
					accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken,verifier);

					
					SharedPreferences settings = getApplicationContext().getSharedPreferences("accessToken", 0);
			    	SharedPreferences.Editor editor = settings.edit();
			    	editor.putString("token", accessToken.getToken());
			    	editor.putString("tokenSecret", accessToken.getTokenSecret());
			    	
			    	// Commit the edits!
			    	editor.commit();
			    	
					Log.i("AccessToken", accessToken.getToken());
					Log.i("PRUEBA", "LLEGA1");

					client = LinkedinDialog.factory.createLinkedInApiClient(accessToken);
					Log.i("PRUEBA", "LLEGA2");

					Log.i("LinkedinSample","ln_access_token: " + accessToken.getToken());
					Log.i("LinkedinSample","ln_access_token: " + accessToken.getTokenSecret());
					
					Person p = client.getProfileForCurrentUser(EnumSet.of(
			                ProfileField.ID, ProfileField.FIRST_NAME,
			                ProfileField.LAST_NAME, ProfileField.HEADLINE,
			                ProfileField.INDUSTRY, ProfileField.PICTURE_URL,
			                ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME,
			                ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY));
					
					Log.i("Nombreee: ",p.getFirstName() +" "+ p.getLastName());
					
					linkedin_id = p.getId();
					
					Log.i("linkedin_id: ",linkedin_id);
					//share.setVisibility(0);
					//et.setVisibility(0);

					intent.putExtra("idLinkedin", linkedin_id);
					
					
					
				} catch (Exception e) {
					Log.i("LinkedinSample", "error to get verifier");
					linkedin_id="";
					e.printStackTrace();
				}
				
				startActivity(intent);
				
			}
		});
		
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(true);
		progressDialog.show();
		
		
	}
	
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
/*
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_linked_in, menu);
		return true;
	}

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

}
