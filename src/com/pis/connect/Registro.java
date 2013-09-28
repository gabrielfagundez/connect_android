package com.pis.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registro extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		//Valido los datos
		EditText mail = (EditText) findViewById(R.id.editText_mail);
	    String mail_str = mail.getText().toString();
		EditText password = (EditText) findViewById(R.id.editText_password);
	    String password_str = password.getText().toString();
		EditText password2 = (EditText) findViewById(R.id.editText_password2);
	    String password2_str = password2.getText().toString();
	    if (password_str.compareTo(password2_str)==0){
	    	//Redirijo a la siguiente pagina de registro
			Intent intent = new Intent(this, RegistroDos.class);
			startActivity(intent);
	    }
	    else{
	    	Toast.makeText(getApplicationContext(), "Passwords don't match. Try again :)", Toast.LENGTH_LONG).show();

	    }


	}
}

