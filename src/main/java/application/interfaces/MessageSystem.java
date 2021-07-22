package application.interfaces;

import java.util.List;

import application.implemet.MessageImpl;

@SuppressWarnings("unused")
public interface MessageSystem {
	
	public void send(Message m);
	
	public List<Message> getAllFrom(Usuario u,int limit);
	
	public List<Message> getAllFrom(String nombre,int limit);
	
	public Message getById(int id);
	
	public List<Message> getAllTo(Usuario u,int limit);
	
	public List<Message> getAllTo(String nombre,int limit);
 
	
	public List<Message> getAll();

}
