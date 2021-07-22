package application;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.mindrot.jbcrypt.BCrypt;

import application.container.MessageContainer;
import application.container.SensorContainer;
import application.container.UserDBContainer;
import application.container.UserMemoryContainer;
import application.controller.ArduController;
import application.controller.LoginController;
import application.controller.LoginViewController;
import application.controller.PatientEditDialogController;
import application.controller.PatientOverviewController;
import application.controller.RootLayoutController;
import application.controller.ServerController;
import application.controller.SignUpViewController;
import application.controller.SinglePatientViewController;
import application.controller.StatsController;
import application.controller.UserController;
import application.dao.SensorDao;
import application.dao.UserDao;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MessageImpl;
import application.implemet.MessengerFileSystem;
import application.implemet.MyCommunicationMessage;
import application.interfaces.ArduMessage;
import application.interfaces.Message;
import application.interfaces.MessageSystem;
import application.interfaces.Usuario;
import application.model.Administrador;
import application.model.DbConnection;
import application.model.Dbase;
import application.model.Paciente;
import application.model.Sensor;
import application.model.SensorData;
import application.model.SensorNotification;
import application.model.Supervisor;
import application.model.TCPServer;
import application.model.UsuarioImpl;
import application.model.context.Context;
import application.model.context.HelperForm;
import application.wrapper.PatientListWrapper; 
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class Main extends Application {


	private Stage primaryStage;
	private BorderPane rootLayout;

	private static Main self=null;
	RootLayoutController Rootcontroller  ;

	ClassLoader classLoader = null;
	private static final String ROOT = "/RootLayout.fxml";
	private static final String REGISTER = "/SignUpView.fxml";
	private static final String LOGIN = "/Login.fxml";
	private static final String PATIENT = "/patientOverview.fxml";
	private static final String PATIENT_SINGLE = "/SinglePatientView2.fxml";
	private static final String PATIENT_DIALOG = "/PatientEditDialog.fxml";
	public static final String CHAT_DIALOG = "/ChatDialogView.fxml";
	
	private static final String ADMIN_VIEW = "/AdminView.fxml";
	

	private static final String VIEW_PATH = "views";


	/**
	 * Los Datos como una lista tipo Observable
	 */
	private ObservableList<UsuarioImpl> userData = FXCollections.observableArrayList();
	private ObservableList<UserDBContainer> userDatacontainer = FXCollections.observableArrayList();

	private Dbase MainDbaseCtrl;

	public static Main getInstance()
	{

		return self;

	}

	public static Dbase getDB()
	{
		return self.MainDbaseCtrl;
	}

	public static String getViewPath()
	{
		return VIEW_PATH;
	}

	@Override
	public void start(Stage primaryStage) {
		try {

			self=this;
			classLoader = getClass().getClassLoader();
			
			System.out.println("-- Cargando configuracion de Configuration.json (o defaultConfig.json) --");
			Configuration c = new Configuration();
			c.loadConfigFile();
		 
			UserDao uo = new UserDao();
		  
		 //	DbConnection db = DbConnection.getInstance();
		//  	db.getConnection(); 
		 
			
			// Read file fxml and draw interface.
			UserDBContainer ucontainer = new UserDBContainer();
			 
		  
			/** debug en modo print = 2 **/
			Context.getInstance().DEBUG_MODE= (int) Configuration.getValue("application.debug",Integer.class);
			
			/** El servidor se inicia 1 vez, si ya existe no se inicia" **/
			ServerController serverC = new ServerController();
			
			/** Conectamos el cliente actual al servidor (puede haber varios clientes -usuario- o -arduino- en una maquina o  varias en una la red) **/
			UserController uc = new UserController();
		
		
			CommunicationCenter ccm =new CommunicationCenter();
			ArduController ardc = new ArduController();
		  
		  
		  	 
		  	/**
		  	 *    ---Eventos-- interfaz: CustomActionListener => EventoObject:  CustomEvent
		  	 *    -Servidor-
		  	 *  onServerReceiveMessage => (el servidor recibe un mensaje) (cliente => servidor)
		  	 * 	onServerSendMessage => (el servidor envia un mensaje) (servidor => cliente)
		  	 *  onUserReceiveMessage (el usuario(cliente) recibe un mensaje del servidor) (servidor => cliente)
		  	 *  onUserSendMessage (el usuario(cliente) envia un mensaje al servidor) (cliente => servidor)
		  	 *  onSocketConnected (un cliente se conecto al servidor) (servidor)
		  	 *  onActionLogin
		  	 *  -Cliente-
		  	 *  onSocketUserConnected (el servidor envia mensaje con datos de la conexion) (servidor => cliente)
		  	 *  onSocketUserDisconnected (cliente)
		  	 *  doneSocketUserConnected
		  	 *  doneSocketUserDisconnected
		  	 *  onUserActionLogin => (usuario introduce datos de login) (cliente => Servidor)
		  	 *  onSucessfullLogin => (login con exito en la applicacion, modo servidor) (servidor => cliente)
		  	 *  onFailedLogin => (login con exito en la applicacion, modo servidor) (servidor => cliente)
		  	 *  
		  	 *  
		  	 *  
		  	 */
		  	
		  	/** Registro de eventos (algunos) **/
		  	Context.getInstance().registerListener("onSocketConnected", serverC);
		  	Context.getInstance().registerListener("onServerReceiveMessage", serverC); 
		  	Context.getInstance().registerListener("onServerSendMessage", serverC);
		  	Context.getInstance().registerListener("onServerSucessfullLogin", serverC);
		  	Context.getInstance().registerListener("onActionLogin", serverC);
		  	
		  	Context.getInstance().registerListener("onSocketUserConnected", uc);
		  	Context.getInstance().registerListener("onUserSendMessage", uc);
		  	Context.getInstance().registerListener("onUserReceiveMessage", uc);
		  	Context.getInstance().registerListener("onSucessfullLogin", uc);
		  	Context.getInstance().registerListener("onFailedLogin", uc);
		  	Context.getInstance().registerListener("doUpdateValues", uc);
		  	Context.getInstance().registerListener("onReceiveAlert", uc);
		  	
		  	
		  	serverC.initServer(Configuration.getValue("server.bind"), (int)Configuration.getValue("server.port",Integer.class)); 
		   
/*************************************************************************
* Datos de prueba
************************************************************************
*/
		  	//Server server = new Server();
		  	//server.startServer();
			 // Registro Administrador

             Administrador admin1 = new Administrador("admin","mypass2");
			 ucontainer.register(admin1);
			 
			 admin1.setPassword("abc");
		 
			 userDatacontainer.add(ucontainer);
			 System.out.println(" TIPO "+admin1.getType());

			 // Registro Supervisor
			 Usuario super1 = new Supervisor ("supervisor", "mypass2");
			 ucontainer.register(super1);
			 super1.setPassword("abc");
			 
			 System.out.println(" TIPO "+super1.getType());
			 
			 super1 =  UserDBContainer.getUser("supervisor");
			 System.out.println(super1.getId());
			 UsuarioImpl paciente1 = new Paciente("paciente","mypass2",super1.getId());
			 UsuarioImpl paciente2 = new Paciente("paciente2","mypass2",super1.getId());
				// Registro paciente
			 ucontainer.register(paciente1);
			 ucontainer.register(paciente2);
			 
			 paciente1 = (UsuarioImpl) ucontainer.getUser("paciente");
			 paciente2 = (UsuarioImpl) ucontainer.getUser("paciente2");
			// paciente1.setCalle("xxxdd");
			 ucontainer.update(paciente1);
			 ucontainer.update(paciente2);
			 
			 
			 
			 Sensor sensor1 = new Sensor();
			 sensor1.setType(1); 
			 Sensor sensor2 = new Sensor();
			 sensor2.setType(2);
			 Sensor sensor3 = new Sensor();
			 sensor3.setType(3); 
			 Sensor sensor4 = new Sensor();
			 sensor4.setType(4);
			 Sensor sensor5 = new Sensor(); 
			 sensor5.setType(5);
			 
			 
			 UsuarioImpl px = (UsuarioImpl) ucontainer.getUser("paciente", true);
			 UsuarioImpl px2 = (UsuarioImpl) ucontainer.getUser("paciente2", true);
			/*
		
			 */
			 SensorDao sd = new SensorDao();
			 sensor1.setId_user(px.getId());
			 
			 sensor2.setId_user(px.getId());
			 sensor3.setId_user(px.getId());
			 sensor4.setId_user(px.getId());
			 sensor5.setId_user(px.getId());
			 
			 sensor1.setId_user(px2.getId());
			 sensor2.setId_user(px2.getId());
			 sensor3.setId_user(px2.getId());
			 sensor4.setId_user(px2.getId());
			 sensor5.setId_user(px2.getId());
			 
			SensorContainer sC = new SensorContainer();
		/*	sC.addSensor(sensor1); 
			sC.addSensor(sensor2); 
			sC.addSensor(sensor3); 
			sC.addSensor(sensor4); 
			sC.addSensor(sensor5); */
		 	
		  
			 List<Sensor> allSensors= sd.getAllSensorBy(0, 0);
			 SensorContainer.getInstance().sensores=allSensors;
			 
				List<SensorData> sensoresData = new ArrayList<SensorData>();
				List<SensorNotification> sensoresNot = new ArrayList<SensorNotification>();
				 for(Sensor s : allSensors)
				 {
					 List<SensorData> thisSdatas = sd.getSensorDatas(s.getId_sensor(),0);
					 List<SensorNotification> thisSNots = sd.getSensorNotifications(s.getId_sensor(),0);
					 
					 for(SensorData sddx : thisSdatas)
					 {
						 sensoresData.add(sddx);
					 }
					 for(SensorNotification sddxf : thisSNots)
					 {
						 sensoresNot.add(sddxf);
					 }
				 }
				 SensorContainer.getInstance().sensoresData = sensoresData;
				 SensorContainer.getInstance().sensoresNot = sensoresNot;
				List<Sensor> slist= sd.getAllSensorBy(0,0);
				 for(Sensor s : slist)
				 {
					 s.doEmulation();
				 }
			
			//paciente1.setPassword("mypass2");
				//ucontainer.update(paciente1);

 
//			 if(ucontainer.login("paciente", "mycontrasenaaaaaaa1") instanceof Usuario)
//			 {
//				 System.out.println("login ok");
//			 }
//			 if(ucontainer.login("admin", "mypass2") instanceof Usuario)
//			 {
//				 System.out.println("login ok");
//			 }

//			ucontainer.persistance();
//			MessageContainer mc = new MessageContainer();
//			MessageSystem mcf =   mc.getMessageSystem();
//
//
//			// FROM, TO , CONTENIDO, CABEZERA
//				 Message m = new MessageImpl("paciente","admin","tengo un problexa","hola me pasa esto");
//				 Message m2 = new MessageImpl("admin","paciente","ahora no puedo","solo soy un admin");
//			//enviar desde..
//				// mcf.send(m);
//				//mcf.send(m2);
//			//MessageContainer.getMessageSystem().send(m);
//			//MessageContainer.getMessageSystem().send(m);
//
//			//Todos los mensajes
//			List <Message> allMessages = mcf.getAll();
//			for(Message mm : allMessages)
//			{
//				// System.out.println(mm.toString());
//			}
//			System.out.println(" Mensajes enviados a paciente ----");
//			// Coger los mensajes enviados al usuario "paciente" | limite 100
//			List <Message> allMessagesTo = mcf.getAllTo("paciente", 100);
//			for(Message mm : allMessagesTo)
//			{
//				 System.out.println(mm.toString());
//			}
//			System.out.println(" Mensajes enviados desde paciente ----");
//			// Coger los mensajes enviados DESDE usuario "paciente" | limite 100
//			List <Message> allMessagesfrom = mcf.getAllFrom("paciente", 100);
//			for(Message mm : allMessagesfrom)
//				{
//				System.out.println(mm.toString());
//			}
//
//			// Get by id
//			System.out.println(" Mensaje 5 ---- getById");
//			Message mid =mcf.getById(5);
//			System.out.println(mid.toString());
//
//			//FXMLLoader loader = new FXMLLoader();
//
//           // loader.setLocation(getClass().getResource("/Login.fxml"));
//            //Parent content = loader.load();


			primaryStage.setTitle(Configuration.getValue("application.title"));
			this.primaryStage = primaryStage;

			FXMLLoader loader = new FXMLLoader();

			loader.setLocation(getClass().getResource(getViewPath() + ROOT));
			rootLayout = (BorderPane) loader.load();


			
			Rootcontroller= loader.getController();
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.setOnCloseRequest(eventox -> {
			 System.exit(1);
			});
			primaryStage.show();

			showLoginView();
			
			 
			
		
			  
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

 

	private void showView(String view)
	{
		try {

			// loadUserDataFromFile(file);

			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + view));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showChatDialog()
	{
	
	 
		
		 Parent root;
	        try {
	          
	            // Hide this current window (if this is what you want)
	            FXMLLoader fxmlLoader = new FXMLLoader();
	            fxmlLoader.setLocation(getClass().getResource(getViewPath()+CHAT_DIALOG));
	            /* 
	             * if "fx:controller" is not set in fxml
	             * fxmlLoader.setController(NewWindowController);
	             */
	            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
	            Stage stage = new Stage();
	            stage.setTitle(" Chat - mensajes : "+this.getInstance().getDB().getLoggedUser().getNombre());
	            stage.setScene(scene);
	            stage.show();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	}

	/**
	 * Muestra la vista Login dentro del Layout raiz.
	 */
	public void showLoginView() {
		try {
			
			
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + LOGIN));
			BorderPane loginView = (BorderPane) loader.load();

			// Set person overview into the center of root layout.
			Rootcontroller.setInPane(loginView);

			// Give the controller access to the main app.
			LoginController controller = loader.getController();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Muestra la vista Login dentro del Layout raiz.
	 */
	public void showSignUpView() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + REGISTER));
			AnchorPane SignUpView = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			Rootcontroller.setInPane(SignUpView);

			// Give the controller access to the main app.
			SignUpViewController controller = loader.getController();
			// Dbase DbaseCtrl = new Dbase();
			controller.setMainApp(this);
			controller.setDataSource(this.MainDbaseCtrl);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Muestra la vista general de los pacientes dentro del Layout raiz.
	 *
	 */
	public void showPatientOverview() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + PATIENT));
			GridPane patientOverview = (GridPane) loader.load();

			// Set person overview into the center of root layout.
			Rootcontroller.setInPane(patientOverview);

			// Give the controller access to the main app.
			PatientOverviewController controller = loader.getController();
			controller.setMainApp(this);
			//Dbase DbaseCtrl = new Dbase();

			controller.setDataSource(MainDbaseCtrl);
			// controller.loadData();

			// controller.setDataSource(DbaseCtrl);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Muestra la vista general de los pacientes dentro del Layout raiz.
	 *
	 */
	public void showSinglePatientView() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + PATIENT_SINGLE));
			GridPane SinglePatientView = (GridPane) loader.load();

 			// Set person overview into the center of root layout.
			Rootcontroller.setInPane(SinglePatientView); 
			
		 
			// Give the controller access to the main app.
			SinglePatientViewController controller = loader.getController();
	
			controller.setMainApp(this);

			System.out.println(" PATIENT VIEW ");
			// controller.loadData();

			// controller.setDataSource(DbaseCtrl);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the person file preference, i.e. the file that was last opened. The
	 * preference is read from the OS specific registry. If no such preference can
	 * be found, null is returned.
	 *
	 * @return
	 */
	public File getUserFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Sets the file path of the currently loaded file. The path is persisted in the
	 * OS specific registry.
	 *
	 * @param file the file or null to remove the path
	 */
	public void setUserFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		if (file != null) {
			prefs.put("filePath", file.getPath());

			// Update the stage title.
			primaryStage.setTitle("Arduapp - " + file.getName());
		} else {
			prefs.remove("filePath");

			// Update the stage title.
			primaryStage.setTitle("Arduapp");
		}
	}

	/**
	 * Opens a dialog to edit details for the specified person. If the user clicks
	 * OK, the changes are saved into the provided person object and true is
	 * returned.
	 *
	 * @param person the person object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPatientEditDialog(Usuario person) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + PATIENT_DIALOG));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Editar Paciente");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the person into the controller.
			PatientEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPatient((Paciente)person);
		//	Dbase DbaseCtrl = new Dbase();
			controller.setDataSource(this.MainDbaseCtrl);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Loads person data from the specified file. The current person data will be
	 * replaced.
	 *
	 * @param file
	 */
	public void loadUserDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			PatientListWrapper wrapper = (PatientListWrapper) um.unmarshal(file);

			UserMemoryContainer ucontainer = new UserMemoryContainer();
		//	ucontainer.
			wrapper.getUsers().forEach(user->{
				try {
					ucontainer.register(user);
					 
				} catch (UserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			// userData.clear();
			userData.addAll(wrapper.getUsers());

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

	/**
	 * Saves the current person data to the specified file.
	 *
	 * @param file
	 */
	public void saveUserDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PatientListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			PatientListWrapper wrapper = new PatientListWrapper();
			wrapper.setUsers(userData);

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, file);

			// Save the file path to the registry.
			// setUserFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	/**
	 * Devuelve los datos como una lista Observable de Pacientes.
	 *
	 * @return
	 */
	public ObservableList<UsuarioImpl> getuserData() {
		return userData;
	}

	/**
	 * Returns the main stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	// Se utiliza para iniciar la aplicacion enviando argunmentos en el String Array
	public static void main(String[] args) {
		launch(args);
	}

	public void showAdminView() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(getViewPath() + ADMIN_VIEW));
			GridPane SinglePatientView = (GridPane) loader.load();

			// Set person overview into the center of r
			Rootcontroller.setInPane(SinglePatientView); 

			// controller.loadData();

			// controller.setDataSource(DbaseCtrl);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
