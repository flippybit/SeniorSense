package application.controller.classes;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import application.implemet.ArduMessageDef;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import javafx.application.Platform;
import javafx.scene.control.Label;


// Esta clase registra el contexto de recogida de mensajes del arduino
public class AutoUpdate implements CustomActionListener {

	protected volatile HashMap<Integer, Label> updateLabels;

	public AutoUpdate() {
		updateLabels = new HashMap<Integer, Label>();
	}

	@Override
	public synchronized boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub


		switch (event.name) {
		case "doneUpdateValues":
			ArduMessageDef amdf = (ArduMessageDef) event.object;
			if (!updateLabels.isEmpty()) {
				
				// Define formato mensaje de salida del arduino 
				DecimalFormat df = new DecimalFormat("#.####");
				df.setRoundingMode(RoundingMode.CEILING);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						Iterator it = updateLabels.entrySet().iterator();
						
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry) it.next();
							if (amdf.getType() == (int) pair.getKey()) {
								 try {
							            Double num = Double.parseDouble(amdf.getMessage());
							            
							        	Label lab = (Label) pair.getValue();
										lab.setText(df.format(num));
							        } catch (NumberFormatException e) {
							          continue;
							        }
							
							}

						}
					}
				});
			} else {
				 
			}

			break;

		}
		return true;
	}

}
