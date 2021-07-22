package application.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import application.dao.UserDao;
import application.exception.UserException;
import application.interfaces.Autenticar;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.UsuarioImpl;
import application.model.context.Context;

// Contenedor para base de datos

public class UserDBContainer implements Autenticar{

	public static HashMap<String,Usuario> userContainer;

	private static UserDBContainer self;
	
	private UserDao userDao;
	
	private static Map<Integer,Usuario> staticUserMap ;

	public UserDBContainer()
	{
		self=this;
		this.userContainer = new HashMap<String,Usuario>();
		userDao=new UserDao();
		staticUserMap = new HashMap<Integer,Usuario>();
		
	}
	
	public static void addUser(Usuario u)
	{

		userContainer.put(u.getUserAccount(), u);
	}
	
	public static Usuario getUser(String name) throws UserException
	{
		 
  
		return  UserDBContainer.getInstance().userDao.getClientByName(name); 
	}
	public static Usuario getUser(int id) throws UserException
	{
		 
		  return getUser(id,false);
	}
	public static Usuario getUser(int id,boolean full) throws UserException
	{
		return getUser(id,full,false);
	}
	public static Usuario getUser(String name,boolean full) throws UserException
	{
	
  
		if(!full)
			return getUser(name);
		
		Usuario u=  UserDBContainer.getInstance().userDao.getClientByName(name);
		
		List<UsuarioImpl> listusers =  UserDBContainer.getInstance().userDao.getNestedUsersByUserId(u.getId());
		u.setNestedList(listusers);
		return u; 
	}
	public static Usuario getUser(int id,boolean full,boolean update) throws UserException
	{
		 if(!update && staticUserMap.containsKey(id) && !full)
		 {
			 return staticUserMap.get(id);
		 }
		  
	
		if(!full)
		{
			staticUserMap.put(id, UserDBContainer.getInstance().userDao.getClientByID(id)) ; 
			return staticUserMap.get(id);
		}
			
		
		Usuario u=  UserDBContainer.getInstance().userDao.getClientByID(id);
		
		List<UsuarioImpl> listusers =  UserDBContainer.getInstance().userDao.getNestedUsersByUserId(u.getId());
		u.setNestedList(listusers);
		staticUserMap.put(id, u) ; 
		return staticUserMap.get(id);
	}
	
	
	public boolean update(Usuario u) throws UserException
	{
		
		if(u.getUserAccount().isEmpty())
			throw new UserException("Empty name");
		
		 
		
		if(u.getPassword()!=null && u.getPassword()!="")
			u.setHashedpassword(hashing(u.getPassword()));
		
		boolean thereturn =userDao.updateUser(u)==0 ? false : true;
		
		staticUserMap.put(u.getId(), getUser(u.getId(),false,true));
	  
	  return thereturn;
	}
	
	public Usuario login(String username, String password) throws UserException {

		Usuario u =  userDao.getClientByName(username); 

 
		if(u!=null)
		{ 
	 
			if(PasswordMatch(password,u.getHashedpassword()) )
			{ 
				Context.getInstance().doAction(new CustomEvent("onLogin",u));
				return u;

			}
			else
			{

				throw new UserException("Password missmatch");
			}
		}else {
			return null;
		}
 
	}

	public static UserDBContainer getInstance()
	{
		if(self==null)
		{
			self = new UserDBContainer();
		}
		return self;
	}
	
	public List<UsuarioImpl> getAll()
	{
		return userDao.geAll();
	}

	public boolean register(Usuario u) throws UserException {


		if(u.getUserAccount().isEmpty())
			throw new UserException("Empty name");

		if(u.getPassword().isEmpty())
			throw new UserException("Empty password");

		 u.setPassword(u.getPassword());
		 u.setHashedpassword(hashing(u.getPassword()));

		 boolean registered = userDao.registerClient(u);
			 if(registered)
			 {
				 
				 
				 Usuario newUser = getUser(u.getNombre());
				 staticUserMap.put(newUser.getId(),newUser);
			 }
			 return registered;
	}

	private boolean PasswordMatch(String pass,String hashed)
	{
		return BCrypt.checkpw(pass, hashed);
	}

	public String hashing(String pass)
	{
		return  BCrypt.hashpw(pass, BCrypt.gensalt());
	}

 
}
