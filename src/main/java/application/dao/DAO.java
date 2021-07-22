package application.dao;

import application.Configuration;

public interface DAO {
	public static final String DB_URL = Configuration.getValue("database.DB_URL");
	public static final String DRIVER =   Configuration.getValue("database.DRIVER");
	public static final String USER =   Configuration.getValue("database.USER");
	public static final String PASS =   Configuration.getValue("database.PASS");
} 