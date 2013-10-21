package com.pis.connect;

import java.util.EnumSet;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;
import com.google.code.linkedinapi.schema.Person;
import com.pis.connect.LinkedinDialog.OnVerifyListener;

public class ShowInfoFriend extends Activity {

	String user_name;
    String user_mail;
    String user_facebookId;
    String user_linkedInId;
    
    LinkedInOAuthService oAuthService;
	LinkedInApiClientFactory factory;
	LinkedInRequestToken liToken;
	LinkedInAccessToken accessToken = null;
    	
	LinkedInApiClient client;
	
	Boolean yaAgregue;
	Boolean agregar;
    
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_info_friend);
		// Show the Up button in the action bar.
		setupActionBar();
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Obtengo los datos de la pantalla anterior de registro
		Intent intent = getIntent();
		user_name= intent.getStringExtra("name");
		user_mail= intent.getStringExtra("mail");
		user_facebookId= intent.getStringExtra("facebookId");
		user_linkedInId = intent.getStringExtra("linkedInId");
		agregar = intent.getBooleanExtra("yaAgregue", false);
		if (!yaAgregue){
			yaAgregue = agregar;
		}
		
		TextView usrName = (TextView) findViewById(R.id.friendName);
		TextView usrMail = (TextView) findViewById(R.id.friendMail);
		
		usrName.setText(user_name);
		usrMail.setText(user_mail);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	public void addToFacebook(View view) {
		//Redirijo a la página para Registrarse
		Intent intent = getIntent();
		String user_id= intent.getStringExtra("facebookId");
		String urlFb = "http://www.facebook.com/" + user_id;
		Uri uri = Uri.parse(urlFb);
		Intent intentOut = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intentOut);
	}
	
	public void addToLinkedIn(View view){	
		if (yaAgregue){
			Toast.makeText(getApplicationContext(), R.string.linkedInSolicitudEnviada , Toast.LENGTH_LONG).show();
		}else{
			ProgressDialog progressDialog = new ProgressDialog(ShowInfoFriend.this);
			LinkedinDialog d = new LinkedinDialog(ShowInfoFriend.this,progressDialog);
			d.show();
	
			d.setVerifierListener(new OnVerifyListener() {
				@Override
				public void onVerify(String verifier) {
					Intent intent = getIntent();
					String user_id= intent.getStringExtra("linkedInId");
	
					Intent newIntent = new Intent();
					newIntent.setClass(getApplicationContext(),ShowInfoFriend.class);
	
					try {
						accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken,verifier);
						client = LinkedinDialog.factory.createLinkedInApiClient(accessToken);
		
						Person p = client.getProfileForCurrentUser(EnumSet.of(
				                ProfileField.ID, ProfileField.FIRST_NAME, ProfileField.EMAIL_ADDRESS,
				                ProfileField.LAST_NAME, ProfileField.HEADLINE,
				                ProfileField.INDUSTRY, ProfileField.PICTURE_URL,
				                ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME,
				                ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY));					
						
						client.sendInviteByEmail(user_id, p.getFirstName(), p.getLastName(),"New LinkedIn Connection", "Hello, add me to your connections");
						
					} catch (Exception e) {
						Log.i("LinkedinSample", "error to get verifier");
						e.printStackTrace();
					}
					if (RegistroDos.fa!=null)
						RegistroDos.fa.finish();
					intent.putExtra("yaAgregue", true);
					startActivity(intent);
					
				}
			});
			
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		
		
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_info_friend, menu);
		return true;
	}

	@Override
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
	}

}
