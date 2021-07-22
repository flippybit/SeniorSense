package application.dao;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import application.dao.FileBase.IntMinMax;
import application.model.UsuarioImpl;

public class SensorDatasDao  extends FileBase{

	public SensorDatasDao() {
		this.CSV_FILENAME = "sensorDatasFile.csv";

		this.headers = new String[] { "id_sensor", "id_user", "value", "timestamp", "type"};
	 
		this.processors = new CellProcessor[] {  // id (must be unique)
				new IntMinMax(0, 999999), // id_sensor
				new IntMinMax(0, 999999), // id_user
				  new Optional(), // value
				new LMinMax(0L, LMinMax.MAX_LONG), // timestamp
				new IntMinMax(0, 15) // type
	 
		};

		this. createFile();
	 
	}
}
