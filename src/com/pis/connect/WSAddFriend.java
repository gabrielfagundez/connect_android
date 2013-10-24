package com.pis.connect;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WSAddFriend {

	public String[] llamarServer(String userId, String mailFrom) {
		
		String res_codigo="";
		String res_name="";
		String res_mail="";
		String res_facebookid="";
		String res_linkedinid="";
		
		try {
			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("server.properties"));
			String server = prop.getProperty("getUser");
			String QrCode = URLEncoder.encode(userId, "UTF-8");
			
			HttpClient client = new DefaultHttpClient();
			String URL = server + QrCode;
			
			String SetServerString = "";
			HttpGet httpget = new HttpGet(URL);
			HttpResponse response;
			
			response = client.execute(httpget);
			//Obtengo el código de la respuesta http
	        int response_code = response.getStatusLine().getStatusCode();
	        
	        if (response_code==200){
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		        String json = reader.readLine();
		        JSONTokener tokener = new JSONTokener(json);
		        try {
					JSONObject finalResult = new JSONObject(tokener);
					
			        String mailTo = finalResult.get("Mail").toString();
			        
					prop.load(getClass().getResourceAsStream("server.properties"));
					String serverAdd = prop.getProperty("addfriend");
			    	// Build the JSON object to pass parameters
			    	JSONObject jsonObj = new JSONObject();
			    	jsonObj.put("MailFrom", mailFrom);
			    	jsonObj.put("MailTo", mailTo);
			    	
			    	// Create the POST object and add the parameters
			    	HttpPost httpPost = new HttpPost(serverAdd);
			    	StringEntity entity = new StringEntity(jsonObj.toString(), HTTP.UTF_8);
			    	entity.setContentType("application/json");
			    	httpPost.setEntity(entity);
			    	HttpResponse responseAdd = client.execute(httpPost);
			        
			    	//Obtengo el código de la respuesta http
			        int response_code_add = responseAdd.getStatusLine().getStatusCode();
			        //Obtengo el nombre de usuario
			        if (response_code_add==200){
			        	BufferedReader readerAdd = new BufferedReader(new InputStreamReader(responseAdd.getEntity().getContent(), "UTF-8"));
				        String jsonAdd = readerAdd.readLine();
				        JSONTokener tokenerAdd = new JSONTokener(jsonAdd);
				        try {
							JSONObject finalResultAdd = new JSONObject(tokenerAdd);
													
							res_name=finalResultAdd.get("Name").toString();
							res_mail=finalResultAdd.get("Mail").toString();
							res_facebookid=finalResultAdd.get("FacebookId").toString();
							res_linkedinid=finalResultAdd.get("LinkedInId").toString();
							
				        }catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        
				        
			        }
			        
			        res_codigo=Integer.toString(response_code_add);

			        String[] result= {res_codigo,res_name, res_mail, res_facebookid, res_linkedinid};
			        return result;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
	        	
	        }
			        
	        res_codigo=Integer.toString(response_code);

	        String[] result= {res_codigo,res_name, res_mail, res_facebookid, res_linkedinid};
	        return result;
	        
		} catch (UnsupportedEncodingException e) {
			String[] result={"-1"};
	    	return result;
		} catch (IOException e) {
			String[] result={"-1"};
	    	return result;
		}
	}
}
