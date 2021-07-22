package application.implemet;

import application.interfaces.ArduMessage;

public class ArduMessageDef implements ArduMessage {

	private int id;
  
	private String message;

	private long timestamp = 0;

	private String socketID;
 

	private int type;

	public ArduMessageDef() {
		this.timestamp = System.currentTimeMillis();
	}

	public ArduMessageDef(int id , String message,int type) {
		super();
		this.id = id; 
		this.message = message;
		this.type=5;
	}

	public ArduMessageDef(int id , String message, String socketid) {
		super();
		this.id = id; 
		this.message = message;
		this.socketID = socketid;
	}
	public String getSocketID() {
		return socketID;
	}

	public void setSocketID(String socketID) {
		this.socketID = socketID;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

 

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long l) {
		this.timestamp = l;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public int getId_from() {
		// TODO Auto-generated method stub
		return 0;
	}

}
