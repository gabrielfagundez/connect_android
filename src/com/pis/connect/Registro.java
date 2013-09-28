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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends Activity {

public static Activity fa;
String user_name;
String user_mail;
String user_pass;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa=this;
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_registro);
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

	public void register(View view){
		//Obtengo los campos
		EditText nombre = (EditText) findViewById(R.id.editText_nombre);
	    String nombre_str = nombre.getText().toString();
		EditText mail = (EditText) findViewById(R.id.editText_mail);
	    String mail_str = mail.getText().toString();
		EditText password = (EditText) findViewById(R.id.editText_password);
	    String password_str = password.getText().toString();
		EditText password2 = (EditText) findViewById(R.id.editText_password2);
	    String password2_str = password2.getText().toString();
	    //Validacion
	    if ((nombre_str.compareTo("")==0) || (mail_str.compareTo("")==0)
	    		|| (password_str.compareTo("")==0) || (password2_str.compareTo("")==0)){
	    	
	    	Toast.makeText(getApplicationContext(), R.string.blank_fields, Toast.LENGTH_LONG).show();
	    }
	    else{
		    if (password_str.compareTo(password2_str)!=0){
		    	password.setText("");
		    	password2.setText("");
		    	password.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.passwords_no_coinciden, Toast.LENGTH_LONG).show();
		    }
		    else{
		    	//Actualizo las variables globales
		    	user_name=nombre_str;
		    	user_mail= mail_str;
		    	user_pass=password_str;				    	
		    	//Verifico si esta disponible el mail
		    	String [] parametros = {mail_str};
		    	setProgressBarIndeterminateVisibility(true);
		    	new consumidorPost().execute(parametros);
		    		
		    }
	    }

	}
	
	
	public int postData(String mail) {		
	    // Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://connectwp.azurewebsites.net/api/login/");
	    try {
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("Email", mail));
	        nameValuePairs.add(new BasicNameValuePair("Password", ""));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        //Obtengo el código de la respuesta http
	        int response_code = response.getStatusLine().getStatusCode();	        
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
			return (long) postData(arg0[0][0]);
		}
		
		@Override
		protected void onPostExecute(Long result){
	        super.onPostExecute(result);
	        setProgressBarIndeterminateVisibility(false);
	        setProgressBarIndeterminateVisibility(false);
			if (result==404){
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),RegistroDos.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("mail", user_mail);
				intent_name.putExtra("pass", user_pass);
				startActivity(intent_name);
			}
			else{
		    	user_name="";
		    	user_mail= "";
		    	user_pass="";
		    	EditText nombre = (EditText) findViewById(R.id.editText_nombre);
		    	EditText mail = (EditText) findViewById(R.id.editText_mail);
		    	EditText pass = (EditText) findViewById(R.id.editText_password);
		    	EditText pass2 = (EditText) findViewById(R.id.editText_password2);
		    	nombre.setText("");
		    	mail.setText("");
		    	pass.setText("");
		    	pass2.setText("");
		    	nombre.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.user_exists , Toast.LENGTH_LONG).show();
			}
		}
	}
}