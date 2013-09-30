package testing;

import org.junit.Test;

import junit.framework.TestCase;
import com.pis.connect.WSLogin;

public class testWSLogin extends TestCase {
    WSLogin tester = new WSLogin();
    String [] res;
	//Casos de prueba
	String[][] valid_users = {
			{"gabriel.fa07@gmail.com", "pass"},
			{"renzomassobrio@gmail.com", "pass"},
			{"fab.kremer@gmail.com","pass"}
	};	
	
	String[][] wrong_pass = {
			{"gabriel.fa07@gmail.com", "2pass"},
			{"renzomassobrio@gmail.com", "passsssñ"},
			{"fab.kremer@gmail.com","pass4"}
	};	
	
	String[][] wrong_users = {
			{"", "2pass"},
			{"renzomassobrio", "2"},
			{"fab.kremer","pass"},
			{"",""}		
	};
	
	
	@Test
	public void testValidUsers() {
	    for(int i=0; i<valid_users.length; i++){
	    	res = tester.llamarServer(valid_users[i][0],valid_users[i][1]);
	    	assertEquals("Codigo 200 en Login exitoso", "200", res[0]);
	    	assertEquals("Mail correcto", valid_users[i][0], res[3]);
	    	assertEquals("Pass correcto", valid_users[i][1], res[6]);
	    	assertEquals("Largo respuesta=7",7,res.length);
	    	for (int j=0;j<=6;j++){
	    		System.out.print(res[j]);
	    		System.out.print(", ");
	    	}
	    	System.out.println();
	    }
	
	} 
	
	@Test
	public void testWrongPass() {
	    for(int i=0; i<wrong_pass.length; i++){
	    	res = tester.llamarServer(wrong_pass[i][0],wrong_pass[i][1]);
	    	assertEquals("Codigo 401 en pass incorrecta", "401", res[0]);
	    	assertEquals("ID vacio", "", res[1]);
	    	assertEquals("Name vacio", "", res[2]);
	    	assertEquals("Mail vacio", "", res[3]);
	    	assertEquals("Face vacio", "", res[4]);
	    	assertEquals("Linkedin vacio", "", res[5]);
	    	assertEquals("Pass vacio", "", res[6]); 
	    	assertEquals("Largo respuesta=7",7,res.length);
	    	for (int j=0;j<=6;j++){
	    		System.out.print(res[j]);
	    		System.out.print(", ");
	    	}
	    	System.out.println();
	    }
	
	} 
	
	
	@Test
	public void testWrongUser() {
	    for(int i=0; i<wrong_users.length; i++){
	    	res = tester.llamarServer(wrong_users[i][0],wrong_users[i][1]);
	    	assertEquals("Codigo 404 user not found", "404", res[0]);
	    	assertEquals("ID vacio", "", res[1]);
	    	assertEquals("Name vacio", "", res[2]);
	    	assertEquals("Mail vacio", "", res[3]);
	    	assertEquals("Face vacio", "", res[4]);
	    	assertEquals("Linkedin vacio", "", res[5]);
	    	assertEquals("Pass vacio", "", res[6]); 
	    	assertEquals("Largo respuesta=7",7,res.length);
	    	for (int j=0;j<=6;j++){
	    		System.out.print(res[j]);
	    		System.out.print(", ");
	    	}
	    	System.out.println();
	    }
	
	} 

}
