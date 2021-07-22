package application.dao;
 

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import application.dao.FileBase.IntMinMax;
import application.model.UsuarioImpl;

public class SensorNotificationsDao  extends FileBase{

	public SensorNotificationsDao() {
		this.CSV_FILENAME = "sensorNotisFile.csv";

		this.headers = new String[] { "id_notification", "id_sensor", "id_user", "timestamp", "value","status","type"};
	 
		this.processors = new CellProcessor[] {   // id (must be unique)
				new IntMinMax(0, 999999), // id_notification
				new IntMinMax(0, 999999), // id_sensor
				new IntMinMax(0, 999999), // id_user		
				new LMinMax(0L, LMinMax.MAX_LONG), // timestamp
				 new Optional(new ParseDouble()), // value
				 new IntMinMax(0, 15), // status
				new IntMinMax(0, 15) // type
	 
		};

		this. createFile();
	 
	}
} 