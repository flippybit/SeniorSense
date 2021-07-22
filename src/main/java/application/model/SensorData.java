package application.model;

public class SensorData {
	   
	private int id_sensor;
	
	private int id_user;
	
	private float value;
	
	private long timestamp;
	
	private int type;
	
	public SensorData()
	{
		
	}
	
	/**
	 * 
	 * @param id_sensor
	 * @param id_user
	 * @param value
	 * @param timestamp
	 * @param type
	 */
	public SensorData(int id_sensor, int id_user, float value, long timestamp, int type) {
		super();
		this.id_sensor = id_sensor;
		this.id_user = id_user;
		this.value = value;
		this.timestamp = timestamp;
		this.type = type;
	}
	
	public int getId_sensor() {
		return id_sensor;
	}

	public void setId_sensor(int id_sensor) {
		this.id_sensor = id_sensor;
	}

	public int getId_user() {
		return id_user;
	}

	public void setId_user(int id_user) {
		this.id_user = id_user;
	}

	public float getValue() {
		return value;
	}
 
	public void setValue(float value) {
		this.value = value;
	}
	public void setValue(String value) {
		this.value = Float.parseFloat(value);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
