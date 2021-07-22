package application.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.Configuration;
import application.Main;
import application.container.SensorContainer;
import application.container.UserDBContainer;
import application.controller.classes.AutoUpdate;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MessageImpl;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.Paciente;
import application.model.Sensor;
import application.model.SensorNotification;
import application.model.Supervisor;
import application.model.UsuarioImpl;
import application.model.context.Context;
import application.model.context.HelperForm;
import eu.hansolo.enzo.notification.Notification.Notifier;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

// controlador Admin
public class AdminController extends AutoUpdate {

	@FXML
	private TableView<UsuarioImpl> tableSupervisor;
	@FXML
	private TableView<UsuarioImpl> patientTable;
	@FXML
	private TableView<Sensor> tableSensor;

	private ObservableList<Sensor> sensorList;
	private ObservableList<UsuarioImpl> patientList;
	private ObservableList<UsuarioImpl> supervisorList;
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

	@FXML
	public void addSupervisor() {
		Scene scene;

		HelperForm hf = new HelperForm();

		Stage stage = new Stage();
		stage.setTitle("Añadir Supervisor");

		hf.addAllowedField("getNombre", "Nombre ");
		hf.addAllowedField("getApellidos", "Apellidos");
		hf.addAllowedField("getCalle", "Calle");
		hf.addAllowedField("getCodigoPostal", "Codigo postal");
		hf.addAllowedField("getCiudad", "Ciudad");
		hf.addAllowedField("getPassword", "Contraseña");

		VBox vbox = new VBox(hf.getFormpane(hf.generateFormByClass(new Supervisor())));
		scene = new Scene(vbox, 600, 400);

		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void addPatient() {
		Scene scene;

		HelperForm hf = new HelperForm();

		Stage stage = new Stage();
		stage.setTitle("Añadir Paciente");

		hf.addAllowedField("getNombre", "Nombre ");
		hf.addAllowedField("getApellidos", "Apellidos");
		hf.addAllowedField("getCalle", "Calle");
		hf.addAllowedField("getCodigoPostal", "Codigo postal");
		hf.addAllowedField("getCiudad", "Ciudad");
		hf.addAllowedField("getPassword", "Contraseña");
		hf.addAllowedField("getIdParent", "Parent id");
		VBox vbox = new VBox(hf.getFormpane(hf.generateFormByClass(new Paciente())));
		scene = new Scene(vbox, 600, 400);

		stage.setScene(scene);
		stage.show();
	}

	@FXML
	public void sendChatMessage() {

	}

	@FXML
	private void initialize() {
		
		Context.getInstance().registerListener("preSaveObject", this);
		Context.getInstance().registerListener("SaveObject", this);
		Context.getInstance().registerListener("doneReceiveAlert", this); 
		Context.getInstance().registerListener("onReceiveChatMessage", this); 
		Context.getInstance().registerListener("doneUpdateValues", this);

		loadTablePacient();

		loadTableSupervisor();

		loadTableSensor();
	}

	private void loadTablePacient() {
		
		UsuarioImpl u = null;
		try {
			u = (UsuarioImpl) UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
		} catch (UserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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

		List<UsuarioImpl> impl=   UserDBContainer.getInstance().getAll();
		List<UsuarioImpl> pacients = new ArrayList<UsuarioImpl>();
		
		System.out.println(pacients.size()+" PACIENTES ");
		for(UsuarioImpl us : impl)
		{
			if(us.getType()==UsuarioImpl.USUARIO_PACIENTE)
			pacients.add(us);
		}
		patientList = FXCollections.observableArrayList(pacients);

		for (UsuarioImpl uxd : patientList) {
			System.out.println(uxd.getNombre() + "  " + uxd.getApellidos() + " " + uxd.getId());
			patientTable.getItems().add(uxd);
		}

		patientTable.setItems(patientList);
		patientTable.refresh();
	}

	private void loadTableSupervisor() {
		UsuarioImpl u = null;
		try {
			u = (UsuarioImpl) UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
		} catch (UserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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
		tableSupervisor.getColumns().clear();
		tableSupervisor.getColumns().addAll(tableColumns);
		List<UsuarioImpl> impl=   UserDBContainer.getInstance().getAll();
		List<UsuarioImpl> pacients = new ArrayList<UsuarioImpl>();
		for(UsuarioImpl us : impl)
		{
			if(us.getType()==UsuarioImpl.USUARIO_SUPERVISOR)
			pacients.add(us);
		}
		supervisorList = FXCollections.observableArrayList(pacients);

		for (UsuarioImpl uxd : supervisorList) {
			System.out.println(uxd.getNombre() + "  " + uxd.getApellidos() + " " + uxd.getId());
			tableSupervisor.getItems().add(uxd);
		}

		tableSupervisor.setItems(supervisorList);
		tableSupervisor.refresh();
	}

	private void loadTableSensor() {
		UsuarioImpl u = null;
		try {
			u = (UsuarioImpl) UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
		} catch (UserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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

	 

		TableColumn actionColSensor = new TableColumn("Action");
		actionColSensor.setText("action");
		actionColSensor.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
		
		tableSensorColumns.add(actionColSensor);
		
		
		this.tableSensor.getColumns().clear();
		tableSensor.getColumns().addAll(tableSensorColumns);

		
		sensorList = FXCollections.observableArrayList(SensorContainer.getInstance().getAllSensorByUserId(0, u.getId()));

	 

		tableSensor.setItems(sensorList);
		tableSensor.refresh();

		// limpiar los detalles del paciente.
		// showPatientDetails(null);

		// Escuchar cambios de seleccion, y muestra los detalles de la persona
		// cuando se cambia.

	}
	
	
	@Override
	public boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		switch (event.name) {
		case "preSaveObject":

			if (event.object instanceof UsuarioImpl) {
				UsuarioImpl p = (UsuarioImpl) event.object;

				p.setUsername(p.getNombre());
				
				p.setIdParent(UserController.getInstance().user.getId());
				Context.getInstance().doAction(new CustomEvent("SaveObject", p));

				if(p.getType()==UsuarioImpl.USUARIO_SUPERVISOR)
				{
					supervisorList.add(p);
				}else {
					patientList.add(p);
				}
				 
				
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
		case "doneUpdateValues":
			ArduMessageDef amdf = (ArduMessageDef) event.object;
			DecimalFormat df = new DecimalFormat("#.####");

			Sensor s = SensorContainer.getInstance().getSensorByID(amdf.getId());

			Iterator<Sensor> iterator = sensorList.iterator();
			 boolean found = false;
			while (iterator.hasNext() && !found) {
				Sensor tablesensor = iterator.next();

				if (s.getId_sensor() == (int) tablesensor.getId_sensor()) {
					try {

						Double num = Double.parseDouble(amdf.getMessage());

						tablesensor.setLastValue(df.format(num));
						tablesensor.setLast_message(s.getLast_message());
						found = true;
					} catch (NumberFormatException e) {
						continue;
					}

				}

			}

			if(tableSensor!=null)
				tableSensor.refresh();
			break;
	 
		}
		return false;
	}
}
