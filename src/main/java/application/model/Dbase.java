package application.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import application.Main;
import application.container.UserMemoryContainer;
import application.exception.UserException;
import application.interfaces.Usuario;
import application.wrapper.PacketListWrapper;
import application.wrapper.PatientListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Dbase {

	// los datos como una Obsevable list de Usuarios .
	private ObservableList<UsuarioImpl> userData = FXCollections.observableArrayList();
	private ObservableList<UserMemoryContainer> userDatacontainer = FXCollections.observableArrayList();
 	private ObservableList<ArduPacket> arduPackets = FXCollections.observableArrayList();
	// Variable donde se guardan las cuentas
	File fileAccounts;
	private UsuarioImpl loggedUser;

	private String userFilePath ="";

	public Dbase() {
		loggedUser = null;
		ClassLoader classLoader = Main.class.getClassLoader();
		File facc= new File("accounts.xml");
		if(!facc.exists())
		{
			try {
				facc.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		  facc= new File("testo.xml");
		if(!facc.exists())
		{
			try {
				facc.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			userFilePath = "accounts.xml";

		File file = new File(userFilePath);
		
	 	loadUserDataFromFile(file);
		this.fileAccounts = file;
	}

	/**
	 * Validar cuenta del usuario
	 * devuelva un numero entero desde -1 hasta 3
	 *
	 **/
	public int verifyUser(String userName, String password) {
		String nombre, contra, tipo;

		// level 0 si no encuntra a ningun usuario o mala contrase�a o no tiene tipo
		// level 1 admin
		// level 2 sanitario
		// level 3 paciente / usuario
		int userLevel = 0;
//		for (Usuario user : getuserData()) {
//			nombre = user.getNombre();
//			contra = user.getPassword();
//			tipo = user.getUserType();
//
//			if (nombre.equals(userName) && contra.equals(password)) {
//				// Guardamos el usuario en una variable de clase
//				this.setLoggedUser(user);
//				switch (tipo) {
//
//				case "admin":
//					userLevel = 1;
//					break;
//				case "sanitario":
//					userLevel = 2;
//					break;
//				case "paciente":
//					userLevel = 3;
//					break;
//				default:
//					userLevel = -1;
//
//				}
//				break;
//			}
//		}
		return userLevel;

	}

	/**
	 * Returns the data as an observable list of Persons.
	 *
	 * @return
	 */
	public ObservableList<UsuarioImpl> getuserData() {
		return this.userData;
	}
	/**
	 * Returns the data as an observable list of Data packet.
	 *
	 * @return
	 */
	public ObservableList<ArduPacket> getArduPacketData() {
		return this.arduPackets;
	}

	/**
	 * Saves the current person data to the specified file.
	 *
	 * @param file
	 */
	public void saveUserDataToFile(File file) {
		try {


			file = new File("accounts.xml");
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			HashMap<String,Usuario> usersContainer = UserMemoryContainer.userContainer;

			// Wrapping our person data.
			PatientListWrapper wrapper = new PatientListWrapper();

			userData= FXCollections.observableArrayList();
			 Iterator it = usersContainer.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        UsuarioImpl impl = (UsuarioImpl)pair.getValue();

			        userData.add((UsuarioImpl) pair.getValue());

			    }
			wrapper.setUsers(userData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

			// Save the file path to the registry.
			// setUserFilePath(file);
		} catch (Exception e) { // catches ANY exception

			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());
			alert.showAndWait();
		}
	}

	/**
	 * Guarda los datos de la lista de usuarios en el fichero accounts.xml.
	 *
	 * @param file
	 */
	public void saveUserToAccountFile() {
		try {
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			PatientListWrapper wrapper = new PatientListWrapper();
			wrapper.setUsers(userData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, fileAccounts);



			// Save the file path to the registry.
			// setUserFilePath(file);
		} catch (Exception e) { // Captura cualquier exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + fileAccounts.getPath());
			alert.showAndWait();
		}
	}

	/**
	 * Guarda un usuario nuevo en en el fichero accounts.xml.
	 *
	 * @param file
	 */
	public void saveUserToAccountFile(UsuarioImpl _user) {
		try {
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			PatientListWrapper wrapper = new PatientListWrapper();
			userData.add(_user);
			wrapper.setUsers(userData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, fileAccounts);

			// Save the file path to the registry.
			// setUserFilePath(file);
		} catch (Exception e) { // Captura cualquier exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + fileAccounts.getPath());
			alert.showAndWait();
		}
	}

	/**
	 * Guarda datos de muestra en un fichero xml con datos del arduino.
	 *
	 * @param file
	 */
	public void saveToNewDataFile(File userDataFile) {
		try {
			JAXBContext context = JAXBContext.newInstance(PacketListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			PacketListWrapper wrapper = new PacketListWrapper();
			arduPackets.add(new ArduPacket("AR01", "01/01/01", "SEQ000"));
			wrapper.setArduPackets(arduPackets);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, userDataFile);

		} catch (Exception e) { // Captura cualquier exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + userDataFile.getPath());
			alert.showAndWait();
		}
	}


	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 *
	 * @param file
	 */
	public void loadUserDataFromFile(File file) {
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("accounts.xml"));     
			if (br.readLine() == null) {
			   return;
			}
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			PatientListWrapper wrapper = (PatientListWrapper) um.unmarshal(file);

			// userData.clear();
//			for(Usuario u : wrapper.getUsers())
//			{
//				UserMemoryContainer.addUser(u);
//			}

			UserMemoryContainer ucontainer = new UserMemoryContainer();
			//	ucontainer.
				wrapper.getUsers().forEach(user->{
					try {
						ucontainer.register(user);
						userDatacontainer.add(ucontainer);
					} catch (UserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			// Save the file path to the registry.
			// setUserFilePath(file);

		} catch (Exception e) { // catches ANY exception

			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());
			alert.showAndWait();
		}
	}
	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 *
	 * @param file
	 */
	public void loadArduDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			PacketListWrapper wrapper = (PacketListWrapper) um.unmarshal(file);

			// userData.clear();
			arduPackets.addAll(wrapper.getArduPackets());

			// Save the file path to the registry.
			// setUserFilePath(file);

		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());
			alert.showAndWait();
		}
	}

	public Usuario getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(UsuarioImpl _loggedUser) {
		this.loggedUser = _loggedUser;
	}

}
