package application.interfaces;

public interface Message {
	
	public String getContent();
	
	public String getHeader();
	
	public String getFrom();
	
	public long getTimestamp();
	
	public void setTimestamp(long timestamp);
	
	public void setFrom(Usuario u);
	
	public void setFrom(String u);
	
	public void setTo(Usuario u);
	
	public void setTo(String u);

	public String getTo();
	
	public int getId();
	
	public void setId(int id);

	public void setStatus(int int1);

	public int getStatus();
	
	public void setContent(String content);
	 
}
