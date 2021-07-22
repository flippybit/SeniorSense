package application.model.context;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;

public class UserSocket implements CustomActionListener,Runnable{
	
	protected static final int BUFFER_SIZE = 64010;
	
	private Socket usock=null;
	 
	
	private InputStream clientIn;

	private OutputStream clientOut;
	
	private boolean running;
	
	public UserSocket(Socket usock)
	{
		this.usock=usock;
	}

	@Override
	public boolean onAction(CustomEvent actionName)
	{
	 
		
		return false;
	}
	
	public boolean socketStatus()
	{
		return usock.isConnected();
		
	}
	
	public synchronized boolean write(String message) throws IOException
	{
	 
			clientOut.write(message.getBytes());
			clientOut.flush();
	 
		return false;
	}

	@Override
	public void run() {
		running = true;
		try {

			usock.setKeepAlive(true);

			// Obtain client & server input & output streams
			clientIn = usock.getInputStream();
			clientOut = usock.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			while (running) {
				int bytesRead1 = clientIn.read(buffer);
				if (bytesRead1 == -1)
					break; // End of stream is reached --> exit

				  
				MyCommunicationMessage message =  (MyCommunicationMessage) CommunicationCenter.deserializeMessage(new String(buffer, 0, bytesRead1)); 
				Context.getInstance().doAction(new CustomEvent("onUserReceiveMessage",message));
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

				usock.close();
				Context.getInstance().doAction(new CustomEvent("onSocketUserDisconnected",null));
			 
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
