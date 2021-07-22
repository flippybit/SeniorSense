package application.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.supercsv.cellprocessor.constraint.LMinMax;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import application.model.UsuarioImpl;

public class FileBase {
	ICsvBeanReader beanReader = null;
	ICsvBeanWriter beanWriter = null;

	public String CSV_FILENAME = "";

	public String[] headers;

	
	
	public String[] getHeaders() {
		return headers;
	}

	public void setHeaders(String[] headers) {
		this.headers = headers;
	}

	public CellProcessor[] getProcessors() {
		return processors;
	}

	public void setProcessors(CellProcessor[] processors) {
		this.processors = processors;
	}

	public CellProcessor[] processors;

	public Object classbeam;

	public ICsvBeanReader getBeanReader() {
		beanReader = new CsvBeanReader(getFileReader(), CsvPreference.STANDARD_PREFERENCE);

		return beanReader;
	}

	public ICsvBeanWriter getFileWriter() {
		return this.getFileWriter(true);
	}
	public ICsvBeanWriter getFileWriter(boolean append) {
		
		
		
		 createFile();
		try {
	  
			
			beanWriter = new CsvBeanWriter(new FileWriter("dataFiles/" + CSV_FILENAME, append),
					CsvPreference.STANDARD_PREFERENCE);
		 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return beanWriter;

	}

	public Object getClassbeam() {
		return classbeam;
	}

	public void setClassbeam(Object classbeam) {
		this.classbeam = classbeam;
	}

	public FileReader getFileReader() {
		 createFile();

		FileReader fx = null;
		try {
			

			fx = new FileReader("dataFiles/" + CSV_FILENAME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fx;
	}
	
	public void createFile()
	{
		File yourFile = new File("dataFiles/" + CSV_FILENAME);
		if (!yourFile.exists()) {
			try {
				yourFile.createNewFile();
				
				 
				 ICsvBeanWriter beanWriter = this.getFileWriter();
					beanWriter.writeHeader(this.getHeaders());
					 if( beanWriter != null ) {
		                 try {
							beanWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		         }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // creating it
		}
	}

	class IntMinMax extends LMinMax {

		public IntMinMax(int min, int max) {
			super(min, max);
		}

		@Override
		public Object execute(Object value, CsvContext context) {
			Long result = (Long) super.execute(value, context);
			return result.intValue();
		}

	}
}
