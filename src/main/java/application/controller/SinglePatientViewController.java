package application.controller;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
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

public class SinglePatientViewController extends AutoUpdate {

	@FXML
	private TableView<Sensor> sensorTable;
	@FXML
	private TableColumn<Sensor, String> columnaId;
	@FXML
	private TableColumn<Sensor, String> columnaTipo;
	@FXML
	private TableColumn<Sensor, String> columnaValor;
	@FXML
	private TableColumn<Sensor, String> columnaMensaje;

	@FXML
	private Label lbNombre;
	@FXML
	private Label lbApellidos;
	@FXML
	private Label lbConnectionState;
	@FXML
	private Label lbGasState;
	@FXML
	private Label lbTempState;
	@FXML
	private Label lbBattState;

	@FXML
	private GridPane gridForm;

	@FXML
	private TextArea chatText;

	@FXML
	private TextField writechatText;

	@FXML
	private Button submitMessage;

	// Referencia al mainApp y base de datos.
	private Dbase dbase;
	private Main mainApp;
	private File dataFile;

	@FXML
	GridPane panelStats;

	@FXML
	TextArea notificationArea;

	private ObservableList<Sensor> sensorData = FXCollections.observableArrayList();
	Usuario u;

	Usuario parentUser;

	/**
	 * Inicializa la clase controlador. Este metodo es invocado automaticamente
	 * despues de cargar el fichero fxml.
	 *
	 * Se busca fichero de datos del usuario sino existe se Crea uno.
	 */
	@FXML
	private void initialize() {

		// Inicializar la tabla con Nombre y Apellidos del paciente
		// packetSeqColumn.setCellValueFactory(cellData ->
		// cellData.getValue().getNombre());
		// packetTimeColumn.setCellValueFactory(cellData ->
		// cellData.getValue().appellidosProperty());
		Context.getInstance().registerListener("doneUpdateValues", this);
		Context.getInstance().registerListener("doneReceiveAlert", this);
		Context.getInstance().registerListener("preSaveObject", this);
		Context.getInstance().registerListener("SaveObject", this);
		Context.getInstance().registerListener("postSaveObject", this);
		Context.getInstance().registerListener("onReceiveChatMessage", this);

		// limpiar los detalles del paciente.
		showPacketDetails(null);

		// Escuchar cambios de seleccion, y muestra los detalles de la persona
		// cuando se cambia.

		try {
			if (notificationArea != null)
				notificationArea.setEditable(false);

			if (Configuration.getCustomValue("impresionateUser", Usuario.class) != null) {
				u = (Usuario) Configuration.getCustomValue("impresionateUser", Usuario.class);

			} else {
				u = UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
			}

			parentUser = null;
			columnaId.setCellValueFactory(new PropertyValueFactory<>("id_sensor"));
			columnaTipo.setCellValueFactory(new PropertyValueFactory<>("type"));
			columnaValor.setCellValueFactory(new PropertyValueFactory<>("lastValue"));
			columnaMensaje.setCellValueFactory(new PropertyValueFactory<>("last_message"));
			

			List<Sensor> misSensores = SensorContainer.getInstance().getAllSensorByUserId(u.getId());
			System.out.println("user loged id : " + u.getId());
			sensorData = FXCollections.observableArrayList(misSensores);
		System.out.println(" esto es sensor data ");
			// datos y notificaciones;

			for (Sensor s : misSensores) {

				List<SensorNotification> notifications = SensorContainer.getInstance()
						.getSensorNotifications(s.getId_sensor());

				for (SensorNotification sn : notifications) {
					notificationArea.appendText(formatNotification(sn));
				}
			}

			sensorTable.setItems(sensorData);

			// Crear formulario de usuario
			HelperForm hf = new HelperForm();

			hf.addAllowedField("getNombre", "Nombre ");
			hf.addAllowedField("getApellidos", "Apellidos");
			hf.addAllowedField("getCalle", "Calle");
			hf.addAllowedField("getCodigoPostal", "Codigo postal");
			hf.addAllowedField("getCiudad", "Ciudad");
			hf.addAllowedField("getPassword", "Contraseña");
			Map<String, TextField> listaFieldForm = hf.generateFormByClass(u);
			gridForm.add(hf.getFormpane(listaFieldForm), 0, 0);

			int id_parent = 0;
			if (u.getIdParent() > 0) {

				parentUser = UserDBContainer.getUser(u.getIdParent());
				id_parent = u.getIdParent();
			}

			//
			List<Message> messageList = MessageContainer.getInstance().getUserMesages(u.getId(), 100);
			for (Message m : messageList) {
				chatText.appendText(formatMessage(m, u, parentUser));
			}

			submitMessage.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {

						if (u.getIdParent() > 0) {
							MessageImpl mip = new MessageImpl(String.valueOf(u.getId()),
									String.valueOf(parentUser.getId()), writechatText.getText(), "",
									System.currentTimeMillis());
							UserController.getInstance()
									.sendMessage(new MyCommunicationMessage("", "onSendChatMessage",
											CommunicationCenter.serializeMessage(mip),
											CommunicationCenter.serializeMessage(mip)));

						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});

			FXMLLoader loader = new FXMLLoader();

			try {
				GridPane mypanelStats = (GridPane) loader.load(Main.class.getResource("views/" + "StatsView.fxml"));

				StatsController stc = loader.getController();

				panelStats.add(mypanelStats, 0, 0);
				panelStats.autosize();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// añadir listener de autoupdate

	}

	private String formatMessage(Message m, Usuario us, Usuario uparent) {
		String ms = "";

		if (Integer.parseInt(m.getFrom()) == us.getId()) {
			ms = " [" + Tools.formatTimestamt(m.getTimestamp()) + " Yo ]: " + m.getContent() + " \n";
		} else {

			if (uparent != null)
				ms = " [" + Tools.formatTimestamt(m.getTimestamp()) + " SuperVisor : " + uparent.getNombre() + " "
						+ uparent.getApellidos() + " ]:" + m.getContent() + " \n";
		}

		return ms;
	}

	/**
	 * Se llama cuando se pulsa "Crear Cuenta"
	 */
	@FXML
	private void handleConectarBt() {
		// buscar
	}

	/**
	 * Se invoca por la mainApp para devolverse una referencia asi misma
	 *
	 * @param mainApp
	 */
	public void setDataSource(Dbase _dbaseCtrl) {

	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	private void showPacketDetails(Object object) {
		// TODO Auto-generated method stub

	}
	
	//dataSeries.setName(String.valueOf(s.getId_sensor())+" "+Configuration.getValue("sensor.types."+s.getType()));

	private String formatNotification(SensorNotification sn) {
		String val = "";
		try {
			Usuario u = UserDBContainer.getUser(sn.getId_user());

			val = "[ " + Tools.formatTimestamt(sn.getTimestamp()) + " ]  Sensor:" +Configuration.getValue("sensor.types."+sn.getType() ) + " Usuario:"
					+ u.getNombre() + " " + u.getApellidos() + " [[" + sn.getValue() + "]] \n";

		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;

	}

	@Override
	public synchronized boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		super.onAction(event);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				switch (event.name) {
				case "doneUpdateValues":
					ArduMessageDef amdf = (ArduMessageDef) event.object;
					DecimalFormat df = new DecimalFormat("#.####");

					Sensor s = SensorContainer.getInstance().getSensorByID(amdf.getId());

					Iterator<Sensor> iterator = sensorData.iterator();
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

					if (sensorTable != null)
						sensorTable.refresh();
					break;
				case "preSaveObject":
					if (event.object instanceof UsuarioImpl) {
						UsuarioImpl p = (UsuarioImpl) event.object;

						if (p.getType() == Usuario.USUARIO_PACIENTE) {
							System.out.println("SAVE");
							Context.getInstance().doAction(new CustomEvent("SaveObject", p));
						}

					}

					break;
				case "SaveObject":
					if (event.object instanceof UsuarioImpl) {
						UsuarioImpl p = (UsuarioImpl) event.object;
						if (p.getType() == Usuario.USUARIO_PACIENTE) {
							try {
								if (UserDBContainer.getInstance().update(p)) {
									System.out.println(" POST SAVE");
									Context.getInstance().doAction(new CustomEvent("postSaveObject", p));
								}

							} catch (UserException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}

					break;
				case "onReceiveChatMessage":
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

								chatText.appendText(formatMessage(msimp, ux, uxTo));

								if (UserController.getInstance().user.getId() != ux.getId()) {

									Notifier.INSTANCE.setPopupLifetime(Duration.seconds(10));
									Notifier.INSTANCE.notifySuccess(" Nuevo mensaje recibido ",
											formatMessage(msimp, ux, uxTo));

								}
							}

						} catch (UserException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

					}

					break;

				case "doneReceiveAlert":

					SensorNotification sensorNot = (SensorNotification) event.object;
					;
					Notifier.INSTANCE.setPopupLifetime(Duration.seconds(10));
					Notifier.INSTANCE.notifyWarning("Alerta del Sensor "
							+ Configuration.getValue("sensor.types." + sensorNot.getType()) + sensorNot.getId_sensor(),
							"Valor de la alerta" + sensorNot.getValue());

					notificationArea.appendText(formatNotification(sensorNot));
					System.out.println(" Nueva alerta ");

					break;
				}
			}
		});
		return true;
	}

}