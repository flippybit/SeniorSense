package application.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.dao.SensorDao;
import application.dao.UserDao;
import application.interfaces.Usuario;
import application.model.Sensor;
import application.model.SensorData;
import application.model.SensorNotification;
//contenedor con sensores
public class SensorContainer {

	private static SensorContainer self;
	
	private volatile SensorDao sensorDao;
	
	public  volatile List<Sensor> sensores;
	
	public  volatile List<SensorData> sensoresData;
	
	public  volatile List<SensorNotification> sensoresNot;
	
	public volatile Map<Integer,SensorNotification> lastNoti;
	
	public SensorContainer()
	{
		self=this;
		sensorDao= new SensorDao();
		sensores = new ArrayList<Sensor>();
		
		sensoresData = new ArrayList<SensorData>();
		sensoresNot = new ArrayList<SensorNotification>();
		
		lastNoti = new HashMap<Integer, SensorNotification>();
	}
	
	public synchronized Sensor getSensorByID(int id)
	{
		Sensor s = null;
	 
		 for(Sensor ns :  sensores)
		 {
			 if(ns.getId_sensor()==id)
				 s =ns;
		 }
		return s;
	}
	
	public SensorNotification getLastSensorNotificationBySensorId(int id)
	{
		if(!lastNoti.containsKey(id))
		{
			return null;
		}
		return lastNoti.get(id);
		 
	}
	public List<Sensor> getAllSensorByUserId(int id)
	{
		return getAllSensorByUserId(id,0);
	}
	
	public List<Sensor> getAllSensorByUserId(int id,int id_parent)
	{
		return sensorDao.getAllSensorBy(id,id_parent);
	}
	
	public Sensor getSensorByIDFullLoaded(int id)
	{
		Sensor s = sensorDao.getById(id);
		if(s!= null)
		{
			 
			
		}
		return null;
	}
	
	public List<SensorData> getSensorDatas(int id)
	{
		return sensorDao.getSensorDatas(id, 100);
	}
	
	public synchronized List<SensorNotification> getSensorNotifications(int id)
	{
		return  sensorDao.getSensorNotifications(id, 100);
	}
	public synchronized boolean addSensorData(SensorData sd)
	{
		if(sd.getId_user()>0 && sd.getId_sensor()>0)
		{
			return sensorDao.saveSensorData(sd);
		}
		return false;
	}
	
	public   boolean updateSensor(Sensor s)
	{
		System.out.println(" UPDATE SENSOR ------------------------- >");
		return sensorDao.updateSensor(s);
		
	}
	
	public   int addSensor(Sensor s)
	{
		System.out.println(" ADD SENSOR ------------------------- >");
		return sensorDao.registerSensor(s);
		
	}
	public synchronized static SensorContainer getInstance()
	{
		if(self==null)
		{
			self = new SensorContainer();
		}
		return self;
	}

	public int addNotify(SensorNotification sn) {
		// TODO Auto-generated method stub
		int n= sensorDao.addNotify(sn);
		
		this.lastNoti.put(sn.getId_sensor(), sn);
		return n;
	}
}
