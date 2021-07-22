package application.model;

public class SensorNotification {
 
	/**
	 * 
	 * @param id_notification
	 * @param type
	 * @param id_sensor
	 * @param timestamp
	 * @param status
	 * @param value
	 * @param id_user
	 */
	public SensorNotification(int id_notification, int type, int id_sensor, long timestamp, int status, String value,
			int id_user) {
		super();
		this.id_notification = id_notification;
		this.type = type;
		this.id_sensor = id_sensor;
		this.timestamp = timestamp;
		this.status = status;
		this.value = value;
		this.id_user = id_user;
	}
	
	public SensorNotification()
	{
		
	}

	private int id_notification;
	
	private int type;
	
	private int id_sensor;
	
	private long timestamp;
	
	private int status;
	
	private String value;
	
	private int id_user;
	
	public int getId_notification() {
		return id_notification;
	}

	public void setId_notification(int id_notification) {
		this.id_notification = id_notification;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId_sensor() {
		return id_sensor;
	}

	public void setId_sensor(int id_sensor) {
		this.id_sensor = id_sensor;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public void setValue(Double value) {
		this.value = String.valueOf(value);
	}
	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}
}
