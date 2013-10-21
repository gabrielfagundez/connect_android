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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class WSAddFriend {

	public String[] llamarServer(String userId) {
		
		String res_codigo="";
		String res_name="";
		String res_mail="";
		String res_facebookid="";
		String res_linkedinid="";
		
		try {
			Properties prop = new Properties();
			prop.load(getClass().getResourceAsStream("server.properties"));
			String server = prop.getProperty("addfriend");
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
			        
			        res_name = finalResult.get("Name").toString();
			        res_mail = finalResult.get("Mail").toString();
			        res_facebookid = finalResult.get("FacebookId").toString();
			        res_linkedinid = finalResult.get("LinkedInId").toString();
			       
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
