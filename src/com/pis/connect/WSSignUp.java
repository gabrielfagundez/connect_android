package com.pis.connect;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WSSignUp {
	/*Esta metodo hace la llamada al servidor para el caso del Sign Up, devuelve un array de string
	 * que contiene en la posicion 0 el codigo http retornado por el servidor, y luego cada uno de 
	 * los campos que devuelve el signup en respuesta. Si el codigo no es 200 los demas campos son
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

	public String[] llamarServer(String name, String email, String face_id, String link_id, String pass ) {
		String res_codigo="";
		String res_id="";
		String res_name="";
		String res_mail="";
		String res_facebookid="";
		String res_linkedinid="";
		String res_password="";
		
	    // Create a new HttpClient and Post Header
	   /* Este es el codigo que teniamos antes por cualquier cosa:
	    * 
	    	HttpClient httpclient = new DefaultHttpClient();
	    	HttpPost httppost = new HttpPost("http://developmentpis.azurewebsites.net/api/Users/SignUp/");
	  		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
	        nameValuePairs.add(new BasicNameValuePair("Name", name));
	        nameValuePairs.add(new BasicNameValuePair("Mail", email));
	        nameValuePairs.add(new BasicNameValuePair("FacebookId", face_id));
	        nameValuePairs.add(new BasicNameValuePair("LinkedInId", link_id));
	        nameValuePairs.add(new BasicNameValuePair("Password", pass));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	    */
	    try {
	    	Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("server.properties"));
			String server = prop.getProperty("signup");
	    	// Build the JSON object to pass parameters
	    	JSONObject jsonObj = new JSONObject();
	    	jsonObj.put("Name", name);
	    	jsonObj.put("Mail", email);
	    	jsonObj.put("FacebookId", face_id);
	    	jsonObj.put("LinkedInId", link_id);
	    	jsonObj.put("Password", pass);
	    	// Create the POST object and add the parameters
	    	HttpPost httpPost = new HttpPost(server);
	    	StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
	    	entity.setContentType("application/json");
	    	httpPost.setEntity(entity);
	    	HttpClient client = new DefaultHttpClient();
	    	HttpResponse response = client.execute(httpPost);
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
	    } catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
	    	String[] result={"-1"};
	    	return result;
		}
	} 
}
