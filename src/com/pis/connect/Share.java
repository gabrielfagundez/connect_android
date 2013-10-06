package com.pis.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class Share extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Login.fa !=null)
			Login.fa.finish();
		if (Registro.fa!=null)
			Registro.fa.finish();
		if (RegistroDos.fa!=null)
			RegistroDos.fa.finish();
		Intent intent = getIntent();
		String name= intent.getStringExtra("name");
		String id= intent.getStringExtra("id");
    	Toast.makeText(getApplicationContext(),name.concat(id) , Toast.LENGTH_LONG).show();
		setContentView(R.layout.activity_share);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	//Manejo de los botones de la Action Bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	//Al apretar el boton de logout
	        case R.id.action_logout:
	        	new AlertDialog.Builder(this)
	            .setMessage("Are you sure you want to exit?")
	            .setCancelable(true)
	            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	    	        	//Actualizo las preferencias
	    	        	SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
	    				pref.edit().putBoolean("log_in", false).commit();
	    				pref.edit().putString("user_name", "").commit();
	    				pref.edit().putString("user_id", "").commit();
	    	            // go to previous screen when app icon in action bar is clicked
	    	            Intent intent = new Intent(getApplicationContext(), Login.class);
	    	            startActivity(intent);
	    	            finish();
	    	            	
	                }
	            })
	            .setNegativeButton("No", null)
	            .show();
	        	return true;
	         //Al apretar el boton de settings
	    }
	    return super.onOptionsItemSelected(item);
	}
}
