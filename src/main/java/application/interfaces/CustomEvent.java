package application.interfaces;

public class CustomEvent {

	public String name;
	
	public Object object;
	
	public Class<?> classToCast;
	
	public CustomEvent(String name,Object ob)
	{
		this.name=name;
		this.object=ob;
		if(ob!=null)
		this.classToCast=ob.getClass();
	}
	
	
}
