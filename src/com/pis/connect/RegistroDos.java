package com.pis.connect;


import java.util.EnumSet;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.widget.LoginButton;
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
public class RegistroDos extends FragmentActivity {
	private FacebookFragment mainFragment;
	public static Activity fa;
	String name;
	String mail;
	String facebook_id = "";
	String linkedin_id="";
	String pass;
	String user_id;
	String user_mail;
	String idLin;
	ProgressBar pbar;

	Button boton_link;
	
	LinkedInOAuthService oAuthService;
	LinkedInApiClientFactory factory;
	LinkedInRequestToken liToken;
	
	LinkedInAccessToken accessToken = null;
	LinkedInApiClient client;
	LoginButton facebutton;
	Button registrarbutton;
	

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa=this;		
		Intent intent = getIntent();
		//idLin = intent.getStringExtra("idLinkedin");
		//Obtengo los datos de la pantalla anterior de registro
		name= intent.getStringExtra("name");
		mail= intent.getStringExtra("mail");
		pass= intent.getStringExtra("pass");
		setContentView(R.layout.activity_registro_dos);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		boton_link = (Button) findViewById(R.id.Button_Linkedin);
		TextView link_sync= (TextView)findViewById(R.id.text_link_sync);
		boton_link.setVisibility(Button.VISIBLE);
		link_sync.setVisibility(TextView.INVISIBLE);
		if (!(linkedin_id == "")) {
		//	linkedin_id = idLin;
			boton_link.setVisibility(Button.INVISIBLE);
			link_sync.setVisibility(TextView.VISIBLE);
		}
		
//		if (LogLinkedIn.fa!=null)
//			LogLinkedIn.fa.finish();
		
		if (linkedin_id != ""){
			Log.i("ID- AL FIN!!: ", linkedin_id);
		}

