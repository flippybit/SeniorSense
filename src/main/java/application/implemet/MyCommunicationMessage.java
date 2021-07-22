package application.implemet;

import application.interfaces.CommunicationMessage;

public class MyCommunicationMessage implements CommunicationMessage {

	public long id=0;

	public String socketId;

	public String action;

	public long timestamp;

	public String Message;
	
	 
	public Object objectmessage;
	
	public MyCommunicationMessage()
	{
	 if(id==0)
	 {
		 this.id=CommunicationCenter.getLastId();
	 }
	 
	 if(timestamp==0)
	 {
		 timestamp=System.currentTimeMillis();
	 }
		
	}

	public MyCommunicationMessage(long id, String socketId, String action, long timestamp, String message,Object objectmessage) {
		this();
		this.id = id;
		this.socketId = socketId;
		this.action = action;
		this.timestamp = timestamp;
		Message = message;
		this.objectmessage=objectmessage;
	}
 
	public MyCommunicationMessage( String socketId, String action, String message,Object objectmessage) {
		this(0,socketId,action,System.currentTimeMillis(),message,objectmessage); 
	 
	}
	 
	public MyCommunicationMessage( String socketId, String message,Object objectmessage) {
		this(socketId,null,message,objectmessage); 
	  
	}
	
	
	public MyCommunicationMessage( String message,Object objectmessage,String action) {
		this(null,action,message,objectmessage);  
	 
	}
	public MyCommunicationMessage( String message,Object objectmessage) {
		this(null,message,objectmessage);  
		 
	}
	public MyCommunicationMessage(String message) {
		this(message,null);   
	}
 
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSocketId() {
		return socketId;
	}

	public void setSocketId(String socketId) {
		this.socketId = socketId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}
	public Object getObjectmessage() {
		return objectmessage;
	}

	public void setObjectmessage(Object objectmessage) {
		this.objectmessage = objectmessage;
	}
	
	@Override
	public String toString()
	{
		return "[ id: "+getId()+" socketID: "+getSocketId()+" action: "+getAction()+" timestamp: "+getTimestamp()+"  "
				+ " message: "+getMessage()+" objectmessage: "+getObjectmessage()+"]";
		
	}

}
