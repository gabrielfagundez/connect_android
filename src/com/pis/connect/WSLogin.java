package com.pis.connect;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WSLogin {
	
	/*Esta metodo hace la llamada al servidor para el caso de Login, devuelve un array de string
	 * que contiene en la posicion 0 el codigo http retornado por el servidor, y luego cada uno de 
	 * los campos que devuelve el login en respuesta. Si el codigo no es 200 los demas campos son
	 * strings vacios.
	 * 
	 * Los campos retornados son los siguientes y en este orden:
	 * 
	 * [0]--> Codigo http retornado
	 * [1]--> Id de usuario
	 * [2]--> Nombre
	 * [3]--> Mail
	 * [4]--> Id de facebook
	 * [5]-->Id de linkedin
	 * [6]--> Password
	 * */
	
	
public String[] llamarServer(String user, String pass) {
	
		String res_codigo="";
		String res_id="";
		String res_name="";
		String res_mail="";
		String res_facebookid="";
		String res_linkedinid="";
		String res_password="";


	    try {
	    	Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("server.properties"));
			String server = prop.getProperty("login");
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(server);
	        // Add your data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("Mail", user));
	        nameValuePairs.add(new BasicNameValuePair("Password", pass));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	      //Timeout
	    	HttpParams httpParameters = new BasicHttpParams();
	    	// Set the timeout in milliseconds until a connection is established.
	    	int timeoutConnection = 10000;
	    	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	    	// Set the default socket timeout (SO_TIMEOUT) 
	    	// in milliseconds which is the timeout for waiting for data.
	    	int timeoutSocket = 10000;
	    	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

	    	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	    	BasicHttpResponse response = (BasicHttpResponse)  httpClient.execute(httppost);
	        //Obtengo el código de la respuesta http
	        int response_code = response.getStatusLine().getStatusCode();
	        //Obtengo el nombre de usuario
	        if (response_code==200){
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        String json = reader.readLine();
		        JSONTokener tokener = new JSONTokener(json);
		        try {
					JSONObject finalResult = new JSONObject(tokener);
			        res_id=finalResult.get("Id").toString();
			        res_name=finalResult.get("Name").toString();
			        res_mail=finalResult.get("Mail").toString();
			        res_facebookid=finalResult.get("FacebookId").toString();
			        res_linkedinid=finalResult.get("LinkedInId").toString();
			        res_password=finalResult.get("Password").toString();  
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        }
	        res_codigo=Integer.toString(response_code);
	        String[] result= {res_codigo, res_id, res_name, res_mail, res_facebookid, res_linkedinid, res_password};
	        return result;

	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    	String[] result={"-1"};
	    	return result;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    	String[] result={"-1"};
	    	return result;
	    }
	} 
}
