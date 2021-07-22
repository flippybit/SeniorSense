package application.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvBeanWriter;

import application.interfaces.Usuario;
import application.model.*;

public class UserDao extends FileBase implements DAO {

	public UserDao() {
		this.CSV_FILENAME = "userfile.csv";

		this.headers = new String[] { "id", "nombre", "apellidos", "calle", "codigoPostal", "ciudad", "username",
				"password", "hashedpassword", "type", "idParent" };

		this.processors = new CellProcessor[] { new IntMinMax(0, 999999), // id (must be unique)
				new NotNull(), // nombre
				new Optional(), // apellidos
				new NotNull(), // calle
				new IntMinMax(0, 99999), // codigoPostal
				new NotNull(), // ciudad
				new NotNull(), // username
				new NotNull(), // password
				new NotNull(), // hashedpassword
				new IntMinMax(0, 10), // type
				new IntMinMax(0, 999999) // idParent
		};

		this.classbeam = UsuarioImpl.class;
	}

	private int getLastId() {
		String[] header;
		int id = 0;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				id = customer.getId();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}

	public boolean clientExist(Usuario u) {
		boolean existe = false;

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if (u.getUserAccount().equals(customer.getUserAccount())) {
					existe = true;
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return existe;
	}

	public List<UsuarioImpl> getNestedUsersByUserId(int id_user) {
		List<UsuarioImpl> listUser = new ArrayList<UsuarioImpl>();

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if (customer.getIdParent() == id_user) {

				 
					listUser.add(customer);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listUser;
	}

	public Usuario getClientByName(String name) {

		Usuario us = null;

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if ((customer.getUserAccount().toLowerCase().equals(name.toLowerCase()) || customer.getNombre().toLowerCase().equals(name.toLowerCase()))) {
					us = customer;
					break;
				}

			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 

		return us;
	}

	public boolean deleteUserbyname(String name) {
		boolean delete = false;

		List<Usuario> userList = new ArrayList<Usuario>();
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if (!(customer.getUserAccount().equals(name))) {
					userList.add(customer);
				}

			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ICsvBeanWriter beanWriter = this.getFileWriter(false);
		for (Usuario ux : userList) {
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

	public boolean registerClient(Usuario u) {
		boolean register = false;

		if (!this.clientExist(u)) {

			ICsvBeanWriter beanWriter = this.getFileWriter();
			int newId = this.getLastId();
			newId++;
			u.setId(newId);
			try {

				beanWriter.write(u, this.getHeaders(), this.getProcessors());
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
		} else {

		}
		return register;
	}

	public int updateUser(Usuario u) {
		int register = 0;

		List<Usuario> userList = new ArrayList<Usuario>();
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if (u.getUserAccount().equals(customer.getUserAccount())) {
					customer = (UsuarioImpl) u;
					register=1;
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
		for (Usuario ux : userList) {
			
			System.out.println(" ON UPDATE "+ux.getId()+" "+ux.getNombre());
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

	public Usuario getClientByID(int id) {

		Usuario us = null;

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				if (id == customer.getId()) {
					us = customer;
					break;
				}

			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 

		return us;
	}

	public List<UsuarioImpl> geAll() {
		List<UsuarioImpl> listUser = new ArrayList<UsuarioImpl>();
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			UsuarioImpl customer;
			while ((customer = beanReader.read(UsuarioImpl.class, header, this.getProcessors())) != null) {

				listUser.add(customer);

			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listUser;
	}
}
