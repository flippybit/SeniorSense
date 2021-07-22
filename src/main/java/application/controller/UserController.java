package application.controller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import application.controller.classes.AutoUpdate;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.ArduMessage;
import application.interfaces.CommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.SensorNotification;
import application.model.context.Context;
import application.model.context.UserSocket;

public class UserController extends AutoUpdate {

	private static UserController self;

	private UserSocket usocket;
	
	private volatile String idSocket= null;
	
	public volatile Usuario user= null;

	private Thread uthread;

	public UserController() {
		if (self == null)
			self = this;
	}

	public static UserController getInstance() {
		if (self == null)
			self = new UserController();
		return self;
	}

	public synchronized boolean sendMessage(CommunicationMessage ms) throws IOException {
		if (usocket != null) {

			if(ms.getSocketId()==null || ms.getSocketId().equals(""))
			{
				ms.setSocketId(idSocket);
			}
			
			usocket.write(CommunicationCenter.serializeMessage(ms));
			Context.getInstance().doAction(new CustomEvent("onUserSendMessage", ms));
			 
		}

		return false;
	}

	public void ConnectToServer(String bound, int port) throws UnknownHostException, IOException {

		usocket = new UserSocket(new Socket(bound, port));
		uthread = new Thread(usocket);
		uthread.start();
	}

	@Override
	public boolean onAction(CustomEvent event) {
		MyCommunicationMessage mimsj=null;
		
		if(event.object instanceof MyCommunicationMessage)
		{
			  mimsj = (MyCommunicationMessage) event.object;
		} 

		switch (event.name) {
		case "onUserReceiveMessage":
			 
			if(mimsj!=null && mimsj.getAction()!=null && !mimsj.getAction().equals("onUserReceiveMessage"))
			{
				Context.getInstance().doAction(new CustomEvent(mimsj.getAction(),mimsj));
			}
			
			break;
		case "onUserSendMessage":
		
			
			break;
		case "onSocketUserConnected":
			if(mimsj.getMessage().equals("ok"))
			{
				this.idSocket=mimsj.getSocketId();
				System.out.println("is ok yeah");
				System.out.println(this.idSocket);
				
				Context.getInstance().doAction(new CustomEvent("doneSocketUserConnected",mimsj));
			}
			
			break;
		case "onSocketUserDisConnected":
			if(mimsj.getMessage().equals("ok"))
			{
				 
				Context.getInstance().doAction(new CustomEvent("doneSocketUserDisConnected",mimsj));
			}
			
			break;
		case "doUpdateValues":
		 
				ArduMessageDef amd = CommunicationCenter.getGson().fromJson(mimsj.getMessage(), ArduMessageDef.class);
				if(amd.getTimestamp()==0)
				{
					amd.setTimestamp(System.currentTimeMillis());
				
				}
				amd.setSocketID(mimsj.getSocketId()); 
				Context.getInstance().doAction(new CustomEvent("doneUpdateValues",amd));
			break;
		case "onReceiveAlert":
			  
			SensorNotification sensorNot = (SensorNotification) CommunicationCenter
			.deserializeMessageTO(mimsj.getMessage().toString(), SensorNotification.class);

				if(sensorNot.getTimestamp()==0)
				{
					sensorNot.setTimestamp(System.currentTimeMillis());
				
				}
			 
				Context.getInstance().doAction(new CustomEvent("doneReceiveAlert",sensorNot));
			break;
		}
		return true;
	}

}
