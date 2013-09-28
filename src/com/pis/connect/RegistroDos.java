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
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroDos extends Activity {
	public static Activity fa;
	String name;
	String mail;
	String facebook_id;
	String linkedin_id;
	String pass;
	String user_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa=this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		Intent intent = getIntent();
		name= intent.getStringExtra("name");
		mail= intent.getStringExtra("mail");
		pass= intent.getStringExtra("pass");
		setContentView(R.layout.activity_registro_dos);
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
	
	
	public void conectarFacebook(View view){
		//Conectar a facebook y poner el nombre de usuario en el textview y variable global
		facebook_id="";
	}

	
	public void conectarLinkedin(View view){
		//Conectar a LinkedIn 
		
		
		//Al terminar de conectar, poner el nombre de usuario de linked in la siguiente variable global
		//Si no se conecto poner el string vacio
		linkedin_id="";
		
		//Poner el nombre de usuario en el textview tambien
		TextView nom_usuario = (TextView) findViewById(R.id.text_Linkedin);
		//nom_usuario.setText("NOMBRE DE USUARIO DE LINKEDIN");
	}
	
	public void registrar(View view){
		//Hago la llamada al server
    	String [] parametros = {name,mail,facebook_id, linkedin_id, pass};
    	setProgressBarIndeterminateVisibility(true);
    	new consumidorPost().execute(parametros);		
		
	}
	
	
	
	public int postData(String name, String email, String face_id, String link_id, String pass ) {
		
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://connectwp.azurewebsites.net/api/SignUp/");
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("Name", name));
	        nameValuePairs.add(new BasicNameValuePair("Email", email));
	        nameValuePairs.add(new BasicNameValuePair("FacebookId", face_id));
	        nameValuePairs.add(new BasicNameValuePair("LinkedInId", link_id));
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
			        name=finalResult.get("Name").toString();
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
			return (long) postData(arg0[0][0],arg0[0][1],arg0[0][2],arg0[0][3],arg0[0][4]);
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
				pref.edit().putString("user_name", name).commit();
				pref.edit().putString("user_id", user_id).commit();
				//Paso a la siguiente activity
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),Share.class);
				intent_name.putExtra("name", name);
				intent_name.putExtra("id", user_id);
				startActivity(intent_name);
			}
			else {
				//USUARIO EXISTE
				//Vuelvo a la pantalla de registro
			}
	
		}
	}
	
	
}
