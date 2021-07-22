package application.model;

import application.interfaces.Usuario;

public class Administrador extends UsuarioImpl{
	
	
	public Administrador()
	{
		this(null,null);
	}
 
	public Administrador(String name,String password)
	{
	  super(name,password);
	}
}
