package application.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.ArduMessage;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.context.Context;

public class ClientSocket extends Thread {

	protected static final int BUFFER_SIZE = 64010;

	public Socket mClientSocket;
	
	private Usuario user=null;

	private InputStream clientIn;

	private OutputStream clientOut;

	private boolean running;
	
	private TCPServer tcpserver;
	
	private String id;

	public ClientSocket(TCPServer tcpserver,String id, Socket clientsocket) {

		this.id=id;
		this.tcpserver=tcpserver;
		this.mClientSocket = clientsocket;

	}
	public Usuario getUser()
	{
		return user;
	}
	public void setUser(Usuario u)
	{
		this.user=u;
	}
	public synchronized void sendMessage(MyCommunicationMessage ms) throws IOException
	{
	
		this.clientOut.write(CommunicationCenter.serializeMessage(ms).getBytes());
		this.clientOut.flush();
	}

	public void run() {
		running = true;
		try {

			mClientSocket.setKeepAlive(true);

			// Obtain client & server input & output streams
			clientIn = mClientSocket.getInputStream();
			clientOut = mClientSocket.getOutputStream();

			this.sendMessage(new MyCommunicationMessage(id,"onSocketUserConnected","ok",null));
			System.out.println("Nuevo CLIENTE "+this.id+" "+this.mClientSocket.getInetAddress()+" "+this.mClientSocket.getRemoteSocketAddress().toString());
			
			byte[] buffer = new byte[BUFFER_SIZE];
			while (running) {
				int bytesRead1 = clientIn.read(buffer);
				if (bytesRead1 == -1)
					break; // End of stream is reached --> exit
				
				System.out.println("MENSAJE RECIBIDO "+new String(buffer, 0, bytesRead1));
		
				
				
 				MyCommunicationMessage message = (MyCommunicationMessage) CommunicationCenter.deserializeMessage(new String(buffer, 0, bytesRead1));
				
				Context.getInstance().doAction(new CustomEvent("onServerReceiveMessage",message));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println(" EXIT CLIENT");
			connectionBroken();
		}
	}

	public synchronized void connectionBroken() {

		try {
			if (running) {

				running = false;

				clientIn.close();
				;
				clientOut.close();

				mClientSocket.close();
				
				this.tcpserver.removeClientById(id);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
