package application.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import application.interfaces.ArduMessage;
import application.interfaces.ArduMessageListener; 
 
public class TCPServer implements Runnable {
	
	private List<ArduMessageListener> listeners = new ArrayList<ArduMessageListener>();

	private ServerSocket serverSocket = null;

	private String name;

	private InetAddress bindaddress;

	private int port;

	private boolean running = false;
	
	private volatile ConcurrentHashMap<String,ClientSocket> clientList;
	
  
	public TCPServer(String name, InetAddress bindaddress, int port) {
		this.name = name;
		this.bindaddress = bindaddress;
		this.port = port;
		
		clientList = new ConcurrentHashMap<String,ClientSocket>();
	}

	@Override
	public void run() {

		try {
			serverSocket = new ServerSocket(getPort(), 1024, getBindaddress());
			running = true;
		 
			
		 System.out.println("Servidor "+this.name+" Iniciado en "+this.getBindaddress().getHostAddress()+":"+this.getPort());
			while (running) {
				 
				Socket clientSocket = null;
				
				

				clientSocket = serverSocket.accept();

				String uniqueID = UUID.randomUUID().toString();
			 
 
				ClientSocket clientThread = new ClientSocket(this,uniqueID, clientSocket );
				addClient(uniqueID, clientThread);
				clientThread.start();
 

			}
 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			running = false;
			 
		}

	}
	public synchronized void removeClientById(String id)
	{
		if(this.clientList.containsKey(id))
			this.clientList.remove(id);
	}
	
	public synchronized void addClient(String id,ClientSocket sockt)
	{
		this.clientList.put(id, sockt);
	}
	
	
	 
	
	public synchronized ConcurrentHashMap<String,ClientSocket>  getClientList()
	{
		return clientList;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InetAddress getBindaddress() {
		return bindaddress;
	}

	public void setBindaddress(InetAddress bindaddress) {
		this.bindaddress = bindaddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
