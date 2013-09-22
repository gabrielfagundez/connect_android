package com.pis.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class Share extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	            // go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(this, Login.class);
	            startActivity(intent);
	            return true;
	         //Al apretar el boton de settings
	    }
	    return super.onOptionsItemSelected(item);
	}
}
