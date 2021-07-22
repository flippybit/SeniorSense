package application.model;

 

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ArduPacket {

	private final StringProperty arduinoID;
	private final StringProperty sensorA;
	private final StringProperty sensorB;
	private final IntegerProperty sensorC;
	private final StringProperty timeStamp;
	private final StringProperty sequenceNum;
	private final StringProperty battLevel;
	private final StringProperty conexionStatus;

	/**
	 * Constructor por defecto.
	 */
	public ArduPacket() {
		this(null, null, null);
	}

	/**
	 * Constructor con datos algunos datos iniciales
	 *
	 * @param arduinoID
	 * @param timeStamp
	 * @param sequenceNum
	 */
	public ArduPacket(String arduinoID, String timeStamp, String sequenceNum) {

		this.arduinoID = new SimpleStringProperty(arduinoID);
		this.timeStamp = new SimpleStringProperty(timeStamp);
		this.sequenceNum = new SimpleStringProperty(sequenceNum);
		this.sensorA = new SimpleStringProperty("OK");
		this.sensorB = new SimpleStringProperty("Warning");
		this.sensorC = new SimpleIntegerProperty(25);
		this.battLevel = new SimpleStringProperty("High");
		this.conexionStatus = new SimpleStringProperty("Not Connected");

	}

	public String getSensorA() {
		return sensorA.get();
	}

	public String getSensorB() {
		return sensorB.get();
	}

	public void setSensorB(String sensorB) {
		this.sensorB.set(sensorB);
	}

	public int getSensorC() {
		return sensorC.get();
	}

	public void setSensorC(int sensorC) {
		this.sensorC.set(sensorC);
	}

	public void setSensorA(String sensorA) {
		this.sensorA.set(sensorA);
	}

	public String getUserId() {
		return arduinoID.get();
	}

	public void setUserId(String _userID) {
		this.arduinoID.set(_userID);
	}

	public StringProperty sequenceNumProperty() {
		return sequenceNum;
	}

	public StringProperty timeStampProperty() {
		return timeStamp;
	}

	public String getSequenceNum() {
		return sequenceNum.get();
	}

	public String getTimeStamp() {
		return timeStamp.get();
	}

	public void setTimeStamp(String _timeStamp) {
		this.timeStamp.set(_timeStamp);
	}

	public void setSequenceNum(String _sequenceNum) {
		this.sequenceNum.set(_sequenceNum);
	}

	public StringProperty battLevelProperty() {
		return battLevel;
	}

	public String getBattLevel() {
		return battLevel.get();
	}

	public void setBattLevel(String _battLevel) {
		this.battLevel.set(_battLevel);

	}

	public StringProperty conexionStatusProperty() {
		return conexionStatus;
	}

	public String getConectionStatus(){
		return conexionStatus.get();
	}
	public void setConectionStatus(String _conectionStatus){
		this.conexionStatus.set(_conectionStatus);
	}

}
