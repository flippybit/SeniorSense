package application.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.ICsvBeanWriter;

import application.dao.FileBase.IntMinMax;
import application.implemet.MessageImpl;
import application.interfaces.Message;
import application.interfaces.Usuario;
import application.model.Administrador;
import application.model.DbConnection;
import application.model.Paciente;
import application.model.Sensor;
import application.model.Supervisor;
import application.model.UsuarioImpl;

public class MessageDao extends FileBase{
	
	public MessageDao() {
		this.CSV_FILENAME = "mesaggesFile.csv";

		this.headers = new String[] { "id", "from", "to", "timestamp", "content","header","status"};
	 
		this.processors = new CellProcessor[] {   // id (must be unique)
				new IntMinMax(0, 999999), // id
				new NotNull(), // from
				new NotNull(), // to		
				new LMinMax(0L, LMinMax.MAX_LONG), // timestamp
				 new Optional(), // content
				 new Optional(), // header
				 new IntMinMax(0, 15), // status 
	 
		};

		this. createFile();
	 
	}
	public Message getMessageByID(int id) {

		Message us = new MessageImpl();

		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			MessageImpl msj;
			while ((msj = beanReader.read(MessageImpl.class, header, this.getProcessors())) != null) {

				if (id == msj.getId()) {
					us = msj;
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

	public List<Message> getMessagesUser(int id_user, int limit) {
		List<Message> listUser = new ArrayList<Message>();
		 
		String[] header;
		try {
			header = this.getBeanReader().getHeader(true);

			MessageImpl msj;
			while ((msj = beanReader.read(MessageImpl.class, header, this.getProcessors())) != null) {

				if (id_user==Integer.valueOf(msj.getFrom()) || id_user == Integer.valueOf(msj.getTo())) {
					listUser.add(msj);
					 
				}

			}

			this.getBeanReader().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listUser;
	}
	private int getLastId() {
		String[] header;
		int id = 0;
		try {
			header = this.getBeanReader().getHeader(true);

			MessageImpl customer;
			while ((customer = beanReader.read(MessageImpl.class, header, this.getProcessors())) != null) {

				id = customer.getId();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;
	}
	public int addMessage(Message u)
	{
	 
		int insertedID= 0;

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
	
 
		return insertedID;
	}
}
