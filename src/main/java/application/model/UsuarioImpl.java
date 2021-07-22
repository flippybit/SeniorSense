package application.model;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import application.container.UserMemoryContainer;
import application.exception.UserException;
import application.implemet.MyDao;
import application.interfaces.Autenticar;
import application.interfaces.NatourDao;
import application.interfaces.NatourDao.Class;
import application.interfaces.Usuario;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import application.dao.*;

@NatourDao.Table(value = "user")
public class UsuarioImpl extends MyDao implements Usuario {

	private String password;

	@NatourDao.Field(type = "String", value = "password")
	private String hashedpassword;

	@NatourDao.Field(value = "id_rol")
	private int type;

	@NatourDao.Field(value = "id_user", index = "PRIMARY", options = { NatourDao.AUTO_INCREMENT })
	private int id;

	@NatourDao.Field(type = "String", value = "name", options = { NatourDao.NO_DUPLICATES })
	public String nombre;

	@NatourDao.Field(value = "lastname", required = true)
	private String apellidos;
	private String calle;
	private int codigoPostal;
	private String ciudad;

	@NatourDao.Field(value = "id_parent")
	private int idParent;

	@NatourDao.Field(type = "String", value = "username", options = { NatourDao.NO_DUPLICATES })
	private String username;
	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}

	private File dataFile;

	private List<UsuarioImpl> nestedList;

	public UsuarioImpl() {
		this(null, null);

	}

	
	/**
	 * 
	 * @param password
	 * @param hashedpassword
	 * @param type
	 * @param id
	 * @param nombre
	 * @param apellidos
	 * @param calle
	 * @param codigoPostal
	 * @param ciudad
	 * @param idParent
	 * @param username
	 * @param dataFile
	 * @param nestedList
	 */
	public UsuarioImpl(String password, String hashedpassword, int type, int id, String nombre, String apellidos,
			String calle, int codigoPostal, String ciudad, int idParent, String username, File dataFile,
			List<UsuarioImpl> nestedList) {
		super();
		this.password = password;
		this.hashedpassword = hashedpassword;
		this.type = type;
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.calle = calle;
		this.codigoPostal = codigoPostal;
		this.ciudad = ciudad;
		this.idParent = idParent;
		this.username = username;
		this.dataFile = dataFile;
		this.nestedList = nestedList;
	}



	public UsuarioImpl(String name, String password) {

		this.nombre = name;
		// this.password = new SimpleStringProperty(password);

		this.apellidos = "";
		// this.nombre = name;
		this.password = password;
		this.calle = "some street";
		this.codigoPostal = 1234;
		this.ciudad = "some city";

		this.id = 0;
		this.username = name;

		this.dataFile = null;
		setUserType();
		nestedList = new ArrayList<UsuarioImpl>();
	}

	public UsuarioImpl(String name, String password, int parent) {
		this(name, password);
		this.idParent = parent;
	}

	private void setUserType() {
		if (this instanceof Administrador) {
			this.type = Usuario.USUARIO_ADMIN;
		} else if (this instanceof Supervisor) {
			this.type = Usuario.USUARIO_SUPERVISOR;
			;

		} else {

			this.type = Usuario.USUARIO_PACIENTE;
			;
		}
	}

	public String getUserTypeName() {
		switch (this.type) {
		case Usuario.USUARIO_ADMIN:
			return "ADMIN";
		case Usuario.USUARIO_SUPERVISOR:
			return "SUPERVISOR";
		case Usuario.USUARIO_PACIENTE:
			return "PACIENTE";
		}
		return null;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHashedpassword() {
		return hashedpassword;
	}

	public void setHashedpassword(String hashedpassword) {
		this.hashedpassword = hashedpassword;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return username;
	}

	public void setEmail(String email) {
		this.username = email;
	}

	public File getDataFile() {
		return dataFile;
	}

	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}

	@Override
	public String getUserAccount() {
		// TODO Auto-generated method stub
		return nombre;
	}

	@Override
	public int getIdParent() {
		// TODO Auto-generated method stub
		return idParent;
	}

	public void setIdParent(int idParent) {
		this.idParent = idParent;
	}

	@Override
	public void setUserAccount(String ac) {
		this.username = ac;

	}

	public List<UsuarioImpl> getNestedList() {
		return nestedList;
	}

	public void setNestedList(List<UsuarioImpl> nestedList) {
		this.nestedList = nestedList;
	}
}
