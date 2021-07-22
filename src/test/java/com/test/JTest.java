package com.test;

import static org.junit.Assert.*;

import org.junit.Test;

import application.container.UserMemoryContainer;
import application.exception.UserException;
import application.model.Administrador;
import application.model.Paciente;

public class JTest {

	@Test
	public void test() {
		UserMemoryContainer ucontainer = new UserMemoryContainer();
		Paciente paciente1 =new Paciente("paciente","mypass2", 0);
		//registro paciente  
		
        Administrador admin1 = new Administrador("admin","mypass2");
      
		
		
		 try {
				assertTrue(ucontainer.register(paciente1));
				assertTrue(ucontainer.register(admin1));
				 ;
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
		 try {
			equals(ucontainer.login("paciente", "mypass2"));
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
