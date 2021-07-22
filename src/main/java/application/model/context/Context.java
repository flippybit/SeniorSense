package application.model.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import application.implemet.ArduMessageDef;
import application.interfaces.ArduMessageListener;
import application.interfaces.CommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.model.ClientSocket;

public class Context {

	private volatile static Context self = null;

	public static int DEBUG_MODE = 2;

	private volatile ConcurrentHashMap<String, ArrayList<CustomActionListener>> listeners;

	public Context() {

		listeners = new ConcurrentHashMap<String, ArrayList<CustomActionListener>>();
		if (self == null)
			self = this;
	}

	public synchronized static Context getInstance() {
		if (self == null)
			self = new Context();
		return self;
	}

	public synchronized boolean registerListener(String actionName, CustomActionListener listener) {
		System.out.println(" EVENTO REGISTRADO: " + actionName + "  Clase: " + listener.getClass().getName());

		if (!listeners.containsKey(actionName)) {
			listeners.put(actionName, new ArrayList<CustomActionListener>());
		}

		if (listeners.get(actionName).add(listener)) {
			if (DEBUG_MODE == 2) {
				System.out.println(" EVENTO REGISTRADO: " + actionName + "  Clase: " + listener.getClass().getName());
			}
			return true;
		}

		return false;
	}

	public synchronized boolean removeListenersInCLass(Class<?> c) {
		return removeListenersInCLass(c, null);
	}

	public synchronized boolean removeListenersInCLass(Class<?> c, String action) {

		Iterator it = listeners.entrySet().iterator();
		Map<String, List<Integer>> listToRemove = new HashMap<String, List<Integer>>();
		while (it.hasNext()) {
			Map.Entry mp = (Entry) it.next();
			String key = (String) mp.getKey();
			List<CustomActionListener> listenerInAction = (List<CustomActionListener>) mp.getValue();

			int i = 0;
			for (CustomActionListener list : listenerInAction) {
				if (list.getClass().getName().equals(c.getName())) {

					if (!listToRemove.containsKey(key)) {
						listToRemove.put(key, new ArrayList<Integer>());
					}
					if (action == null || key.equals(action))
						listToRemove.get(key).add(i);

				}

				i++;
			}
		}

		Iterator full = listToRemove.entrySet().iterator();
		while (full.hasNext()) {

			Map.Entry mp = (Entry) full.next();
			String key = (String) mp.getKey();
			List<Integer> values = (List<Integer>) mp.getValue();

			for (int i : values) {
				if(listeners.contains(key))
				listeners.get(key).remove(i);

			}
		}

		return true;
	}

	public synchronized boolean doAction(CustomEvent event) {
	 
				// do stuff

				if (!event.name.equals("")) {
					if (!listeners.containsKey(event.name)) {
						listeners.put(event.name, new ArrayList<CustomActionListener>());

					}
					ArrayList<CustomActionListener> thelisteners = listeners.get(event.name);
					boolean theReturn = true;

					if (DEBUG_MODE == 2 || event.name.equals("doneUpdateValues")) {
						System.out.print(System.currentTimeMillis() + " Acción ejecutada:  " + event.name);

					}
					for (CustomActionListener hl : thelisteners) {
						theReturn = hl.onAction(event);
						if (DEBUG_MODE == 2 || event.name.equals("doneUpdateValues")) {
							System.out.print(" llamadas: ");
							System.out.print(" " + hl.getClass().getName() + ":" + " resultado " + theReturn + ",");
							System.out.println("");
						}

					}
					if (DEBUG_MODE == 2)
						System.out.println(" ");

				}
		 
		return false;
	}

}