		//Boton de facebook
		if (savedInstanceState == null) {
	        // Add the fragment on initial activity setup
	        mainFragment = new FacebookFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    } else {
	        // Or set the fragment from restored state info
	        mainFragment = (FacebookFragment) getSupportFragmentManager()
	        .findFragmentById(android.R.id.content);
	    }
		registrarbutton= (Button) findViewById(R.id.Button_Registrar);
		facebutton= (LoginButton) findViewById(R.id.authButton);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }
	
	
	public void conectarLinkedin(View view){
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo i = conMgr.getActiveNetworkInfo();
		  if ((i == null) || (!i.isConnected()) || (!i.isAvailable()))
		    	Toast.makeText(getApplicationContext(), R.string.connection_error , Toast.LENGTH_LONG).show();
		  else{
				boton_link.setClickable(false);
				loginLinkedIn();
		  }		
	}
	
	//Al hacer click en registrar
	public void registrar(View view){
		ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo i = conMgr.getActiveNetworkInfo();
		  if ((i == null) || (!i.isConnected()) || (!i.isAvailable()))
		    	Toast.makeText(getApplicationContext(), R.string.connection_error , Toast.LENGTH_LONG).show();
		  else{
		    	pbar = (ProgressBar) findViewById(R.id.progressBar1);
		    	pbar.setVisibility(view.VISIBLE);
				boton_link.setClickable(false);
				facebutton.setClickable(false);
				registrarbutton.setClickable(false);
				//Primero se busca el username de Facebook asincronamente y luego se continua el registro
				if ((Session.getActiveSession()==null)){
					facebook_id="";
					String [] parametros={"NOFB"};
					new web().execute(parametros);
				}
				else if (Session.getActiveSession().getState().toString().compareTo("OPENED")!=0){
							facebook_id="";
							String [] parametros={"NOFB"};
							new web().execute(parametros);
				}
				else{
					String [] parametros=null;
					new web().execute(parametros);
				}	
		  }
	}
	
	
	
	//CONEXION CON FACEBOOK
	
	  private String getusername(){
	      Session session = Session.getActiveSession();
	      Request request = Request.newGraphPathRequest(session, "me", null);
	      com.facebook.Response response = Request.executeAndWait(request);
		try {
			return response.getGraphObject().getInnerJSONObject().getString("username");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
			
	  
	  }
	  
	  private class web extends AsyncTask<String[], Void, String[]>{
			protected String [] doInBackground(String[]... arg0) {
				// TODO Auto-generated method stub
				if (arg0[0]==null){
					String [] res = {getusername()};
					return res;
				}
				else{
					return null;
				}
			}
			@Override
			protected void onPostExecute(String[] result){
	            super.onPostExecute(result);
	            if (result!=null)
	            	facebook_id=result[0];
	            else
	            	facebook_id="";	            
	            
	    		//Hago la llamada al server
	        	String [] parametros = {name,mail,facebook_id, linkedin_id, pass};
	        	new consumidorPost().execute(parametros);	
			}
			
			
			private class consumidorPost extends AsyncTask<String[], Void, String[]>{
				protected String[] doInBackground(String[]... arg0) {
					// TODO Auto-generated method stub
					WSSignUp wssignup = new WSSignUp();
					String [] res = wssignup.llamarServer(arg0[0][0],arg0[0][1],arg0[0][2],arg0[0][3],arg0[0][4]);
					return res;
				}
				
				@Override
				protected void onPostExecute(String[] result){
		            super.onPostExecute(result);
		            pbar.setVisibility(pbar.INVISIBLE);
		            int result_code = Integer.parseInt(result[0]);
					if (result_code==200){
						//Registro exitoso
						//Actualizamos variables globales
						name=result[2];
						user_id=result[1];
						user_mail=result[3];
						//Guardamos el user como logueado
						SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
						pref.edit().putBoolean("log_in", true).commit();
						pref.edit().putString("user_name", name).commit();
						pref.edit().putString("user_id", user_id).commit();
						pref.edit().putString("user_mail", user_mail).commit();
						//Paso a la siguiente activity
						Intent intent_name = new Intent();
						intent_name.setClass(getApplicationContext(),Share.class);
						intent_name.putExtra("name", name);
						intent_name.putExtra("id", user_id);
						startActivity(intent_name);
					}
					else if (result_code==410){
						//USUARIO EXISTE
						//Vuelvo a la pantalla de registro
						if (Registro.fa!=null)
							Registro.fa.finish();
						Intent intent_name = new Intent();
						intent_name.setClass(getApplicationContext(),Registro.class);
						intent_name.putExtra("ocupado", true);
						startActivity(intent_name);
					}
					else{
				    	Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
					}
					boton_link.setClickable(true);
					facebutton.setClickable(true);
					registrarbutton.setClickable(true);
			
				}
			}
	
	  }
	  
	  private void loginLinkedIn(){
			ProgressDialog progressDialog = new ProgressDialog(RegistroDos.this);
			LinkedinDialog d = new LinkedinDialog(RegistroDos.this,progressDialog);
			d.show();
			
			
			d.setOnKeyListener(new Dialog.OnKeyListener() {

	            @Override
	            public boolean onKey(DialogInterface arg0, int keyCode,
	                    KeyEvent event) {
	                // TODO Auto-generated method stub
	                if (keyCode == KeyEvent.KEYCODE_BACK) {
						boton_link.setClickable(true);
						arg0.dismiss();
	                }
	                return false;

	            }
	        });

			d.setVerifierListener(new OnVerifyListener() {
				@Override
				public void onVerify(String verifier) {
					
					try {

						Log.i("LinkedinSample", "verifier: " + verifier);
						accessToken = LinkedinDialog.oAuthService.getOAuthAccessToken(LinkedinDialog.liToken,verifier);

						
						SharedPreferences settings = getApplicationContext().getSharedPreferences("accessToken", 0);
				    	SharedPreferences.Editor editor = settings.edit();
				    	editor.putString("token", accessToken.getToken());
				    	editor.putString("tokenSecret", accessToken.getTokenSecret());
				    	
				    	// Commit the edits!
				    	editor.commit();
				    	
						Log.i("LogLin-Token", accessToken.getToken());
						Log.i("LogLin-Secret", accessToken.getTokenSecret());


						client = LinkedinDialog.factory.createLinkedInApiClient(accessToken);

						Log.i("LinkedinSample","ln_access_token: " + accessToken.getToken());
						Log.i("LinkedinSample","ln_access_token: " + accessToken.getTokenSecret());
						
						Person p = client.getProfileForCurrentUser(EnumSet.of(
				                ProfileField.ID, ProfileField.FIRST_NAME, ProfileField.EMAIL_ADDRESS,
				                ProfileField.LAST_NAME, ProfileField.HEADLINE,
				                ProfileField.INDUSTRY, ProfileField.PICTURE_URL, ProfileField.SITE_STANDARD_PROFILE_REQUEST, ProfileField.SITE_STANDARD_PROFILE_REQUEST_URL,
				                ProfileField.DATE_OF_BIRTH, ProfileField.LOCATION_NAME, ProfileField.API_STANDARD_PROFILE_REQUEST,
				                ProfileField.MAIN_ADDRESS, ProfileField.LOCATION_COUNTRY, ProfileField.API_STANDARD_PROFILE_REQUEST_URL));
											
						linkedin_id = p.getSiteStandardProfileRequest().getUrl();
						
						Log.i("linkedin_id: ",linkedin_id);
						
						Button boton_link = (Button) findViewById(R.id.Button_Linkedin);
						TextView link_sync= (TextView)findViewById(R.id.text_link_sync);
						
						boton_link.setVisibility(Button.INVISIBLE);
						link_sync.setVisibility(TextView.VISIBLE);
						boton_link.setClickable(true);

						
					} catch (Exception e) {
						Log.i("LinkedinSample", "error to get verifier");
						linkedin_id="";
						boton_link.setClickable(true);

						e.printStackTrace();
					}
//					if (RegistroDos.fa!=null)
//						RegistroDos.fa.finish();
					
				}
			});
			
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
			
			
		}
}
