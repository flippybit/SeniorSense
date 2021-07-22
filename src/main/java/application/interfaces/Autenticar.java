package application.interfaces;

import application.exception.UserException;

public interface Autenticar {
	
	
	public Usuario login(String user,String password) throws UserException;
	
	public boolean register(Usuario u) throws UserException;
	
	 
}
