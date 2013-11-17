package com.pis.connect;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

public class ShowInfoFriend extends Activity {

	String user_name;
    String user_mail;
    String user_facebookId;
    String user_linkedInId;
    public static Activity fa;//Esto permite matar la activity desde afuera
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_info_friend);
		
		fa=this;
		
		//Obtengo los datos de la pantalla anterior de registro
		Intent intent = getIntent();
		user_name= intent.getStringExtra("name");
		user_mail= intent.getStringExtra("mail");
		user_facebookId= intent.getStringExtra("facebookId");
		user_linkedInId = intent.getStringExtra("linkedInId");		
		
		TextView user= (TextView) findViewById(R.id.textView3);
		TextView mail = (TextView) findViewById(R.id.textView5);
		user.setText(user_name);
		mail.setText(user_mail);
	
	}
	
	public void addToFacebook(View view) {
		//Redirijo a la página para Registrarse
		if (user_facebookId==null || user_facebookId.compareTo("")==0)
			Toast.makeText(getApplicationContext(), R.string.nofacebook , Toast.LENGTH_LONG).show();
		else{
			String urlFb = "http://www.facebook.com/" + user_facebookId;
			Uri uri = Uri.parse(urlFb);
			Intent intentOut = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intentOut);
		}
	}
	
	public void addToLinkedIn(View view){	
			if (user_linkedInId==null || user_linkedInId.compareTo("")==0)
				Toast.makeText(getApplicationContext(), R.string.nolinkedin , Toast.LENGTH_LONG).show();
			else{
				String urlFb = user_linkedInId;
				Uri uri = Uri.parse(urlFb);
				Intent intentOut = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intentOut);
			}
	}
	
	@Override
	public void onBackPressed() {
		if (Share.fa !=null)
			Share.fa.finish();
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(),Share.class);
		intent_name.putExtra("vineparaatras", true);
		startActivity(intent_name);
		finish();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_info_friend, menu);
		return true;
	}

	
	public void ok (View view){

		finish();
	}
	
	
	public void addphonebook (View view){
    	new AlertDialog.Builder(this)
        .setMessage(getResources().getString(R.string.confirm_add1)+" "+ user_name+" " + getResources().getString(R.string.confirm_add2))
        .setCancelable(true)
        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	boolean ok=agregarcontacto(user_name, user_mail);	 
            	if (ok)
    		    	Toast.makeText(getApplicationContext(), R.string.okphonebook , Toast.LENGTH_LONG).show();
            	else
    		    	Toast.makeText(getApplicationContext(), R.string.errphonebook , Toast.LENGTH_LONG).show();

            }
        })
        .setNegativeButton(getResources().getString(R.string.NO), null)
        .show();
	}
	
	
	
	public boolean agregarcontacto (String name, String mail){
		 String DisplayName = name;
		 String emailID = mail;


		 ArrayList < ContentProviderOperation > ops = new ArrayList < ContentProviderOperation > ();

		 ops.add(ContentProviderOperation.newInsert(
		 ContactsContract.RawContacts.CONTENT_URI)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
		     .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
		     .build());

		 //------------------------------------------------------ Names
		 if (DisplayName != null) {
		     ops.add(ContentProviderOperation.newInsert(
		     ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
		         .withValue(
		     ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
		     DisplayName).build());
		 }
		 //------------------------------------------------------ Email
		 if (emailID != null) {
		     ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
		         .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
		         .withValue(ContactsContract.Data.MIMETYPE,
		     ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
		         .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
		         .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
		         .build());
		 }

		 // Asking the Contact provider to create a new contact                 
		 try {
		     getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
		     return true;
		 } catch (Exception e) {
		     e.printStackTrace();
		     return false;
		 } 
	}
	
	
	
	

}
