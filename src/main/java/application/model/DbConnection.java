package application.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import application.dao.DAO;

public class DbConnection implements DAO {

	private static DbConnection self = null;
	private volatile Connection connection = null;

	/** Constructor de DbConnection */
	public DbConnection() {

	}

	/** Permite retornar la conexión */
	public synchronized Connection getConnection() {

		try {
			if (connection != null && !connection.isClosed())
				return connection;
			// obtenemos el driver de para mysql
			Class.forName(DRIVER);
			// obtenemos la conexión
			connection = DriverManager.getConnection(DB_URL, USER, null);

			if (connection != null) {
				System.out.println("Conexión a base de datos  OK\n");
			}
		} catch (SQLException e) {
			System.out.println(e);
		} catch (ClassNotFoundException e) {
			System.out.println(e);
		} catch (Exception e) {
			System.out.println(e);
		}
		return connection;
	}

	public synchronized Statement executeQuery(String sql) {
		Statement stm = null;
		Connection con = null;
		try {
			con = DbConnection.getInstance().getConnection();
			stm = con.createStatement();
			stm.execute(sql);
			stm.close();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stm;
	}

	public synchronized ResultSet getQuery(String sql) {
		ResultSet rs = null;
		Statement stm = null;
		Connection con = null;
		try {
			con = DbConnection.getInstance().getConnection();
			stm = con.createStatement();
			rs = stm.executeQuery(sql);
			 
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rs;
	}

	public synchronized static DbConnection getInstance() {
		if (self == null)
			self = new DbConnection();
		return self;
	}

	public synchronized void desconectar() {
		connection = null;
	}
}
