package application.interfaces;

public interface CommunicationMessage {

	public long getId();

	public void setId(long id);

	public String getSocketId();

	public void setSocketId(String socketId);

	public String getAction();

	public void setAction(String action);

	public long getTimestamp();

	public void setTimestamp(long timestamp);

	public String getMessage();

	public void setMessage(String message);
}
