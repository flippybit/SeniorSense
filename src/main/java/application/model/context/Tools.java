package application.model.context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

	public static String formatTimestamt(long timestamp)
	
	{
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date today = new Date(timestamp);
		
		return ft.format(today);
	}
}
