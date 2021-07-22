package application.interfaces;

import java.lang.annotation.*;  
import java.lang.reflect.*;  


public @interface NatourDao {
	 
	public static final String AUTO_INCREMENT="ASD";
	public static final String NO_DUPLICATES ="NO_DUPLICATE";
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public  @interface Class {
		boolean parent(); 
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface Table{ 
		String value(); 
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface Field{
		String type() default "";
		String value() default ""; 
		String index() default "";
		String index_p() default "";
		String[] options() default "";
		boolean required() default false;
	}
	
}


