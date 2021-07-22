package application.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.internal.LinkedTreeMap;

import application.Configuration;
import application.container.MessageContainer;
import application.container.UserDBContainer;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MessageImpl;
import application.implemet.MyCommunicationMessage;
import application.interfaces.ArduMessage;
import application.interfaces.ArduMessageListener;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.ClientSocket;
import application.model.Supervisor;
import application.model.TCPServer;
import application.model.UsuarioImpl;
import application.model.context.Context;

public class ServerController implements CustomActionListener {

	public static ServerController self = null;

	private TCPServer server = null;

	private Thread threadServer = null;

	public ServerController() {

		if (self == null)
			self = this;

		Context.getInstance().registerListener("onSendChatMessage", this);
		Context.getInstance().registerListener("dologout", this);
		
	}

	public static ServerController getInstance() {

		if (self == null)
			self = new ServerController();

		return self;

	}

	public synchronized void sendMessageToSocketID(String socketID, MyCommunicationMessage ms) throws IOException {
		ConcurrentHashMap<String, ClientSocket> clients = this.server.getClientList();

		if (clients.containsKey(socketID)) {
			ClientSocket cst = clients.get(socketID);
			Context.getInstance().doAction(new CustomEvent("onServerSendMessage", ms));

			cst.sendMessage(ms);
		}
	}

	public synchronized void sendMessageToAll(MyCommunicationMessage ms) throws IOException {
		ConcurrentHashMap<String, ClientSocket> clients = this.server.getClientList();
		Iterator it = clients.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();

			ClientSocket cf = (ClientSocket) pair.getValue();

			sendMessageToSocketID((String) pair.getKey(), ms);

		}
	}

	public synchronized void sendMessageToUserID(int id, MyCommunicationMessage ms) throws IOException {
		ConcurrentHashMap<String, ClientSocket> clients = this.server.getClientList();
		Iterator it = clients.entrySet().iterator();
		boolean searchmore = true;
		while (it.hasNext() && searchmore) {
			Map.Entry pair = (Map.Entry) it.next();

			ClientSocket cf = (ClientSocket) pair.getValue();
			if (cf.getUser() != null && cf.getUser().getId() == id) {
				sendMessageToSocketID((String) pair.getKey(), ms);
				 
			}
		}
	}

	public boolean initServer(String bound, int port) throws UnknownHostException {
		server = new TCPServer("Server socket", InetAddress.getByName(bound), (int) port);

		threadServer = new Thread(server);
		threadServer.start();
		return true;

	}

	@Override
	public boolean onAction(CustomEvent event) {

		MyCommunicationMessage mimsj = null;

		if (event.object instanceof MyCommunicationMessage) {
			mimsj = (MyCommunicationMessage) event.object;
		}

		switch (event.name) {
		case "onServerReceiveMessage":

			if (mimsj != null && mimsj.action != null && mimsj.action.equals("ardu")) {
				ArduMessageDef amd = CommunicationCenter.getGson().fromJson(mimsj.getMessage(), ArduMessageDef.class);
				if (amd.getTimestamp() == 0) {
					amd.setTimestamp(System.currentTimeMillis());

				}
				amd.setSocketID(mimsj.getSocketId());
				Context.getInstance().doAction(new CustomEvent("doArduMessageReceived", amd));
			} else {
				if (mimsj != null && mimsj.getAction() != null && !mimsj.getAction().equals("onServerReceiveMessage")) {
					Context.getInstance().doAction(new CustomEvent(mimsj.getAction(), mimsj));
				}
			}

			break;
		case "dologout":
			
			
			
			ConcurrentHashMap<String, ClientSocket> clientsx = this.server.getClientList();
			Iterator it = clientsx.entrySet().iterator();
			boolean searchmore = true;
			int x=0;
			while (it.hasNext() && searchmore) {
				Map.Entry pair = (Map.Entry) it.next();

				ClientSocket cf = (ClientSocket) pair.getValue();
				 if(cf.getUser()!=null)
				 {
				 
					 cf.setUser(null);
					 searchmore=false;
				 }
				 x++;
			}
			break;
		case "onServerSendMessage":

			break;
		case "onSendChatMessage":
			System.out.println("Mensaje enviado ");

			MessageImpl msimp = (MessageImpl) CommunicationCenter
					.deserializeMessageTO(mimsj.getObjectmessage().toString(), MessageImpl.class);

			MessageContainer.getInstance().addMessage(msimp);
			Usuario ux;
			Usuario uxTo;
			try {
				ux = UserDBContainer.getUser(Integer.parseInt(msimp.getFrom()));

				 
				if (ux != null) {
					try {
						ServerController.getInstance().sendMessageToUserID(ux.getId(), new MyCommunicationMessage("","onReceiveChatMessage",
								CommunicationCenter.serializeMessage(msimp), CommunicationCenter.serializeMessage(msimp)));

						uxTo = UserDBContainer.getUser(ux.getIdParent());
						if (uxTo != null) {
						 
							ServerController.getInstance().sendMessageToUserID(uxTo.getId(), new MyCommunicationMessage("", "onReceiveChatMessage",
									CommunicationCenter.serializeMessage(msimp), CommunicationCenter.serializeMessage(msimp)));
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} catch (UserException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			break;
		case "onActionLogin":
			System.out.println("actionLogsin");

			LinkedTreeMap lintree = (LinkedTreeMap) mimsj.objectmessage;

			String jsonmap = CommunicationCenter.getGson().toJson(lintree);

			UsuarioImpl usimpl = (UsuarioImpl) CommunicationCenter.deserializeMessageTO(jsonmap, UsuarioImpl.class);
			if (usimpl instanceof UsuarioImpl) {

				UsuarioImpl um = usimpl;
				try {
					Usuario umloged = UserDBContainer.getInstance().login(um.getNombre(), um.getPassword());
					if (umloged != null) {
						ConcurrentHashMap<String, ClientSocket> clients = this.server.getClientList();
						if (clients.containsKey(mimsj.getSocketId())) {
							ClientSocket st = clients.get(mimsj.getSocketId());
							st.setUser(umloged);
							this.server.addClient(mimsj.getSocketId(), st);
						}
						this.sendMessageToSocketID(mimsj.getSocketId(),
								new MyCommunicationMessage(mimsj.getSocketId(), "onSucessfullLogin", "ok", umloged));
					}

				} catch (UserException e) {
					// TODO Auto-generated catch block
					try {
						this.sendMessageToSocketID(mimsj.getSocketId(),
								new MyCommunicationMessage(mimsj.getSocketId(), "onFailedLogin", "ok", false));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			break;
		}

		return true;
	}

}
