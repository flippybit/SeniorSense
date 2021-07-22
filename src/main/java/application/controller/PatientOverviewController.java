package application.controller;
 
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import application.Configuration;
import application.Main;
import application.container.MessageContainer;
import application.container.SensorContainer;
import application.container.UserDBContainer;
import application.controller.classes.AutoUpdate;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MessageImpl;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Message;
import application.interfaces.Usuario;
import application.model.Dbase;
import application.model.Paciente;
import application.model.Sensor;
import application.model.SensorNotification;
import application.model.UsuarioImpl;
import application.model.context.Context;
import application.model.context.HelperForm;
import application.model.context.Tools;
import eu.hansolo.enzo.notification.Notification.Notifier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PatientOverviewController extends AutoUpdate {
	@FXML
	private TableView<UsuarioImpl> patientTable;
	@FXML
	private TableColumn<Usuario, String> firstNameColumn;
	@FXML
	private TableColumn<Usuario, String> lastNameColumn;

	@FXML
	private Label nombrelb;
	@FXML
	private Label apellidoslb;
	@FXML
	private Label callelb;
	@FXML
	private Label codigoPostallb;
	@FXML
	private Label ciudadlb;
	@FXML
	private Label cumplelb;
	@FXML
	TextArea notificationArea;

	private ObservableList<Sensor> sensorData = FXCollections.observableArrayList();
	
	@FXML
	private TableView<UsuarioImpl> tablePaciente;
	@FXML
	private TableColumn<UsuarioImpl,String> columnPaciente;
	
	@FXML
	private TextArea areaChat;
	@FXML
	private TextField fieldChat;
	@FXML
	private Button sendChatMessage;
	
	@FXML
	private Label labelChat;
	
	@FXML
	private TableView<Sensor> tableSensor ;
	
	private Map<Integer,StringBuilder> chatMessages;

	// Referencia al mainApp y base de datos.
	private Dbase dbase;
	private Main mainApp;

	private ObservableList<UsuarioImpl> clientList;

	
	private ObservableList<Sensor> sensorList;
	
	private int selectedChatIdUser = 0;
	
	/**
	 * The constructor. El constructor se llama antes que el metodo intialize().
	 */
	public PatientOverviewController() {

	}

	/**
	 * Inicializa la clase controlador. Este metodo es invocado automaticamente
	 * despues de cargar el fichero fxml.
	 */
	@FXML
	private void initialize() {

		try {

			Context.getInstance().registerListener("preSaveObject", this);
			Context.getInstance().registerListener("SaveObject", this);
			Context.getInstance().registerListener("doneReceiveAlert", this); 
			Context.getInstance().registerListener("onReceiveChatMessage", this);
			UsuarioImpl u = (UsuarioImpl) UserDBContainer.getUser(UserController.getInstance().user.getId(), true);

		 
			List<TableColumn<UsuarioImpl, String>> tableColumns = new ArrayList<TableColumn<UsuarioImpl, String>>();

			TableColumn<UsuarioImpl, String> t = new TableColumn();
			t.setText("id user");
			t.setCellValueFactory(new PropertyValueFactory<>("id"));
			tableColumns.add(t);

			TableColumn<UsuarioImpl, String> t1 = new TableColumn();
			t1.setText("nombre");
			t1.setCellValueFactory(new PropertyValueFactory<>("nombre"));
			tableColumns.add(t1);

			TableColumn<UsuarioImpl, String> t2 = new TableColumn();
			t2.setText("apellidos");
			t2.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
			tableColumns.add(t2);

			TableColumn<UsuarioImpl, String> t3 = new TableColumn();
			t3.setText("nombre");
			t3.setCellValueFactory(new PropertyValueFactory<>("nombre"));
			tableColumns.add(t3);
			TableColumn<UsuarioImpl, String> t4 = new TableColumn();
			t4.setText("ciudad");
			t4.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
			tableColumns.add(t4);

			TableColumn<UsuarioImpl, String> t5 = new TableColumn();
			t5.setText("calle");
			t5.setCellValueFactory(new PropertyValueFactory<>("calle"));
			tableColumns.add(t5);

			TableColumn<UsuarioImpl, String> t6 = new TableColumn();
			t6.setText("codigoPostal");
			t6.setCellValueFactory(new PropertyValueFactory<>("codigoPostal"));
			tableColumns.add(t6);

			TableColumn actionCol = new TableColumn("Action");
			actionCol.setText("action");
			actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

			Callback<TableColumn<UsuarioImpl, String>, TableCell<UsuarioImpl, String>> cellFactory = //
					new Callback<TableColumn<UsuarioImpl, String>, TableCell<UsuarioImpl, String>>() {
						@Override
						public TableCell call(final TableColumn<UsuarioImpl, String> param) {
							final TableCell<UsuarioImpl, String> cell = new TableCell<UsuarioImpl, String>() {

								final Button btn = new Button("Ver Usuario");

								@Override
								public void updateItem(String item, boolean empty) {
									super.updateItem(item, empty);
									if (empty) {
										setGraphic(null);
										setText(null);
									} else {
										btn.setOnAction(event -> {
											UsuarioImpl person = getTableView().getItems().get(getIndex());
											System.out.println(person.getNombre() + "   " + person.getApellidos());

											try {
												Usuario uact = UserController.getInstance().user;

												try {
													Configuration.setValue("impresionateUser",
															UserDBContainer.getUser(person.getId(), true));
												} catch (UserException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
												FXMLLoader fxmlLoader = new FXMLLoader();
												fxmlLoader.setLocation(
														Main.class.getResource("views/SinglePatientView2.fxml"));
												/*
												 * if "fx:controller" is not set in fxml
												 * fxmlLoader.setController(NewWindowController);
												 */
												Scene scene;

												GridPane gp = (GridPane) fxmlLoader.load();
												scene = new Scene(gp, 600, 400);

												Stage stage = new Stage();
												stage.setTitle("Viendo panel del usuario " + person.getId() + "-"
														+ person.getNombre() + "-" + person.getApellidos());
												stage.setOnCloseRequest(eventox -> {
													Configuration.setValue("impresionateUser", null);
													Context.getInstance()
															.removeListenersInCLass(SinglePatientViewController.class);

													Context.getInstance().removeListenersInCLass(StatsController.class);
												});

												stage.setOnShown(eventox -> {

													Context.getInstance().removeListenersInCLass(
															SinglePatientViewController.class, "doneReceiveAlert");

												});

												stage.setScene(scene);
												stage.show();
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}

										});
										setGraphic(btn);
										setText(null);

									}
								}
							};
							return cell;
						}
					};

			actionCol.setCellFactory(cellFactory);
			tableColumns.add(actionCol);
			patientTable.getColumns().clear();
			patientTable.getColumns().addAll(tableColumns);

			clientList = FXCollections.observableArrayList(u.getNestedList());

			for (UsuarioImpl uxd : clientList) {
				System.out.println(uxd.getNombre() + "  " + uxd.getApellidos() + " " + uxd.getId());
				patientTable.getItems().add(uxd);
			}

			patientTable.setItems(clientList);
			patientTable.refresh();
			
			
			
			
			List<TableColumn<Sensor, String>> tableSensorColumns = new ArrayList<TableColumn<Sensor, String>>();

			TableColumn<Sensor, String> t10 = new TableColumn();
			t10.setText("id_sensor");
			t10.setCellValueFactory(new PropertyValueFactory<>("id_sensor"));
			tableSensorColumns.add(t10);

			TableColumn<Sensor, String> t11 = new TableColumn();
			t11.setText("type");
			t11.setCellValueFactory(new PropertyValueFactory<>("type"));
			tableSensorColumns.add(t11);

			TableColumn<Sensor, String> t21 = new TableColumn();
			t21.setText("id_user");
			t21.setCellValueFactory(new PropertyValueFactory<>("id_user"));
			tableSensorColumns.add(t21);

			TableColumn<Sensor, String> t31 = new TableColumn();
			t31.setText("last_message");
			t31.setCellValueFactory(new PropertyValueFactory<>("last_message"));
			tableSensorColumns.add(t31);
			TableColumn<Sensor, String> t41 = new TableColumn();
			t41.setText("lastValue");
			t41.setCellValueFactory(new PropertyValueFactory<>("lastValue"));
			tableSensorColumns.add(t41);

		 
			
			List<Sensor> misSensores = SensorContainer.getInstance().getAllSensorByUserId(0,u.getId());
		 
			sensorData = FXCollections.observableArrayList(misSensores);

			// datos y notificaciones;

			for (Sensor s : misSensores) {

				List<SensorNotification> notifications = SensorContainer.getInstance()
						.getSensorNotifications(s.getId_sensor());

				for (SensorNotification sn : notifications) {
					notificationArea.appendText(formatNotification(sn));
				}
			}
			

			TableColumn actionColSensor = new TableColumn("Action");
			actionCol.setText("action");
			actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
			
			tableColumns.add(actionCol);
			
			
			this.tableSensor.getColumns().clear();
			tableSensor.getColumns().addAll(tableSensorColumns);

			
			sensorList = FXCollections.observableArrayList(SensorContainer.getInstance().getAllSensorByUserId(0, u.getId()));

		 

			tableSensor.setItems(sensorList);
			tableSensor.refresh();

			// limpiar los detalles del paciente.
			// showPatientDetails(null);

			// Escuchar cambios de seleccion, y muestra los detalles de la persona
			// cuando se cambia.

			Context.getInstance().registerListener("doneUpdateValues", this);

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		renderChat();
		// this.updateLabels.put(5, this.nombrelb);
	}
	
	private void renderChat()
	{
	/*  tablePaciente;
	 areaChat;
	  fieldChat;
	 sendChatMessage;*/
		
		try {
			Usuario supervisor = UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
			
			this.chatMessages = new HashMap<Integer, StringBuilder>();
			
			this.columnPaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
			
			List<UsuarioImpl> pacientes = supervisor.getNestedList();
			
			ObservableList<UsuarioImpl> pacientesCast  = FXCollections.observableArrayList();
			for(UsuarioImpl pacient : pacientes)
			{
				pacientesCast.add(pacient);
				
				List<Message> messageList = MessageContainer.getInstance().getUserMesages(pacient.getId(), 100);
				this.chatMessages.put(pacient.getId(),new StringBuilder());
				for (Message m : messageList) {
					System.out.println("añadir "+m.getContent());
					this.chatMessages.put(pacient.getId(), this.chatMessages.get(pacient.getId()).append(formatMessage(m, pacient, supervisor)));
				}
			}
			tablePaciente.setItems(pacientesCast);
			columnPaciente.setCellFactory(tc -> {
	            TableCell<UsuarioImpl, String> cell = new TableCell<UsuarioImpl, String>() {
	                @Override
	                protected void updateItem(String item, boolean empty) {
	                    super.updateItem(item, empty) ;
	                    setText(empty ? null : item);
	                }
	            };
	            cell.setOnMouseClicked(e -> {
	                if (! cell.isEmpty()) {
	                    String userName = cell.getItem();
	                    try {
							Usuario uclicked = UserDBContainer.getUser(userName);
							StringBuilder chatM = this.chatMessages.get(uclicked.getId());
							
							this.areaChat.setText(chatM.toString());
							 areaChat.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
							 labelChat.setText("Chat de :"+uclicked.getNombre()+" "+uclicked.getApellidos());
							 selectedChatIdUser = uclicked.getId();
						} catch (UserException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                }
	            });
	            return cell ;
	        });
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@FXML
	public void sendChatMessage()
	{
		try {
		 Usuario u = UserDBContainer.getUser(selectedChatIdUser);
		 
		 System.out.println(" SEND CHAT MSG");
		 if(u!=null)
		 {
			 
			 MessageImpl mip = new MessageImpl(String.valueOf(u.getId()),
						String.valueOf(u), fieldChat.getText(), "",
						System.currentTimeMillis());
				
					UserController.getInstance()
							.sendMessage(new MyCommunicationMessage("", "onSendChatMessage",
									CommunicationCenter.serializeMessage(mip),
									CommunicationCenter.serializeMessage(mip)));
					
					fieldChat.setText("");
		 }
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		 
	}

	/**
	 * Se invoca por la mainApp para devolverse una referencia asi misma
	 *
	 * @param mainApp
	 */
	public void setDataSource(Dbase dbaseCtrl) {
		this.dbase = dbaseCtrl;
		// Add observable list data to the table

	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Abre el ventana con "about us".
	 */
	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("ArduApp: Una aplicacion para Ambient Assited Living");
		alert.setHeaderText("About: ");
		alert.setContentText("Author: Grupo 6 ");

		alert.showAndWait();
	}

	/**
	 * Cierra la aplicacion.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

	@FXML
	public void addPatientView() {
		/*
		 * if "fx:controller" is not set in fxml
		 * fxmlLoader.setController(NewWindowController);
		 */
		Scene scene;

		HelperForm hf = new HelperForm();
		UsuarioImpl uimp = new UsuarioImpl();
		uimp.setIdParent(UserController.getInstance().user.getId());
		uimp.setType(Usuario.USUARIO_PACIENTE);
		Stage stage = new Stage();
		stage.setTitle("Añadir paciente");
		hf.addAllowedField("getUsername", "Nombre de usuario");
		
		hf.addAllowedField("getIdParent", "Id supervisor");
		hf.addAllowedField("getType", "Tipo");
		hf.addAllowedField("getNombre", "Nombre ");
		
		hf.addAllowedField("getApellidos", "Apellidos");
		hf.addAllowedField("getCalle", "Calle");
		hf.addAllowedField("getCodigoPostal", "Codigo postal");
		hf.addAllowedField("getCiudad", "Ciudad");
		hf.addAllowedField("getPassword", "Contraseña");

		VBox vbox = new VBox(hf.getFormpane(hf.generateFormByClass(uimp)));
		scene = new Scene(vbox, 600, 400);

		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void addSensor() {
		Scene scene;

		HelperForm hf = new HelperForm();

		Stage stage = new Stage();
		stage.setTitle("Añadir Sensor");

		hf.addAllowedField("getType", "Tipo de Sensor");
		hf.addAllowedField("getId_user", "Usuario asignado");

		VBox vbox = new VBox(hf.getFormpane(hf.generateFormByClass(new Sensor())));
		scene = new Scene(vbox, 600, 400);

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Rellena todos los campos sobre la paciente. Si la persona especificada es
	 * null, el texto se borra.
	 *
	 * @param user the person or null
	 */
	private void showPatientDetails(Usuario user) {
		if (user != null) {
			// Rellena las etiquetas con datos del objeto persona.
			nombrelb.setText(user.getNombre());
			apellidoslb.setText(user.getApellidos());
			callelb.setText(user.getCalle());
			codigoPostallb.setText(Integer.toString(user.getCodigoPostal()));
			ciudadlb.setText(user.getCiudad());
			// cumplelb.setText(DateUtil.format(user.getBirthday()));

		} else {
			// Si el Paciente es null, limpiar el texto de las etiquetas.
			nombrelb.setText("");
			apellidoslb.setText("");
			callelb.setText("");
			codigoPostallb.setText("");
			ciudadlb.setText("");
			// cumplelb.setText("");
		}
	}

	/**
	 * Invocada cuando se pulse "Deletebtn".
	 */
	@FXML
	private void handleDeletePatient() {
		int selectedIndex = patientTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			patientTable.getItems().remove(selectedIndex);

			// Guardamos los cambios en el archivo de cuentas
			dbase.saveUserToAccountFile();
		} else {
			// Nada seleccionado.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Sin Seleccion");
			alert.setHeaderText("Ninguna persona seleccionada");
			alert.setContentText("Selecciona un persona de la tabla.");

			alert.showAndWait();
		}
	}

	/**
	 * Se llama cuando el Usuario hace click en "NEW". Abre el dialog para añadir
	 * los datos del nuevo paciente.
	 */
	@FXML
	private void handleNewPatient() {
		Paciente tempPerson = new Paciente();
		boolean okClicked = mainApp.showPatientEditDialog(tempPerson);
		if (okClicked) {
			dbase.getuserData().add(tempPerson);
			dbase.saveUserToAccountFile();
		}
	}

	/**
	 * Invocada cuando se pulsa el "BOTON EDITAR". Abre una ventana con los detalled
	 * del paciente seleccionado
	 */
	@FXML
	private void handleEditPatient() {
		Usuario selectedPerson = patientTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {
			boolean okClicked = true;
			if (okClicked) {
				showPatientDetails(selectedPerson);
				// Salvamos los datos en el archivo para los usuarios.
				dbase.saveUserToAccountFile();
			}

		} else {
			// Nada seleccionado.
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Sin Seleccion");
			alert.setHeaderText("Ninguna persona seleccionada");
			alert.setContentText("Selecciona un persona de la tabla.");

			alert.showAndWait();
		}
	}

	public void loadData() {
		// Añade un observable list a la tabla

	}

	@Override
	public boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		switch (event.name) {
		case "preSaveObject":

			if (event.object instanceof UsuarioImpl) {
				UsuarioImpl p = (UsuarioImpl) event.object;

				p.setIdParent(UserController.getInstance().user.getId());
				Context.getInstance().doAction(new CustomEvent("SaveObject", p));

				clientList.add(p);
			}else {
				Context.getInstance().doAction(new CustomEvent("SaveObject", event.object));
			}

			break;
		case "SaveObject":
			if (event.object instanceof UsuarioImpl) {
				UsuarioImpl p = (UsuarioImpl) event.object;
				

				try {
					if (UserDBContainer.getInstance().register(p)) {
						System.out.println(" POST SAVE");
						Context.getInstance()
								.doAction(new CustomEvent("postSaveObject", UserDBContainer.getUser(p.getNombre())));
					}

				} catch (UserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (event.object instanceof Sensor) {
				Sensor s = (Sensor) event.object;
				if (s.getId_sensor() > 0) {
					System.out.println("actu sensor");
					SensorContainer.getInstance().updateSensor(s);
				} else {		System.out.println("añadir sensor");
					SensorContainer.getInstance().addSensor(s);
				}

			}

			break;
		case "doneReceiveAlert":
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
			SensorNotification sensorNot =(SensorNotification) event.object;;
			Notifier.INSTANCE.setPopupLifetime(Duration.seconds(10));
			
			try {
				Usuario ux = UserDBContainer.getUser(sensorNot.getId_user());
				Notifier.INSTANCE.notifyWarning("Alerta Sensor "+Configuration.getValue("sensor.types."+sensorNot.getType())+sensorNot.getId_sensor()+" DEL USUARIO "+ux.getId()+" "+ux.getNombre()+" ","Valor de la alerta"+sensorNot.getValue()+" |"+ux.getId()+" "+ux.getNombre()+" "+ux.getApellidos());
				notificationArea.appendText(formatNotification(sensorNot));
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			System.out.println(" Nueva alerta ");
				}
			});
		 
		break;
	 
		case "doneUpdateValues":
			ArduMessageDef amdf = (ArduMessageDef) event.object;
			DecimalFormat df = new DecimalFormat("#.####");

			Sensor s = SensorContainer.getInstance().getSensorByID(amdf.getId());

			Iterator<Sensor> iterator = sensorList.iterator();
			while (iterator.hasNext()) {
				Sensor tablesensor = iterator.next();

				if (s.getId_sensor() == (int) tablesensor.getId_sensor()) {
					try {

						Double num = Double.parseDouble(amdf.getMessage());

						tablesensor.setLastValue(df.format(num));
						tablesensor.setLast_message(s.getLast_message());

					} catch (NumberFormatException e) {
						continue;
					}

				}

			}

			if(tableSensor!=null)
				tableSensor.refresh();
			break;
		case "onReceiveChatMessage":
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
			MyCommunicationMessage mimsj = null;

			if (event.object instanceof MyCommunicationMessage) {
				mimsj = (MyCommunicationMessage) event.object;

				MessageImpl msimp = (MessageImpl) CommunicationCenter
						.deserializeMessageTO(mimsj.getObjectmessage().toString(), MessageImpl.class);
				Usuario ux = null;
				Usuario uxTo = null;

				System.out.println("MENSAJE RECIBIDO");
				try {
					ux = UserDBContainer.getUser(Integer.parseInt(msimp.getFrom()));

					if (ux != null) {
						uxTo = UserDBContainer.getUser(ux.getIdParent());
 
					 
						Notifier.INSTANCE.setPopupLifetime(Duration.seconds(10));
						Notifier.INSTANCE.notifySuccess(" Nuevo mensaje recibido ",formatMessage(msimp, ux, uxTo));
						
						chatMessages.put(ux.getId(), chatMessages.get(ux.getId()).append(formatMessage(msimp, ux, uxTo)));
						StringBuilder chatM = chatMessages.get(ux.getId());
						
						areaChat.appendText(formatMessage(msimp, ux, uxTo));
						 areaChat.setText(chatM.toString());
						 areaChat.setScrollTop(0);
						  areaChat.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
						
					}

				} catch (UserException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

			}
				}});
			break;
		}
		return false;
	}
	
	private String formatMessage(Message m, Usuario us, Usuario uparent) {
		String ms = "";

		if (Integer.parseInt(m.getFrom()) == us.getId()) {
		
			ms = " [" + Tools.formatTimestamt(m.getTimestamp()) + " Paciente : " + us.getNombre() + " " + us.getApellidos() + " ]:"+ m.getContent() + " \n";
		} else {

			if(uparent!=null)
				ms = " [" + Tools.formatTimestamt(m.getTimestamp()) + " Yo ]: " + m.getContent() + " \n";
					
		}

		return ms;
	}
	private String formatNotification(SensorNotification sn) {
		String val="";
		try {
			Usuario u = UserDBContainer.getUser(sn.getId_user());

			  val = "[ " + Tools.formatTimestamt(sn.getTimestamp()) + " ]  Sensor:" + sn.getId_sensor()+" Usuario:"+u.getNombre()+" "+u.getApellidos()+" [["+sn.getValue()+"]] \n";

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;

	}

}