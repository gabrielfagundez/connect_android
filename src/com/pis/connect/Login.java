package com.pis.connect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class Login extends Activity {
	String user_name;
	String user_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		EditText mail = (EditText) findViewById(R.id.editText_mail);
		EditText password = (EditText) findViewById(R.id.editText_password);
		mail.setText("");
		password.setText("");
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
	
	@Override
	public void onBackPressed(){
		this.finish();
		return;
	}
	
	public void login(View view) {
		//Obtengo usuario y pass
		EditText mail = (EditText) findViewById(R.id.editText_mail);
	    String mail_str = mail.getText().toString();
		EditText password = (EditText) findViewById(R.id.editText_password);
	    String password_str = password.getText().toString();
	    //Si algún campo está vacio, evito la llamada al server
	    if ((mail_str.compareTo("")==0) || (password_str.compareTo("")==0)){
	    	Toast.makeText(getApplicationContext(), R.string.invalid_user_pass , Toast.LENGTH_LONG).show();
	    }
	    else{
	    	//Hago la llamada al server
	    	String [] parametros = {mail_str,password_str};
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
	
	public String getData(String user){
		try {
		    HttpClient client = new DefaultHttpClient();  
		    String getURL = "http://connectwp.azurewebsites.net/api/Users/".concat(user.toString()) ;
		    HttpGet get = new HttpGet(getURL);
		    HttpResponse responseGet = client.execute(get);  
		    HttpEntity resEntityGet = responseGet.getEntity();  
		    if (resEntityGet != null) {  
		        // do something with the response
		        String response = EntityUtils.toString(resEntityGet);
		        Log.i("GET RESPONSE", response);
		        return response;
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		    Log.i("GET RESPONSE", e.toString());
		}
		return "";
	}
	
	
	
    private class consumidorGet extends AsyncTask<String, Void, String>{
   	 
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return getData(arg0[0]);
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
			if (result==200){
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),Share.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("id", user_id);
				startActivity(intent_name);
			}
			else {
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),Login.class);
				//intent_name.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent_name);
			}
		}

    }
}
