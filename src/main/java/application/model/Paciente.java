package application.model;

import application.interfaces.Autenticar;
import application.interfaces.NatourDao;
import application.interfaces.Usuario;


@NatourDao.Class(parent=true)
public class Paciente extends UsuarioImpl {

	public Paciente()
	{
		this(null,null,0);
	}
 
	public Paciente(String name,String password,int id_supervisor)
	{
	  super(name,password,id_supervisor);
	}

	 
}
