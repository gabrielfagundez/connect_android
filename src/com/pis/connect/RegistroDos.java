package com.pis.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroDos extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		//Conectar a facebook y poner el nombre de usuario en el textview
	}

	
	public void conectarLinkedin(View view){
		//Conectar a LinkedIn 
		
		
		//Poner el nombre de usuario en el textview
		TextView nom_usuario = (TextView) findViewById(R.id.text_Linkedin);
		//nom_usuario.setText("NOMBRE DE USUARIO DE LINKEDIN");
	}
}
