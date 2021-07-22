package application.interfaces;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import application.model.UsuarioImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public interface Usuario {

	public static final int USUARIO_ADMIN = 0;
	public static final int USUARIO_SUPERVISOR = 1;
	public static final int USUARIO_PACIENTE = 2;

	public String getNombre();

	public String getApellidos();

	public String getCalle();

	public int getCodigoPostal();

	public String getCiudad();

	public int getType();

	public String getHashedpassword();

	public String getPassword();

	public void setPassword(String password);

	public void setHashedpassword(String hashing);

	public String getUserAccount();

	public File getDataFile();
 

	public int getId();

	public int getIdParent();

	public void setIdParent(int idParent);

	public void setUserAccount(String ac);

	public void setNombre(String nm);

	public void setType(int type);

	public void setId(int id);

	public void setApellidos(String apellidos);

	public List<UsuarioImpl> getNestedList();

	public void setNestedList(List<UsuarioImpl> nestedList);
}
