package application.implemet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;

import application.interfaces.CommunicationMessage;

public class CommunicationCenter {
	
	public static volatile long lastID=0;
	
	public static Gson gson;
	
	public CommunicationCenter()
	{
		  gson = new GsonBuilder()
		        .setLenient()
		        .create();
  
	}

	public static MyCommunicationMessage generateMessage()
	{
		
		return null;
		
	}
	
	public static Gson getGson()
	{
		return gson;
	}
	
	public static String serializeMessage(CommunicationMessage ms)
	{
		
		return gson.toJson(ms);
	}
	public static String serializeMessage(Object ms)
	{
		
		return gson.toJson(ms);
	}
	public static CommunicationMessage deserializeMessage(String msj)
	{
		
		try {
			return gson.fromJson(msj, MyCommunicationMessage.class);
		}catch(JsonIOException jsonex)
		{
			return null;
		}
		 
	}
	
	public static Object deserializeMessageTO(String msj,Class<?>  theclass)
	{
		
		return gson.fromJson(msj,theclass );
		
	}
	public synchronized static long getLastId()
	{
		return ++lastID;
	}
}
