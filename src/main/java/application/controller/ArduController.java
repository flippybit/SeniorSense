package application.controller;

import java.io.IOException;

import application.Configuration;
import application.container.SensorContainer;
import application.container.UserDBContainer;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.Sensor;
import application.model.SensorData;
import application.model.SensorNotification;
import application.model.Supervisor;
import application.model.UsuarioImpl;
import application.model.Administrador;

import application.model.context.Context;

public class ArduController implements CustomActionListener {

	
	public static long TIME_ELAPSED_BEETWEEN_DATA = 60000;

	public static long TIME_ELAPSED_BEETWEEN_NOTIFICATIONS = 60000;
	
	public ArduController() {
		
		TIME_ELAPSED_BEETWEEN_DATA = Long.parseLong(Configuration.getValue("notification.TIME_ELAPSED_BEETWEEN_DATA"));
		TIME_ELAPSED_BEETWEEN_NOTIFICATIONS = Long.parseLong(Configuration.getValue("notification.TIME_ELAPSED_BEETWEEN_NOTIFICATIONS"));
		Context.getInstance().registerListener("doArduMessageReceived", this);
		Context.getInstance().registerListener("docheckSensorData", this);

	}



	@Override
	public boolean onAction(CustomEvent event) {
		switch (event.name) {
		case "doArduMessageReceived":

			ArduMessageDef amdf = (ArduMessageDef) event.object;

			// buscar usuario etc.

			Sensor s = SensorContainer.getInstance().getSensorByID(amdf.getId());
			
			System.out.println(s.getId_user()+" id user");
			if (s != null && s.getId_user() > 0) {
				long timestamp = amdf.getTimestamp();
				if (timestamp <= 0) {
					timestamp = System.currentTimeMillis();
				}

				SensorData sd = new SensorData(s.getId_sensor(), s.getId_user(), Float.parseFloat(amdf.getMessage()),
						timestamp, 0);
				// enviar datos al usuario del socket y su padre

				// guardamos el data? si el ultimo mensaje se envio hace al menos
				// TIME_ELAPSED_BEETWEEN_MESSAGES, para evitar inserts en vano
				if ((System.currentTimeMillis() - s.getLast_message()) >= TIME_ELAPSED_BEETWEEN_DATA) {
					SensorContainer.getInstance().addSensorData(sd);
					s.setLast_message(timestamp);
					// update sensor last message;
					SensorContainer.getInstance().updateSensor(s);
				}

				;
				// notificar?

				try {
					Usuario u = UserDBContainer.getUser(s.getId_user());
					if (u != null) {
						// supervisor

						ServerController.getInstance().sendMessageToUserID(u.getId(), new MyCommunicationMessage(
								CommunicationCenter.serializeMessage(amdf), 0, "doUpdateValues"));

						UsuarioImpl sp = null;
						
						if (u.getIdParent() > 0) {
							
							sp = (UsuarioImpl) UserDBContainer.getUser(u.getIdParent());
							ServerController.getInstance().sendMessageToUserID(sp.getId(), new MyCommunicationMessage(
									CommunicationCenter.serializeMessage(amdf), 0, "doUpdateValues"));

						}
						
						UsuarioImpl adm = (UsuarioImpl) UserDBContainer.getUser("admin");
						if(adm!=null)
						{
							ServerController.getInstance().sendMessageToUserID(adm.getId(), new MyCommunicationMessage(
									CommunicationCenter.serializeMessage(amdf), 0, "doUpdateValues"));
						}

						Context.getInstance().doAction(new CustomEvent("docheckSensorData", sd));
						// Enviar alerta=?????????
					}

				} catch (UserException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;
		// Comprobamos el dato del sensor, y si debemos enviar una notificacion
		case "docheckSensorData":
			SensorData sd = (SensorData) event.object;

			sensorHaveToAlert(sd);
			break;
		}
		return false;
	}

	private boolean sensorHaveToAlert(SensorData sd) {
		Sensor s = SensorContainer.getInstance().getSensorByID(sd.getId_sensor());
		boolean sendNotify = false;
		// LAST SENSOR NOTIFY
		SensorNotification lastNotify = SensorContainer.getInstance()
				.getLastSensorNotificationBySensorId(sd.getId_sensor());
		if (lastNotify == null
				|| ((System.currentTimeMillis() - lastNotify.getTimestamp()) >= TIME_ELAPSED_BEETWEEN_NOTIFICATIONS)) {
	 
			System.out.println(s.getId_sensor()+" "+s.getType());
			double alertValue = Double.parseDouble(Configuration.getValue("sensor.alerts."+s.getType()));
			if(alertValue>0 && alertValue<sd.getValue())
			{
				sendNotify = true;
			}
 
			if (sendNotify) {
				SensorNotification sn = new SensorNotification(0, s.getType(), s.getId_sensor(),
						System.currentTimeMillis(), 0, String.valueOf(sd.getValue()), s.getId_user());
				int sensorId = SensorContainer.getInstance().addNotify(sn);
				 
				if (sensorId > 0) {
					sn.setId_notification(sensorId);
					Usuario u;
					try {
						u = UserDBContainer.getUser(s.getId_user());

						MyCommunicationMessage ms = new MyCommunicationMessage(CommunicationCenter.serializeMessage(sn),0,
								"onReceiveAlert");
					 
						ServerController.getInstance().sendMessageToUserID(u.getId(), ms);
						if (u.getIdParent() > 0) {
							ServerController.getInstance().sendMessageToUserID(u.getIdParent(), ms);
						}

					} catch (UserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}

		return false;

	}

}
