package application.implemet;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.Iterators;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import application.dao.DAO;
import application.interfaces.NatourDao;
import application.model.DbConnection;

public class MyDao implements DAO {

	private String table;

	Map<String, Annotation> annotationList;

	Map<String, Field> fieldList = null;

	private boolean loaded = false;

	Class<?> c = null;

	private String primaryName = null;
	private String primaryNameField = null;
	private void loader() {
		annotationList = new LinkedHashMap<String, Annotation>();
		fieldList = new LinkedHashMap<String, Field>();

		c = getLastClassWithAnnotation(this.getClass(), 0);

		// Table
		if (c.isAnnotationPresent(NatourDao.Table.class)) {
			NatourDao.Table ntable = c.getAnnotation(NatourDao.Table.class);
			String value = ntable.value();
			if (!value.isEmpty()) {
				this.table = value;
			}
		}

		// Fields
		for (Field field : c.getDeclaredFields()) {

			if (field.isAnnotationPresent(NatourDao.Field.class)) {
				Annotation[] annons = field.getAnnotationsByType(NatourDao.Field.class);

				fieldList.put(field.getName(), field);
				if (annons.length > 0) {

					for (Annotation annon : annons) {
						annotationList.put(field.getName(), annon);

						NatourDao.Field myannon = (NatourDao.Field) annon;

						if (myannon.index().equals("PRIMARY")) {
							if (!myannon.value().isEmpty()) {
								primaryName = myannon.value();
							} else {
								primaryName = field.getName();
							}
							primaryNameField=field.getName();
						}
					}
				}

			}
		}

		loaded = true;
	}

	public void get(long id) {
		get(id, false);
	}

	public boolean delete() throws Exception
	{
		boolean deleted = false;
		
		if(this.primaryName.isEmpty())
			throw new Exception("Primary is empty");
		
		String query = buildDeleteQuery(getFieldValue(this.primaryNameField));

		PreparedStatement preparedStmt= DbConnection.getInstance().getConnection().prepareStatement(query);
		
		int row = preparedStmt.executeUpdate();
		
		if(row>0)
			deleted=true;
		
		return deleted; 
	}
	
	 
	
