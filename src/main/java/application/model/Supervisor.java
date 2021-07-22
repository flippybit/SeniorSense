package application.model;

public class Supervisor extends UsuarioImpl{
	
	public Supervisor()
	{
		this(null,null);
	}
 
	public Supervisor(String name,String password)
	{
	  super(name,password);
	}

 
}
