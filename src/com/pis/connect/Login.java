package com.pis.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;



public class Login extends Activity {
	public static Activity fa;
	String user_name;
	String user_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa = this;
		//Veo si ya está logueado
		SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
		boolean logueado = pref.getBoolean("log_in", false);
		if (logueado){
			Intent intent_name = new Intent();
			intent_name.setClass(getApplicationContext(),Share.class);
			intent_name.putExtra("name", pref.getString("user_name", ""));
			intent_name.putExtra("id", pref.getString("user_id", ""));
			startActivity(intent_name);
			
		}
		else{
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
			setContentView(R.layout.activity_login);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//Escondo los iconos del menú de logout y settings
		menu.findItem(R.id.action_logout).setVisible(false);
		menu.findItem(R.id.action_settings).setVisible(false);
		return true;
	}
	
	
	public void login(View view) {
		//Obtengo usuario y pass
		EditText mail = (EditText) findViewById(R.id.editText_mail);
	    String mail_str = mail.getText().toString();
		EditText password = (EditText) findViewById(R.id.editText_password);
	    String password_str = password.getText().toString();
	    //Si algún campo está vacio, evito la llamada al server
	    if ((mail_str.compareTo("")==0) || (password_str.compareTo("")==0)){
	    	Toast.makeText(getApplicationContext(), R.string.blank_fields , Toast.LENGTH_LONG).show();
	    }
	    else{
	    	//Hago la llamada al server
	    	String [] parametros = {mail_str,password_str};
	    	setProgressBarIndeterminateVisibility(true);
	    	new consumidorPost().execute(parametros);
	    }
		
	}
	
	public void register(View view) {
		//Redirijo a la página para Registrarse
		Intent intent = new Intent(this, Registro.class);
		startActivity(intent);
	}
	
		
	
	public int postData(String user, String pass) {
		
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://connectwp.azurewebsites.net/api/login/");
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("Email", user));
	        nameValuePairs.add(new BasicNameValuePair("Password", pass));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        //Obtengo el código de la respuesta http
	        int response_code = response.getStatusLine().getStatusCode();
	        //Obtengo el nombre de usuario
	        if (response_code==200){
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        String json = reader.readLine();
		        JSONTokener tokener = new JSONTokener(json);
		        try {
					JSONObject finalResult = new JSONObject(tokener);
			        user_name=finalResult.get("Name").toString();
			        user_id=finalResult.get("Id").toString();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        
	        return response_code;

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	return -1;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	return -1;
	    }
	} 
	

    private class consumidorPost extends AsyncTask<String[], Void, Long>{
		protected Long doInBackground(String[]... arg0) {
			// TODO Auto-generated method stub
			return (long) postData(arg0[0][0],arg0[0][1]);
		}
		
		@Override
		protected void onPostExecute(Long result){
            super.onPostExecute(result);
            setProgressBarIndeterminateVisibility(false);
			if (result==200){
				//Login exitoso
				//Guardamos el user como logueado
				SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
				pref.edit().putBoolean("log_in", true).commit();
				pref.edit().putString("user_name", user_name).commit();
				pref.edit().putString("user_id", user_id).commit();
				//Paso a la siguiente activity
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),Share.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("id", user_id);
				startActivity(intent_name);
			}
			else if (result==404) {
				//USUARIO NO ENCONTRADO
				//Borro los campos y pongo el foco en el primero
				EditText mail = (EditText) findViewById(R.id.editText_mail);
				EditText password = (EditText) findViewById(R.id.editText_password);
				mail.setText("");
				password.setText("");
				mail.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.user_not_found , Toast.LENGTH_LONG).show();
			}
			else if (result==401){
				//PASSWORD INCORRECTO
				//Borro el campo de password y pongo foco en el
				EditText password = (EditText) findViewById(R.id.editText_password);
				password.setText("");
				password.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_LONG).show();
			}
			else{
				//OTRO TIPO DE ERROR
				//PASSWORD INCORRECTO
				//Borro los campos y pongo el foco en el primero
				EditText mail = (EditText) findViewById(R.id.editText_mail);
				EditText password = (EditText) findViewById(R.id.editText_password);
				mail.setText("");
				password.setText("");
				mail.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
				
			}
		}

    }
}
