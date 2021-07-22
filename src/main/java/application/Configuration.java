package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import application.interfaces.CustomActionListener;
import application.interfaces.Usuario;
import io.advantageous.config.Config;
import io.advantageous.config.ConfigLoader;

// clase para cargar los ficheros de configuracion
public class Configuration {

	private static Configuration self = null;

	private String fileName = "Configuration.json";

	private OutputStream output;

	private Config config;

	private Config defaultconfig;
	
	private  volatile ConcurrentHashMap<String,Object> customConfig;

	public Configuration() {
		customConfig =  new ConcurrentHashMap<String,Object>();
		if (self == null)
			self = this;
	
	}

	public void loadConfigFile() {

		boolean newFile = false;
		File df = new File("defaultConfig.json");
		File f = new File(fileName);

		try {

			if (df.exists() && !df.isDirectory()) {
				// do something
			} else {
				df.createNewFile();
			}

			if (f.exists() && !f.isDirectory()) {
				// do something
			} else {
				if (f.createNewFile()) {
					newFile = true;
				} else {

				}
			}

			if (newFile) {

				loadDefaultConfig();
			}

			URI udef = df.toURI();
			URI u = f.toURI();

			self.defaultconfig = ConfigLoader.load(udef.toString());
			self.config = ConfigLoader.load(u.toString());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadDefaultConfig() {

		try {
			FileChannel src = new FileInputStream("defaultConfig.json").getChannel();
			FileChannel dest = new FileOutputStream(fileName).getChannel();
			dest.transferFrom(src, 0, src.size());
			src.close();
			dest.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getValue(String value) {
		String cd = null;
		try {
			if(!self.config.hasPath(value))
				return null;

			  cd = self.config.get(value, String.class);
			if (cd.isEmpty()) {
				cd = self.defaultconfig.get(value, String.class);
			}

			if (cd.equals("")) {
				return null;
			}
		} catch (Exception e) {
		 System.err.println(e.getMessage());
		}
		return cd;
	}

	public static Object getValue(String value, Class<?> c) {
		if(self.customConfig.containsKey(value))
			
		{
			System.out.println(" GET THE VALUE "+c.getName());
			System.out.println((Usuario) self.customConfig.get(value));
			return self.customConfig.get(value);
		}
		
		 
		Object cd = self.config.get(value, c);
		if (cd == null) {
			cd = self.defaultconfig.get(value, c);
		}
		return self.config.get(value, c);
	}
	
	public static Object getCustomValue(String value, Class<?> c)
	{
		if(self.customConfig.containsKey(value))
					
			{
			Usuario u = (Usuario) self.customConfig.get(value);
				System.out.println(" GET THE VALUE "+ u.getNombre());
				System.out.println((Usuario) self.customConfig.get(value));
				return self.customConfig.get(value);
			}
		return null;
	}
	
	public static void setValue(String key,Object value)
	{ 
		if(self.customConfig==null)
		{
			self.customConfig =  new ConcurrentHashMap<String,Object>();
		}
	  
		if(value!=null)
		self.customConfig.put(key, value);
	}

}
