package application.implemet;

import java.util.Date;

import com.opencsv.bean.CsvBindByName;

import application.interfaces.Message;
import application.interfaces.Usuario;
import application.model.UsuarioImpl;

public class MessageImpl implements Message{
	
	@CsvBindByName
	private int id;
	
	@CsvBindByName
	private String from;
	
	@CsvBindByName
	private String to;
	 
	 @CsvBindByName
	private String content;
	 
	 @CsvBindByName
	private String header;
	 
	private int status;
	 


	@CsvBindByName
	private long timestamp;

	public MessageImpl()
	{
		
	}
	public MessageImpl(long l,String from,String to,String content,String header,long timestamp)
	{
		this.id=(int) l;
		this.from=from;
		this.to=to;
		this.content=content;
		this.header=header;
		this.timestamp=timestamp;
		
	}
	
	public MessageImpl(String from,String to,String content,String header,long timestamp)
	{
	
		this(0,from,to,content,header,timestamp);
	}
	
	 
	public MessageImpl(String from,String to,String content,String header)
	{
		this(0,from,to,content,header,System.currentTimeMillis()); 
	}
	public MessageImpl(Usuario from,Usuario to,String content,String header)
	{
		this(from.getNombre(),to.getNombre(),content,header);
	}
	@Override
	 public String getTo() {
			return to;
		}

		public void setContent(String content) {
			this.content = content;
		}
		@Override
		public String getContent() {
			// TODO Auto-generated method stub
			return content;
		}
		public void setHeader(String header) {
			this.header = header;
		}
	
	

	@Override
	public String getHeader() {
		// TODO Auto-generated method stub
		return header;
	}

	@Override
	public long getTimestamp() {
		// TODO Auto-generated method stub
		return timestamp;
	}

	@Override
	public void setTimestamp(long timestamp) {
		// TODO Auto-generated method stub
		this.timestamp=timestamp;
	}

	@Override
	public void setFrom(Usuario u) {
		// TODO Auto-generated method stub
		this.from=u.getNombre();
	}

	@Override
	public void setFrom(String u) {
		// TODO Auto-generated method stub
		this.from=u;
	}

	@Override
	public void setTo(Usuario u) {
		// TODO Auto-generated method stub
		this.to=u.getNombre();
	}

	@Override
	public void setTo(String u) {
		// TODO Auto-generated method stub
		this.to=u;
	}

	@Override
	public String getFrom() {
		// TODO Auto-generated method stub
		return from;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString()
	{
		return "[ Mensaje:  | Id: "+getId()+" |From: "+getFrom()+" |To: "+getTo()+" |Contenido: "+getContent()+" |Cabecera "+getHeader()+" |Hora: "+new Date(getTimestamp());
	}

	    
	 public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
