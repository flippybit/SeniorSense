package application.model.context;
 
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.Paciente;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class HelperForm implements CustomActionListener{

	private Map<String,String> allowedFields ;
	
	private Button submitButton;
	
	private Map<String,TextField> textFieldList;
	
	private Object object;
	
	public HelperForm()
	{
		allowedFields = new LinkedHashMap<String,String>();
		textFieldList= new LinkedHashMap<String,TextField>();
		
		Context.getInstance().registerListener("postSaveObject", this);
	}
	
	public void setAllowedFields(Map<String,String> vals)
	{
		allowedFields = vals;
	}
	
	public void addAllowedField(String key , String val)
	{
		allowedFields.put(key, val);
		
	}
	
	private String getAllowedField(int index)
	{
		return allowedFields.get(index);
	}
	
	private boolean isFieldAllowed(String field)
	{
		boolean allowed= false;
		Iterator it = allowedFields.entrySet().iterator();
		 
		while(it.hasNext() && !allowed)
		{
			 Map.Entry pair = (Map.Entry)it.next(); 
			String itKey =  (String) pair.getKey();
			String itValue =  (String) pair.getValue();
			
			if(field.toLowerCase().equals(itKey.toLowerCase()))
			{
				allowed = true;
			}
		}
		
		return allowed; 
	}
	
	private List<Method> getMethodList(Object someObject)
	{
		List<Method> methodList = new ArrayList<Method>();
	 
		for (Method method : someObject.getClass().getMethods()) {
			 
		    if (Modifier.isPublic(method.getModifiers())
		        && method.getParameterTypes().length == 0
		        && method.getReturnType() != void.class
		        && (method.getName().startsWith("get") || method.getName().startsWith("is"))
		    ) {
		    
		        
				try {
					  
					if(allowedFields.isEmpty() || this.isFieldAllowed(method.getName()))
					{
						methodList.add(method);
					}
		            
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		return methodList;
	}
	
	
	public  Map<String,TextField> generateFormByClass(Object someObject)
	{
 		 textFieldList = new LinkedHashMap<String,TextField>();
		
		List<Method> methodList = this.getMethodList(someObject);
		
		 
		
		object = someObject;
		
		if(!methodList.isEmpty())
		{
			Iterator<Method> mit = methodList.iterator();
			while(mit.hasNext())
			{
				
				Method m = mit.next();
				
				Object value = "";
				
				try {
					value =   m.invoke(someObject);
					
					String val = "";
					switch(m.getReturnType().getName())
					{
						case  "int":
							val = String.valueOf(value);
							break;
						case "java.lang.String":
							val =  (String) value ;
							break;
						case "long":
							val = String.valueOf(value);
							break;
						case "float":
							val = String.valueOf(value);
							break;
						default :
						 
							val = String.valueOf(value);
							break;
					}
				 
					textFieldList.put(m.getName(),new TextField(val));
				
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		}
		
		Map<String,TextField> newtextFieldList = new LinkedHashMap<String,TextField>();
	
		if(!allowedFields.isEmpty())
		{
			Iterator it = allowedFields.entrySet().iterator();
			 
			while(it.hasNext() )
			{
				 Map.Entry pair = (Map.Entry)it.next(); 
				String itKey =  (String) pair.getKey();
				String itValue =  (String) pair.getValue();
				
				Iterator itField = textFieldList.entrySet().iterator();
				while(itField.hasNext() )
				{
					 Map.Entry pairF = (Map.Entry)itField.next(); 
					String fitKey =  (String) pairF.getKey();
					TextField fitValue =  (TextField) pairF.getValue();
					
					if(fitKey.equals(itKey))
					{
						newtextFieldList.put(fitKey, fitValue);
					}
				}
				
			}
		}else {
			newtextFieldList= textFieldList;
		}
		textFieldList=newtextFieldList;
		return textFieldList;
		 
	}
	public Pane getFormpane(Map<String,TextField> listmap)
	{
		
		return getFormpane(listmap,null);
		
	}
	
	public Map<String,TextField> getTextField()
	{
		return this.textFieldList;
	}
	
	public Pane getFormpane(Map<String,TextField> listmap,List<String> paramList)
	{
		GridPane gp = new GridPane();
		 
		 ColumnConstraints column1 = new ColumnConstraints();
	     column1.setPercentWidth(50);
	     ColumnConstraints column2 = new ColumnConstraints();
	     column2.setPercentWidth(50);
	     gp.getColumnConstraints().addAll(column1, column2); // each get 50% of width
		Iterator  itm = listmap.entrySet().iterator();
		
		
		int i = 0;
		while(itm.hasNext())
		{
			 Map.Entry pair = (Map.Entry)itm.next(); 
		     TextField textField = (TextField) pair.getValue();
  
		     String textValue = (String) pair.getKey();
		     if(allowedFields.containsKey(pair.getKey()))
		     {
		    	 textValue = allowedFields.get(pair.getKey());
		     }
		     
		     gp.add( new HBox(new Label(textValue)), 0, i); // column=1 row=0
		     gp.add( new HBox(textField), 1, (i)); // column=1 row=0
		     i++; 
		}
	
		System.out.println("set button");
		generateSubmitButton("Guardar");
	     gp.add( new HBox(), 0, i); // column=1 row=0
	     gp.add( new HBox(submitButton), 1, (i)); // column=1 row=0  
		
		return gp;
		
	}
	
	public Button generateSubmitButton(String text)
	{
		submitButton = new Button(text);
		submitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	Object newObject = object;
            	
            	Iterator fieldIT = textFieldList.entrySet().iterator();
            	while(fieldIT.hasNext())
            	{
            		 Map.Entry pair = (Map.Entry)fieldIT.next(); 
            		String fieldName =(String) pair.getKey();
            		TextField fieldValue =(TextField)pair.getValue();
            		
            		for (Method method : object.getClass().getMethods()) {
            			
            			if(method.getName().equals(fieldName.replace("get", "set")))
            			{
            				try {
            					Class<?>[] parameterTypes = method.getParameterTypes();
            					 
            					Parameter[] tipes = method.getParameters();
            					System.out.println(method.getName());
            					if(tipes.length==1 )
            					{
            						switch(tipes[0].getType().getName())
            						{
            							case "int":
            								method.invoke(newObject, Integer.parseInt(fieldValue.getText()));
            							break;
            							case "long":
            								method.invoke(newObject, Long.parseLong(fieldValue.getText()));
                						break;
            							case "float":
            								method.invoke(newObject, Float.parseFloat(fieldValue.getText()));
                    						break;
            							case "java.lang.String":
            							 
            								method.invoke(newObject, fieldValue.getText());
                    						break;
            							case "default":
            								
            								break;
            						}
            						
            						
            					}
            				 
								
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            			}
            		}
            	}
            	
                Context.getInstance().doAction(new CustomEvent("preSaveObject",newObject));
            }
        });
		return submitButton;
	}
  
	public Button getSubmitButton()
	{
		return submitButton;
		
	}

	@Override
	public boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		switch(event.name)
		{
		case "postSaveObject":
			System.out.println(" guardado con éxito");
			break;
		}
		return false;
	}
}
