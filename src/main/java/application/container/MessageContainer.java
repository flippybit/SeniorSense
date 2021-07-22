package application.container;

import java.util.List;

import application.dao.MessageDao;
import application.implemet.MessengerFileSystem;
import application.interfaces.Message;
import application.interfaces.MessageSystem;


// contenedor de los chats
public class MessageContainer {

	private  MessageSystem ms;
	
	private MessageDao md;
	
	private static MessageContainer self;
	
	public MessageContainer()
	{
	//	ms = new MessengerFileSystem(); 
		md = new MessageDao();
		self=this;
	}
	
	public  MessageSystem getMessageSystem()
	{
		return ms;
	}
	
	public static MessageContainer getInstance()
	{
		if(self==null)
		{
			self = new MessageContainer();
		}
		return self;
	}
	
	public Message getMessage(int id)
	{
		return md.getMessageByID(id);
	}
	
	public List<Message> getUserMesages(int id,int limit)
	{
		return md.getMessagesUser(id,limit);
	}
	
	public int addMessage(Message u)
	{
		if( u.getTimestamp()<=0)
		{
			u.setTimestamp(System.currentTimeMillis());
		}
		return md.addMessage(u);
	}
}
