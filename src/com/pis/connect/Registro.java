package com.pis.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Registro extends Activity {

public static Activity fa;//Esto permite matar la activity desde afuera
String user_name;
String user_mail;
String user_pass;
ProgressBar pbar;
Button next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if (intent.getBooleanExtra("ocupado", false))
			Toast.makeText(getApplicationContext(), R.string.user_exists, Toast.LENGTH_LONG).show();
		if (RegistroDos.fa!=null)
			RegistroDos.fa.finish();
		fa=this;
		setContentView(R.layout.activity_registro);
		next=(Button) findViewById(R.id.Button_Registrar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	    //Campos vacios
	    if ((nombre_str.compareTo("")==0) || (mail_str.compareTo("")==0) || (password_str.compareTo("")==0) || (password2_str.compareTo("")==0)){
	    	
	    	Toast toast= Toast.makeText(getApplicationContext(), R.string.blank_fields, Toast.LENGTH_LONG);
	    	toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 10);
	    	toast.show();
	    }
	    else{
	    	//Passwords no coinciden
		    if (password_str.compareTo(password2_str)!=0){
		    	password.setText("");
		    	password2.setText("");
		    	password.requestFocus();
		    	Toast.makeText(getApplicationContext(), R.string.passwords_no_coinciden, Toast.LENGTH_LONG).show();
		    }
		    else {
		    	if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail_str).matches()){
			    	password.setText("");
			    	password2.setText("");
			    	mail.setText("");
			    	mail.requestFocus();
			    	Toast.makeText(getApplicationContext(), R.string.mail_invalido, Toast.LENGTH_LONG).show();
		    	}
		    	else 
		    		if (password_str.length()<6){
		    			password.setText("");
				    	password2.setText("");
				    	password.requestFocus();
				    	Toast.makeText(getApplicationContext(), R.string.password_short, Toast.LENGTH_LONG).show();

			    	}
			    	else{
				    	//Campos OK, llamada al server
				    	//Actualizo las variables globales
				    	user_name=nombre_str;
				    	user_mail= mail_str;
				    	user_pass=password_str;				    	
				    	//Verifico si esta disponible el mail
				    	String [] parametros = {mail_str};
				    	pbar = (ProgressBar) findViewById(R.id.progressBar1);
				    	pbar.setVisibility(view.VISIBLE);
				    	next.setClickable(false);
				    	new consumidorPost().execute(parametros);
			    		
			    	}
		    }
	    }

	}
	
	//RUTINAS DE LLAMADA AL SERVER PARA CHEQUEAR USUARIO DISPONIBLE
	private class consumidorPost extends AsyncTask<String[], Void, Long>{
		
		protected Long doInBackground(String[]... arg0) {
			// TODO Auto-generated method stub
			WSLogin wslogin = new WSLogin();
			String [] res = wslogin.llamarServer(arg0[0][0], "");
			return (Long.parseLong(res[0]));
		}
		
		@Override
		protected void onPostExecute(Long result){
	        super.onPostExecute(result);
	        pbar.setVisibility(pbar.INVISIBLE);
	        next.setClickable(true);
	        //User disponible
			if (result==404){
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),RegistroDos.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("mail", user_mail);
				intent_name.putExtra("pass", user_pass);
				startActivity(intent_name);
			}
			//User ya registrado
			else {
		    	user_name="";
		    	user_mail= "";
		    	user_pass="";

		    	if (result==401){
		    		Toast.makeText(getApplicationContext(), R.string.user_exists , Toast.LENGTH_LONG).show();
			    	EditText nombre = (EditText) findViewById(R.id.editText_nombre);
			    	EditText mail = (EditText) findViewById(R.id.editText_mail);
			    	EditText pass = (EditText) findViewById(R.id.editText_password);
			    	EditText pass2 = (EditText) findViewById(R.id.editText_password2);
			    	nombre.setText("");
			    	mail.setText("");
			    	pass.setText("");
			    	pass2.setText("");
			    	nombre.requestFocus();
		    	}
		    	else{
		    		Toast.makeText(getApplicationContext(), R.string.connection_error , Toast.LENGTH_LONG).show();
			    	EditText pass = (EditText) findViewById(R.id.editText_password);
			    	EditText pass2 = (EditText) findViewById(R.id.editText_password2);
			    	pass.setText("");
			    	pass2.setText("");
			    	pass.requestFocus();
		    	}	
			}
		}
	}
}