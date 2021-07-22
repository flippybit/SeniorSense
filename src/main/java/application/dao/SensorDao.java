package application.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvBeanWriter;

import application.container.UserDBContainer;
import application.dao.FileBase.IntMinMax;
import application.exception.UserException;
import application.interfaces.Usuario;
import application.model.Administrador;
import application.model.DbConnection;
import application.model.Paciente;
import application.model.Sensor;
import application.model.SensorData;
import application.model.SensorNotification;
import application.model.Supervisor;
import application.model.UsuarioImpl;

public class SensorDao extends FileBase implements DAO {

	public SensorDao() {
		this.CSV_FILENAME = "sensorFile.csv";

		this.headers = new String[] { "id_sensor", "type", "id_user", "lastValue", "last_message" };

		this.processors = new CellProcessor[] { // id (must be unique)
				new IntMinMax(0, 9999), // id_sensor
				new IntMinMax(0, 20), // type
				new IntMinMax(0, 9999), // id_userLMinMax
				new Optional(), // lastValue
				new LMinMax(0L, LMinMax.MAX_LONG), // last_message

		};

		this.classbeam = UsuarioImpl.class;
	}

	public Sensor getById(int id) {
		Sensor us = null;

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			Sensor sensor;
			while ((sensor = beanReader.read(Sensor.class, header, this.getProcessors())) != null) {

				if (id == sensor.getId_sensor()) {
					us = sensor;
					break;
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			beanReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return us;
	}

	public Usuario getUserBySensorID(int id) {
		Usuario u = null;
		Sensor s = this.getById(id);
		if (s != null) {
			try {
				u = UserDBContainer.getUser(id);
			} catch (UserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return u;
	}

	public boolean updateSensor(Sensor s) {
		boolean register = false;

		System.out.println("UPDATE SENSOR "+s.getId_sensor()+" "+s.getLastValue());
		List<Sensor> userList = new ArrayList<Sensor>();
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			Sensor customer;
			while ((customer = beanReader.read(Sensor.class, header, this.getProcessors())) != null) {

				if (s.getId_sensor()==customer.getId_sensor()) {
					customer =  s;
					register=true;
				}
				userList.add(customer);
			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ICsvBeanWriter beanWriter = this.getFileWriter(false);
		try {
			beanWriter.writeHeader(this.getHeaders());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (Sensor ux : userList) { 
			System.out.println(" ON UPDATE SSSSSS");
			try {
				beanWriter.write(ux, this.getHeaders(), this.getProcessors());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (beanWriter != null) {
			try {
				beanWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return register;

	}

	public boolean deletebyID(int id) {
		boolean delete = false;
		List<Sensor> userList = new ArrayList<Sensor>();
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			Sensor sensor;
			while ((sensor = beanReader.read(Sensor.class, header, this.getProcessors())) != null) {

				if (id != sensor.getId_sensor()) {
					delete = true;
					userList.add(sensor);
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ICsvBeanWriter beanWriter = this.getFileWriter(false);
		for (Sensor ux : userList) {
			try {
				beanWriter.write(ux, this.getHeaders(), this.getProcessors());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (beanWriter != null) {
			try {
				beanWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return delete;
	}

	public List<Sensor> getAllSensorBy(int id_user, int id_parent) {
		List<Sensor> listSensor = new ArrayList<Sensor>();

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			Sensor sensor;
			while ((sensor = beanReader.read(Sensor.class, header, this.getProcessors())) != null) {

				if (id_user == 0) {
					listSensor.add(sensor);
				} else if (id_user > 0 && id_parent == 0) {
					if (sensor.getId_user() == id_user)
						listSensor.add(sensor);
				} else if (id_parent > 0) {
					Usuario listaUser = null;
					try {
						listaUser = UserDBContainer.getUser(id_parent, true, true);
					} catch (UserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (Usuario userofparent : listaUser.getNestedList()) {
						int id_userinparent = userofparent.getId();
						if (sensor.getId_user() == id_userinparent) {
							listSensor.add(sensor);
							break;
						}

					}
				}

			}
 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listSensor;
	}

	public List<SensorData> getSensorDatas(int id_sensor, int limit) {

		SensorDatasDao sdd = new SensorDatasDao();
		List<SensorData> listSensor = new ArrayList<SensorData>();
		String[] header;
		try {
			header = sdd.getBeanReader().getHeader(true);

			SensorData sensor;
			while ((sensor = sdd.beanReader.read(SensorData.class, header, sdd.getProcessors())) != null) {

				if (id_sensor == sensor.getId_sensor()) {
					listSensor.add(sensor);
				
				}

			}

			sdd.beanReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listSensor;
	}

	public synchronized List<SensorNotification> getSensorNotifications(int id_sensor, int limit) {
		SensorNotificationsDao sdde = new SensorNotificationsDao();

		List<SensorNotification> listSensor = new ArrayList<SensorNotification>();

		String[] header;
		try {
			header = sdde.getBeanReader().getHeader(true);

			SensorNotification sensor;
			while ((sensor = sdde.beanReader.read(SensorNotification.class, header, sdde.getProcessors())) != null) {

				if (id_sensor == sensor.getId_sensor()) {
					listSensor.add(sensor);
				 
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			sdde.beanReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listSensor;
	}

	public boolean saveSensorData(SensorData sd) {
		boolean register = false;
		SensorDatasDao sdd = new SensorDatasDao();
		ICsvBeanWriter beanWriter = sdd.getFileWriter();
		try {

			sdd.beanWriter.write(sd, sdd.getHeaders(), sdd.getProcessors());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (sdd.beanWriter != null) {
			try {
				sdd.beanWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return register;
	}

	public static volatile SensorNotification lastnoti = null;

	public SensorNotification getLastSensorNotification(int id) {
		SensorNotification us = null;
		SensorNotificationsDao sdd = new SensorNotificationsDao();

		if (lastnoti != null)
			return lastnoti;

		String[] header;
		try {
			header = sdd.getBeanReader().getHeader(true);

			SensorNotification customer;
			while ((customer = sdd.getBeanReader().read(SensorNotification.class, header,
					sdd.getProcessors())) != null) {

				if (customer.getId_sensor() == id) {
					us = customer;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sdd.beanReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastnoti = us;

		return us;
	}

	private int getLastIdNoti() {
		SensorNotificationsDao sdd = new SensorNotificationsDao();
		String[] header;
		int id = 0;
		try {
			header = sdd.getBeanReader().getHeader(true);

			SensorNotification customer;
			while ((customer = sdd.beanReader.read(SensorNotification.class, header, sdd.getProcessors())) != null) {

				id = customer.getId_notification();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public int addNotify(SensorNotification sn) {
		// TODO Auto-generated method stub
		int insertedID = 0;
		boolean register = false;
		SensorNotificationsDao sdd = new SensorNotificationsDao();
		ICsvBeanWriter beanWriter = sdd.getFileWriter();

		int lastId = this.getLastIdNoti();
		lastId++;
		sn.setId_notification(lastId);
		try {

			beanWriter.write(sn, sdd.getHeaders(), sdd.getProcessors());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (beanWriter != null) {
			try {
				beanWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return lastId;

	}

	private int getLastId() {

		String[] header;
		int id = 0;
		try {
			header = this.getBeanReader().getHeader(true);

			Sensor customer;
			while ((customer = beanReader.read(Sensor.class, header, this.getProcessors())) != null) {

				id = customer.getId_sensor();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public int registerSensor(Sensor sn) {
		// TODO Auto-generated method stub
		int insertedID = 0;
		boolean register = false;

		ICsvBeanWriter beanWriter = this.getFileWriter();

		int lastId = this.getLastId();
		lastId++;
		sn.setId_sensor(lastId);
		try {

			beanWriter.write(sn, this.getHeaders(), this.getProcessors());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			beanWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return insertedID;
	}
}
