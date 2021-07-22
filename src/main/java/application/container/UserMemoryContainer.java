package application.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import org.mindrot.jbcrypt.BCrypt;

import application.Main;
import application.exception.UserException;
import application.exception.UserException;
import application.interfaces.Autenticar;
import application.interfaces.Usuario;
import application.model.UsuarioImpl;
import javafx.collections.ObservableList;

//Contenedor para cada usuario
public class UserMemoryContainer implements Autenticar{

	public static HashMap<String,Usuario> userContainer;

	private static UserMemoryContainer self;

	public UserMemoryContainer()
	{
		self=this;
		this.userContainer = new HashMap<String,Usuario>();
		
	}

	public HashMap<String,Usuario> getPatientList()
	{



		return null;

	}

	public void persistance()
	{
		Main.getDB().saveUserDataToFile(null);
	}

	public static void addUser(Usuario u)
	{

		userContainer.put(u.getUserAccount(), u);
	}
public ObservableList<UsuarioImpl> getUserObservable() {
	ObservableList<UsuarioImpl> UserFullList;
	//return   Observable.from(userContainer.values());
	return   null;
}


	public static Usuario getUser(String name) throws UserException
	{
		System.out.println(name+" name");
		System.out.println(" MIAUU");
		userContainer.get(name);
		   Iterator it = userContainer.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        System.out.println(pair.getKey() + " = " + pair.getValue());

		    }


		if(!userContainer.containsKey(name))
			throw new UserException("User not found");
		return userContainer.get(name);
	}

	public Usuario login(String username, String password) throws UserException {

		Usuario u = getUser(username);

		System.out.println("Nombre cuenta: " +u.getUserAccount());
		if(u!=null)
		{
			System.out.println("PASSWORD : "+ password);
			System.out.println("PASSWORD : "+ u.getHashedpassword());

			if(PasswordMatch(password,u.getHashedpassword()))
			{

				return u;

			}
			else
			{

				throw new UserException("Password missmatch");
			}
		}

		//String hashed =
		return null;
	}

	public static UserMemoryContainer getInstance()
	{
		if(self==null)
		{
			self = new UserMemoryContainer();
		}
		return self;
	}

	public boolean register(Usuario u) throws UserException {


		if(u.getUserAccount().isEmpty())
			throw new UserException("Empty name");

		if(u.getPassword().isEmpty())
			throw new UserException("Empty password");

		 u.setPassword(u.getPassword());
		 u.setHashedpassword(hashing(u.getPassword()));

		 if(!userContainer.containsKey(u.getUserAccount()))
		  userContainer.put(u.getUserAccount(), u);

		return true;
	}

	private boolean PasswordMatch(String pass,String hashed)
	{
		return BCrypt.checkpw(pass, hashed);
	}

	private String hashing(String pass)
	{
		return  BCrypt.hashpw(pass, BCrypt.gensalt());
	}

}
