package application.implemet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.input.ReversedLinesFileReader;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import application.interfaces.Message;
import application.interfaces.MessageSystem;
import application.interfaces.Usuario;

// mensajes a CVS

public class MessengerFileSystem implements MessageSystem {

	File dbFile;
	String path="./messages.csv";
	long lastId= 0;
	 private static final String STRING_ARRAY_SAMPLE = "./messages.csv";

	public MessengerFileSystem()
	{
		dbFile = new File(path);
		if(!dbFile.exists())
			createNewFile();
		
		lastId = getLastId();
	}
	
	private void createNewFile()
	{
		try { 
				dbFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 
 
	@Override
	public void send(Message u) { 
	        		try {
	        			
	        			if(u.getId()==0)
	        			{
	        				lastId++;
	        				u.setId((int) lastId);
	        			}
	        				
	        			
	        			
	        			CSVWriter writer=   new CSVWriter(new FileWriter(path,true),',');
	        		     // feed in your array (or convert your data to an array)
	        		     String[] entries = new String[] {String.valueOf(u.getId()),u.getFrom(),u.getTo(),u.getContent(),u.getHeader(),String.valueOf(u.getTimestamp())};
	        		     writer.writeNext(entries);
	        		     writer.close();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
	             
	}
	
	private long getLastId()
	{
		Message m =getLastMessage();
		 if(m==null)
			 return 0;
		return m.getId();
	}
	
	public Message getLastMessage()
	{	Message m = null;
		 
		    try (
	                Reader reader = Files.newBufferedReader(Paths.get(path));
		    	 
	                CSVReader csvReader = new CSVReader(reader); 
	            ) {
	                // Reading Records One by One in a String array
		  
	                String[] nextRecord;
	                while ((nextRecord = csvReader.readNext()) != null) {
	                
	                     m =new MessageImpl(Long.parseLong(nextRecord[0]),nextRecord[1],nextRecord[2],nextRecord[3],nextRecord[4],Long.parseLong(nextRecord[5]));
	                }
	         
	            } catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		return m;
		
	}

	public List<Message> getAll()
	{
		 List<Message> messageList = new ArrayList<Message>();
	    try (
                Reader reader = Files.newBufferedReader(Paths.get(path));
	    	 
                CSVReader csvReader = new CSVReader(reader); 
            ) {
                // Reading Records One by One in a String array
	    	 
                String[] nextRecord;
                while ((nextRecord = csvReader.readNext()) != null) {
                
                    messageList.add(new MessageImpl(Long.parseLong(nextRecord[0]),nextRecord[1],nextRecord[2],nextRecord[3],nextRecord[4],Long.parseLong(nextRecord[5])));
                }
         
            } catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		return messageList;
	}

 
	@Override
	public Message getById(int id) {
		// TODO Auto-generated method stub
		Message messagesFrom = null;
		List<Message> allMessage= getAll();
		int i=0;
		Iterator<Message> alliterator = allMessage.iterator();
		boolean stop = false;
		while(alliterator.hasNext() && !stop) {
			Message m = alliterator.next();
			
			 if(m.getId()!=id)
				 continue;
			 messagesFrom=m;
			 stop=true;
			 
		}
		return messagesFrom;
	}

	@Override
	public List<Message> getAllFrom(Usuario u, int limit) {
		// TODO Auto-generated method stub
		
		
		return getAllFrom(u.getNombre(),limit);
	}

	@Override
	public List<Message> getAllFrom(String nombre, int limit) {
		// TODO Auto-generated method stub
		List<Message> messagesFrom = new ArrayList<Message>();
		List<Message> allMessage= getAll();
		int i=0;
		Iterator<Message> alliterator = allMessage.iterator();
		while(alliterator.hasNext() && i<limit) {
			Message m = alliterator.next();
			
			 if(!m.getFrom().equals(nombre))
				 continue;
			 
			 messagesFrom.add(m);
			 
			
			i++;
		}
		return messagesFrom;
	}

	@Override
	public List<Message> getAllTo(Usuario u, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Message> getAllTo(String nombre, int limit) {
		// TODO Auto-generated method stub
		List<Message> messagesTo = new ArrayList<Message>();
		List<Message> allMessage= getAll();
		int i=0;
		Iterator<Message> alliterator = allMessage.iterator();
		while(alliterator.hasNext() && i<limit) {
			Message m = alliterator.next();
			
			 if(!m.getTo().equals(nombre))
				 continue;
			 
			 messagesTo.add(m);
			 
			
			i++;
		}
		return messagesTo;
	}
 
	 
	
	

}