	private <T> String buildDeleteQuery(T id)
	{
		String query = "";

		query = " DELETE FROM " + this.table  + " WHERE " + getPrimaryName() + "=" + id;
 

		return query;
	}
	public <T> T get(long id, boolean returned) {
		if (!loaded)
			this.loader();

		String query = buildgetQuery(1, id);

		PreparedStatement preparedStmt;
		try {
			preparedStmt = DbConnection.getInstance().getConnection().prepareStatement(query);

			ResultSet r = preparedStmt.executeQuery();
			boolean ok = false;
			while (r.next() && !ok) {
				Iterator it = this.getParamList().entrySet().iterator();
				while (it.hasNext() && !ok) {
					Map.Entry mp = (Entry) it.next();
					String key = (String) mp.getKey();
					String c = (String) mp.getValue();

					String thevalue = key;
					if (!c.isEmpty())
						thevalue = c;

					Object o = r.getObject(thevalue);

					Field f = this.getFieldByName(key);

					try {
						if (!f.isAccessible())
							f.setAccessible(true);
						f.set(this, o);

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Object my = this;

		return null;

	}

	private String getFieldNameForTable(String key) {
		String name = null;
		NatourDao.Field fn = (NatourDao.Field) this.getAnnotationByName(key);
		Field myField = this.getFieldByName(key);
		if (fn.value().equals("")) {
			name = myField.getName();
		} else {
			name = fn.value();
		}
		return name;
	}

	private String getPrimaryName() {
		return this.primaryName;
	}

	private String buildgetQuery(int limit, long id) {
		String query = "";

		query = " SELECT " + getParamsByComma(true) + " FROM " + this.table + " WHERE " + getPrimaryName() + "=" + id;

		if (limit > 0)
			query += " LIMIT " + limit;

		return query;
	}

	public long save() throws Exception {

		if (!loaded)
			this.loader();
		int insertedID = 0;
		ResultSet rs = null;
		String query = buildInsertQuery();
		Iterator iter = this.getParamList().entrySet().iterator();

		boolean doStatement = true;
		try {
			PreparedStatement preparedStmt = DbConnection.getInstance().getConnection().prepareStatement(query,
					Statement.RETURN_GENERATED_KEYS);

			int i = 1;
			while (iter.hasNext() && doStatement) {
				Map.Entry mp = (Entry) iter.next();
				String key = (String) mp.getKey();
				String c = (String) mp.getValue();

				NatourDao.Field fn = (NatourDao.Field) this.getAnnotationByName(key);
				if (fn.index().equals("PRIMARY")
						&& Arrays.stream(fn.options()).anyMatch(NatourDao.AUTO_INCREMENT::equals)) {

					continue;
				}

				String valueKey = key;
				if (!c.isEmpty())
					valueKey = c;

				Object value = getFieldValue(key);
				if (this.checkDuplicate(fn, valueKey, value)) {
					doStatement = false;
					throw new Exception("The key " + key + " already exists with this value " + value);
				}
				if (this.isValueNullOrEmpty(value) && this.isFieldRequired(key)) {
					doStatement = false;
					throw new Exception("The key " + key + " is required but its empty");

				} else {
					preparedStmt.setObject(i, value);
				}

				i++;
			}
			if (doStatement) {
				int rowAffected = preparedStmt.executeUpdate();
				if (rowAffected == 1) {
					rs = preparedStmt.getGeneratedKeys();
					if (rs.next())
						insertedID = rs.getInt(1);
				}
			}

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (insertedID > 0) {
			setIdToprimeryField(insertedID);
		}

		return insertedID;

	}

	private boolean checkDuplicate(NatourDao.Field fn, String keyVal, Object value) {
		if (Arrays.stream(fn.options()).anyMatch(NatourDao.NO_DUPLICATES::equals)) {
			String sql = "SELECT " + keyVal + " FROM " + this.table + " WHERE " + keyVal + "=? LIMIT 1";

			try {
				PreparedStatement preparedStmt = DbConnection.getInstance().getConnection().prepareStatement(sql);

				preparedStmt.setObject(1, value);
				ResultSet r = preparedStmt.executeQuery();
				int rowCount = 0;
				while (r.next()) {
					rowCount++;
				}

				if (rowCount > 0)
					return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return false;
	}

	private void setIdToprimeryField(long id) {
		Iterator it = this.annotationList.entrySet().iterator();
		boolean seted = false;
		while (it.hasNext() && !seted) {
			Map.Entry mp = (Entry) it.next();
			String key = (String) mp.getKey();
			NatourDao.Field fn = (NatourDao.Field) mp.getValue();

			Field field = this.getFieldByName(key);
			if (fn.index().equals("PRIMARY")
					&& Arrays.stream(fn.options()).anyMatch(NatourDao.AUTO_INCREMENT::equals)) {
				if (!field.isAccessible())
					field.setAccessible(true);
				try {
					if (field.getType().getName().equals("int")) {
						field.set(this, ((Long) id).intValue());
					} else {
						field.set(this, id);
					}

					seted = true;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private boolean isValueNullOrEmpty(Object value) {
		// TODO Auto-generated method stub
		if (value == null)
			return true;

		if (value instanceof String) {
			String vale = (String) value;

			if (vale.isEmpty())
				return true;
		}

		return false;
	}

	private boolean isFieldRequired(String key) {

		boolean required = false;

		Field field = (Field) this.getFieldByName(key);

		NatourDao.Field anofield = (NatourDao.Field) this.getAnnotationByName(key);
		;

		if (field.getName().equals(key)) {
			required = anofield.required();
		}

		return required;
	}

	private Class<?> getLastClassWithAnnotation() {
		return getLastClassWithAnnotation(null, 0);
	}

	private Class<?> getLastClassWithAnnotation(Class<?> c, int i) {
		if (c == null)
			c = this.getClass();

		if (useParentAnnotations(c) && c.getSuperclass() != null)
			return getLastClassWithAnnotation(c.getSuperclass(), i++);

		return c;
	}

	private boolean useParentAnnotations(Class<?> c) {
		if (c.isAnnotationPresent(NatourDao.Class.class)) {

			NatourDao.Class tf = c.getAnnotation(NatourDao.Class.class);
			return tf.parent();
		}
		return false;
	}

	private String buildInsertQuery() {
		String query = "";

		Iterator iter = getParamList().entrySet().iterator();
		int total = Iterators.size(iter);
		iter = getParamList().entrySet().iterator();
		query = " insert into " + this.table + " (" + getParamsByComma() + ")";
		query += " VALUES( ";
		int i = 0;
		while (iter.hasNext()) {
			Map.Entry mp = (Entry) iter.next();
			String name = (String) mp.getKey();

			NatourDao.Field fn = (NatourDao.Field) this.getAnnotationByName(name);
			if (fn.index().equals("PRIMARY")
					&& Arrays.stream(fn.options()).anyMatch(NatourDao.AUTO_INCREMENT::equals)) {
				i++;
				continue;
			}
			query += "?";
			if (!(i == total - 1))
				query += ",";
			i++;
		}

		query += ")";

		return query;

	}

	private String getParamsByComma() {
		return this.getParamsByComma(false);
	}

	private String getParamsByComma(boolean usePrimary) {
		Map<String, String> mapc = getParamList();
		List<String> params = new ArrayList<String>();

		Iterator iter = mapc.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry mp = (Entry) iter.next();
			String key = (String) mp.getKey();
			String c = (String) mp.getValue();
			if (!usePrimary) {
				NatourDao.Field fn = (NatourDao.Field) this.getAnnotationByName(key);
				if (fn.index().equals("PRIMARY")
						&& Arrays.stream(fn.options()).anyMatch(NatourDao.AUTO_INCREMENT::equals)) {

					continue;
				}
			}
			String value = key;
			if (!c.isEmpty())
				value = c;
			params.add(value);
		}

		return params.stream().collect(Collectors.joining(","));

	}

	private Object getFieldValue(String name) {

		Object value = null;

		Field field = this.getFieldByName(name);
	 

		if (field.getName().equals(name)) {

			try {
				if (!field.isAccessible())
					field.setAccessible(true);
				value = field.get(this);

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return value;
	}

	private Map<String, String> getParamList() {

		Map<String, String> params = new HashMap<String, String>();

		Iterator iter = this.fieldList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry mp = (Entry) iter.next();
			String paramName = (String) mp.getKey();
			Field field = (Field) mp.getValue();

			String fieldValue = "";

			NatourDao.Field anofield = (NatourDao.Field) getAnnotationByName(paramName);

			fieldValue = (anofield.value()).isEmpty() ? field.getName() : anofield.value();
			// tipeValue = (anofield.type()).isEmpty() ? field.getType().getName() :
			// anofield.type();

			params.put(field.getName(), fieldValue);
		}

		return params;
	}

	private Field getFieldByName(String name) {
		if (this.fieldList.containsKey(name))
			return this.fieldList.get(name);
		return null;
	}

	private Annotation getAnnotationByName(String name) {
		if (this.annotationList.containsKey(name))
			return this.annotationList.get(name);
		return null;
	}
}
