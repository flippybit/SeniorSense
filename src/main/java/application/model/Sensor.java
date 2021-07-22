package application.model;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import application.Configuration;
import application.dao.DAO;
import application.implemet.ArduMessageDef;

public class Sensor {
 
	private int id_sensor;
	
	private int type;
	
	private int id_user=0;
	
	private long last_message;
	
	private String lastValue;
	


	private List<SensorNotification> sensorNotification;
	
	private List<SensorData> sensorData;
	
	/**
	 * 
	 * @param id_sensor
	 * @param type
	 * @param id_user
	 * @param last_message
	 */
	public Sensor(int id_sensor, int type, int id_user, long last_message) {
		super();
		this.id_sensor = id_sensor;
		this.type = type;
		this.id_user = id_user;
		this.last_message = last_message;
		
		sensorNotification= new ArrayList<SensorNotification>();
		sensorData= new ArrayList<SensorData>();
	}
	
	public Sensor()
	{
		
	}
	
	public String getLastValue() {
		return lastValue;
	}

	public void setLastValue(String lastValue) {
		this.lastValue = lastValue;
	}
	
	public int getId_sensor() {
		return id_sensor;
	}

	public void setId_sensor(int id_sensor) {
		this.id_sensor = id_sensor;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}

	public long getLast_message() {
		return last_message;
	}

	public void setLast_message(long last_message) {
		this.last_message = last_message;
	}


	public void doEmulation()
	{
		(new Thread() {
			public void run()
			{
				try {
					 emulate();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void emulate() throws NumberFormatException, UnknownHostException, IOException
	{
		Socket socketArduino = new Socket(Configuration.getValue("client.bind"),Integer.valueOf(Configuration.getValue("client.port")));
		 Sensor s = this;
		Timer timer = new Timer(); 
		int randomNum = ThreadLocalRandom.current().nextInt(1000, 4000 + 1);
		timer.schedule( new TimerTask() 
		{ 
		    public void run() { 
		     
		    	try {
		    		ArduMessageDef amdf = new ArduMessageDef(3,"5.0",2);
		    		double random = Math.random() * 200 + 1;
		    		String message = "{\"id\":0,\"action\":\"ardu\",\"timestamp\":"+System.currentTimeMillis()+",\"Message\":\"{\\\"id\\\":"+s.getId_sensor()+",\\\"message\\\":\\\" "+String.valueOf(random)+" \\\",\\\"timestamp\\\":0,\\\"type\\\":"+s.getType()+"}\"}";
					  socketArduino.getOutputStream().write(message.getBytes());
					  socketArduino.getOutputStream().flush();
						  //	 socketArduino.getOutputStream().write(CommunicationCenter.serializeMessage(new MyCommunicationMessage(CommunicationCenter.serializeMessage(amdf),amdf,"ardu")).getBytes());
					//socketArduino.getOutputStream().write("{\"id\":0,\"action\":\"ardu\",\"timestamp\":1557525117604,\"Message\":\"{\"id\":3,\"message\":\"5.0\",\"timestamp\":0,\"type\":5}\",\"objectmessage\":{\"id\":3,\"message\":\"5.0\",\"timestamp\":0,\"type\":5}}".getBytes());
		    	} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(java.lang.Thread.activeCount());
		    } 
		}, 0, (randomNum*1));
	}
}
