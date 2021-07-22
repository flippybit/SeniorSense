package application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import application.interfaces.Usuario;
import application.model.Administrador;
import application.model.DbConnection;
import application.model.Paciente;
import application.model.Supervisor;

public class CrudOperation implements DAO{
	
	

	public Object getBy(Class<?> c ,String table,String column,Object value)
	{
		String sql="";
		if(value instanceof String)
		{
			  sql = "SELECT * FROM  "+table+ " WHERE "+column+"=\"" + value + "\" LIMIT 1 ";
		}else if(value instanceof Integer)
		{
			  sql = "SELECT * FROM  "+table+ " WHERE \"+column+\"=" + value + " LIMIT 1 ";
		}else {
			return null;
		}
		ResultSet res = DbConnection.getInstance().getQuery(sql);
		
		try {
			if (res.next()) {
			 
			}
		} catch (SQLException e) {

		}
		
		return null;
		
	}
}
