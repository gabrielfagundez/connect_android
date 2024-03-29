package com.pis.connect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class Share extends Activity {

	private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    public static Activity fa;//Esto permite matar la activity desde afuera
    String res_codigo;
    String user_name;
    String user_mail;
    String user_facebookId;
    String user_linkedInId;
    
    String mailFrom;
    ProgressBar pbar;
    ImageButton logoutbutton;
    ImageButton cambutton;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (Login.fa !=null)
			Login.fa.finish();
		if (Registro.fa!=null)
			Registro.fa.finish();
		if (RegistroDos.fa!=null)
			RegistroDos.fa.finish();
		if (Share.fa!=null)
			Share.fa.finish();
		if (ShowInfoFriend.fa!=null)
			ShowInfoFriend.fa.finish();
		fa=this;
		Intent intent = getIntent();
		boolean vineparaatras= intent.getExtras().getBoolean("vineparaatras", false);
		
		SharedPreferences settings = getApplicationContext().getSharedPreferences("prefs", 0);
		mailFrom = settings.getString("user_mail","");
        SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
		String id= pref.getString("user_id", "");
	    String dirFoto = "fotoQR";
	    Context context = this;
	    setContentView(R.layout.activity_share);
	    logoutbutton=(ImageButton) findViewById(R.id.Button_Linkedin);
	    cambutton= (ImageButton) findViewById(R.id.imageButton2);
		try {
	        // generate a 150x150 QR code
			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("server.properties"));
			String server = prop.getProperty("webqr");
			
	        Bitmap bm = encodeAsBitmap(server+id, BarcodeFormat.QR_CODE, 400, 400);
	        File file = new File(context.getFilesDir(), dirFoto);
	        
	        FileOutputStream fOut = new FileOutputStream(file);

	        bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
	        fOut.flush();
	        fOut.close();
	        
	        ImageView imgView;
	        imgView = (ImageView) findViewById(R.id.imagenQR);
	        imgView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
	        
	        
	    } catch (WriterException e) {  
	    	
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (vineparaatras)
			readQr ((View)cambutton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void readQr(View view){
		//Intent intent = new Intent(Share.this, CaptureActivity.class);
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				Properties prop = new Properties();
				try {
					prop.load(getClass().getResourceAsStream("server.properties"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String server = prop.getProperty("webqr");
				// Handle successful scan
				String capturedQrValue = data.getStringExtra("SCAN_RESULT");
				String[] res = capturedQrValue.split("id=");
				if (res[0].compareTo(server.split("id=")[0])==0){
					//Hago la llamada al server
			    	String [] parametros = {res[1], mailFrom};
			    	pbar = (ProgressBar) findViewById(R.id.progressBar1);
			    	pbar.setVisibility(pbar.VISIBLE);
			    	logoutbutton.setClickable(false);
			    	cambutton.setClickable(false);
			    	new consumidorPost().execute(parametros);
				}else{
					//USUARIO NO ENCONTRADO
			    	Toast toast=Toast.makeText(getApplicationContext(), R.string.user_not_found_share , Toast.LENGTH_LONG);
			    	toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			    	toast.show();
				}
					
			} else if (resultCode == RESULT_CANCELED) {
			// Handle cancel
			}
		} else {

		}
    }
	
	//METODOS LLAMADOS PARA HACER EL LOGIN
    private class consumidorPost extends AsyncTask<String[], Void, String[]>{
		protected String[] doInBackground(String[]... arg0) {
			// TODO Auto-generated method stub
			WSAddFriend wsAddFriend= new WSAddFriend();
			Log.i("consPost:", arg0[0][0]);
			String[] res = wsAddFriend.llamarServer(arg0[0][0],arg0[0][1]);
			return res;
		}
		
		@Override
		protected void onPostExecute(String[] result){
            super.onPostExecute(result);
	    	pbar.setVisibility(pbar.INVISIBLE);
            int codigo_res = Integer.parseInt(result[0]);
            SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
            String my_id= pref.getString("user_id", "");
			if (codigo_res==200 && my_id.compareTo(result[5])!=0){
				//Login exitoso
				//Actualizamos las variables globales
				user_name=result[1];
			    user_mail=result[2];
			    user_facebookId=result[3];
			    user_linkedInId=result[4];
				//Guardamos el user como logueado
				
				Intent intent_name = new Intent();
				intent_name.setClass(getApplicationContext(),ShowInfoFriend.class);
				intent_name.putExtra("name", user_name);
				intent_name.putExtra("mail", user_mail);
				intent_name.putExtra("facebookId", user_facebookId);
				intent_name.putExtra("linkedInId", user_linkedInId);
				startActivity(intent_name);
			}
			else if (codigo_res==404 || my_id.compareTo("")==0) {
				//USUARIO NO ENCONTRADO
		    	Toast toast=Toast.makeText(getApplicationContext(), R.string.user_not_found_share , Toast.LENGTH_LONG);
		    	toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		    	toast.show();
			}
			else{
				//OTRO TIPO DE ERROR				
		    	Toast toast=Toast.makeText(getApplicationContext(), R.string.errphonebook, Toast.LENGTH_LONG);
		    	toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
		    	toast.show();
			}
	    	logoutbutton.setClickable(true);
	    	cambutton.setClickable(true);
		}

    }


	public void logout (View view) {
    	new AlertDialog.Builder(this)
        .setMessage(getResources().getString(R.string.confirm_logout))
        .setCancelable(true)
        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
	        	//Actualizo las preferencias
	        	SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
				pref.edit().putBoolean("log_in", false).commit();
				pref.edit().putString("user_name", "").commit();
				pref.edit().putString("user_id", "").commit();
				pref.edit().putString("user_mail", "").commit();
	            // go to previous screen when app icon in action bar is clicked
	            Intent intent = new Intent(getApplicationContext(), Login.class);
	            startActivity(intent);
	            finish();
	            	
            }
        })
        .setNegativeButton(getResources().getString(R.string.NO), null)
        .show();

	}

	private static String guessAppropriateEncoding(CharSequence contents) {
	    // Very crude at the moment
	    for (int i = 0; i < contents.length(); i++) {
	        if (contents.charAt(i) > 0xFF) {
	        return "UTF-8";
	        }
	    }
	    return null;
	}
	
	Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
	    String contentsToEncode = contents;
	    if (contentsToEncode == null) {
	        return null;
	    }
	   // Map<EncodeHintType, Object> hints = null;
	    String encoding = guessAppropriateEncoding(contentsToEncode);
	    if (encoding != null) {
	      //  hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
	       // hints.put(EncodeHintType.CHARACTER_SET, encoding);
	    }
	    MultiFormatWriter writer = new MultiFormatWriter();
	    BitMatrix result;
	    try {
	        result = writer.encode(contentsToEncode, format, img_width, img_height);
	    } catch (IllegalArgumentException iae) {
	        // Unsupported format
	        return null;
	    }
	    int width = result.getWidth();
	    int height = result.getHeight();
	    int[] pixels = new int[width * height];
	    for (int y = 0; y < height; y++) {
	        int offset = y * width;
	        for (int x = 0; x < width; x++) {
	        pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
	        }
	    }

	    Bitmap bitmap = Bitmap.createBitmap(width, height,
	        Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
	    return bitmap;
	}
}
