package testing;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import com.pis.connect.WSLogin;
import com.pis.connect.WSSignUp;

import org.junit.Test;

public class testWSSignUp extends TestCase {
	WSSignUp tester = new WSSignUp();
    String [] res;
	//Casos de prueba
	String[][] free_users = {
			{"Renzo Massobrio","hola21212@ejemplo.com","renzomassobrio", "", "pass"},
			{"","hola22222@mail.com","a", "", ""},
			{"","hola22222","b", "", ""}

	};	
	
	String[][] used_users = {
			{"","gabriel.fa07@gmail.com","","", "pass"},
			{"Renzo","renzomassobrio@gmail.com","a","b","pass"},
			{"","fab.kremer@gmail.com","","","pass"}
	};	
		
	
	@Test
	public void testFreeUsers() {
	    for(int i=0; i<free_users.length; i++){
	    	res = tester.llamarServer(free_users[i][0],free_users[i][1],free_users[i][2],free_users[i][3],free_users[i][4]);
	    	for (int j=0;j<=6;j++){
	    		System.out.print(res[j]);
	    		System.out.print(", ");
	    	}
	    	System.out.println();
	    	assertEquals("Codigo 200 en Login exitoso", "200", res[0]);
	    	assertEquals("Nombre correcto", free_users[i][0], res[2]);
	    	assertEquals("Mail correcto", free_users[i][1], res[3]);
	    	assertEquals("FacebookId correcto", free_users[i][2], "");
	    	assertEquals("Linkedinid correcto", free_users[i][3], "");
	    	assertEquals("Pass correcto", free_users[i][4], res[6]);
	    	assertEquals("Largo respuesta=7",7,res.length);

	    }
	
	} 
	
	@Test
	public void testWrongPass() {
	    for(int i=0; i<used_users.length; i++){
	    	res = tester.llamarServer(used_users[i][0],used_users[i][1],used_users[i][2],used_users[i][3],used_users[i][4]);
	    	assertEquals("Codigo 410 si usuario existe", "410", res[0]);
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
