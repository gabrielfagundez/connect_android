package com.pis.connect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;



public class Login extends Activity {
	
	public static Activity fa;//Esto permite matar la activity desde afuera
	String user_name;
	String user_id;
	String user_mail;
	ProgressBar pbar;
	Button login;
	Button signup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fa = this;
		
		//Veo si ya está logueado
		SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
		boolean logueado = pref.getBoolean("log_in", false);
		if (logueado){
			//Redirijo a la activity con el QR
			Intent intent_name = new Intent();
			intent_name.setClass(getApplicationContext(),Share.class);
			intent_name.putExtra("name", pref.getString("user_name", ""));
			intent_name.putExtra("id", pref.getString("user_id", ""));
			startActivity(intent_name);
			
		}
		else{
			setContentView(R.layout.activity_login);
			login=(Button) findViewById(R.id.Button_Registrar);
			signup=(Button) findViewById(R.id.button_register);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	public void login(View view) {
		//RUTINA AL APRETAR EL BOTON DE LOGIN
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
	    	pbar = (ProgressBar) findViewById(R.id.progressBar1);
	    	pbar.setVisibility(view.VISIBLE);
	    	login.setClickable(false);
	    	signup.setClickable(false);
	    	new consumidorPost().execute(parametros);
	    }
		
	}
	
	//RUTINA AL APRETAR EL BOTON DE REGISTRAR UNA NUEVA CUENTA
	public void register(View view) {
		//Redirijo a la página para Registrarse
		Intent intent = new Intent(this, Registro.class);
		startActivity(intent);
	}
	
		
	//METODOS LLAMADOS PARA HACER EL LOGIN
    private class consumidorPost extends AsyncTask<String[], Void, String[]>{
		protected String[] doInBackground(String[]... arg0) {
			// TODO Auto-generated method stub
			WSLogin wslogin= new WSLogin();
			String[] res= wslogin.llamarServer(arg0[0][0], arg0[0][1]);
			return res;
		}
		
		@Override
		protected void onPostExecute(String[] result){
            super.onPostExecute(result);
            int codigo_res = Integer.parseInt(result[0]);
			if (codigo_res==200){
				//Login exitoso
				//Actualizamos las variables globales
				user_id=result[1];
				user_name=result[2];
				user_mail=result[3];
				
				//Guardamos el user como logueado
				SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
				pref.edit().putBoolean("log_in", true).commit();
				pref.edit().putString("user_name", user_name).commit();
				pref.edit().putString("user_id", user_id).commit();
				pref.edit().putString("user_mail", user_mail).commit();
				//Actualizo UI
	            pbar.setVisibility(pbar.INVISIBLE);	    
	            login.setClickable(true);
		    	signup.setClickable(true);
				//Paso a la siguiente activity
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),Share.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("id", user_id);
				startActivity(intent_name);
			}
			else if (codigo_res==404) {
				//USUARIO NO ENCONTRADO
				//Borro los campos y pongo el foco en el primero
				EditText mail = (EditText) findViewById(R.id.editText_mail);
				EditText password = (EditText) findViewById(R.id.editText_password);
				mail.setText("");
				password.setText("");
				mail.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.user_not_found , Toast.LENGTH_LONG).show();
			}
			else if (codigo_res==401){
				//PASSWORD INCORRECTO
				//Borro el campo de password y pongo foco en el
				EditText password = (EditText) findViewById(R.id.editText_password);
				password.setText("");
				password.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_LONG).show();
			}
			else{
				//OTRO TIPO DE ERROR
				//Borro los campos y pongo el foco en el primero
				EditText mail = (EditText) findViewById(R.id.editText_mail);
				EditText password = (EditText) findViewById(R.id.editText_password);
				mail.setText("");
				password.setText("");
				mail.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.connection_error, Toast.LENGTH_LONG).show();
				
			}
            pbar.setVisibility(pbar.INVISIBLE);	    
            login.setClickable(true);
	    	signup.setClickable(true);

		}

    }
}
