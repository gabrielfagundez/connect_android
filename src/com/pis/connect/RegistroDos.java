package com.pis.connect;


import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Session;

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

	Button buttonLinkedIn;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa=this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);		
		Intent intent = getIntent();
		idLin = intent.getStringExtra("idLinkedin");
		//Obtengo los datos de la pantalla anterior de registro
		name= intent.getStringExtra("name");
		mail= intent.getStringExtra("mail");
		pass= intent.getStringExtra("pass");
		setContentView(R.layout.activity_registro_dos);
		Button boton_link = (Button) findViewById(R.id.Button_Linkedin);
		TextView link_sync= (TextView)findViewById(R.id.text_link_sync);
		boton_link.setVisibility(Button.VISIBLE);
		link_sync.setVisibility(TextView.INVISIBLE);
		if (!(idLin == null)) {
			linkedin_id = idLin;
			boton_link.setVisibility(Button.INVISIBLE);
			link_sync.setVisibility(TextView.VISIBLE);
			if (LogLinkedIn.fa!=null)
				LogLinkedIn.fa.finish();
		}
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
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }
	
	
	public void conectarLinkedin(View view){
		
		Intent intent = getIntent();
		intent.setClass(getApplicationContext(),LogLinkedIn.class);
		startActivity(intent);
	}
	
	//Al hacer click en registrar
	public void registrar(View view){
    	setProgressBarIndeterminateVisibility(true);
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
		}
			return "";
	  
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
		            setProgressBarIndeterminateVisibility(false);
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
						pref.edit().putString("user_mail", user_mail);
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
			
				}
			}
	
	  }
}
