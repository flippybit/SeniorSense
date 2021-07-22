package application.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.Main;
import application.container.MessageContainer;
import application.implemet.MessageImpl;
import application.implemet.UserComparator;
import application.interfaces.Message;
import application.interfaces.MessageSystem;
import application.interfaces.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatDialogController {

	@FXML
	public TextArea chatTa;
 
	@FXML
	public TextArea inputChat;
	 
	@FXML
	public Button enviarBt;

	@FXML
	public TabPane tabPaneTb;

	private Stage dialogStage;
 	
	private Usuario user= null;
	
	private 	MessageContainer mc;
	
	private 	MessageSystem mcf ;
	StringBuilder mensajes ;
	
	private volatile List <MessageImpl> allmensajes;
	@FXML
	private void initialize(){
		
		this.user= Main.getInstance().getDB().getLoggedUser();
		
		  mc = new MessageContainer();
		  mcf =   mc.getMessageSystem();
		  reloadChat();
		  
		
	}
	
	private void reloadChat()
	{
		  mensajes = new StringBuilder();
		  allmensajes = new ArrayList<MessageImpl>();
			List <Message> allMessagesTo = mcf.getAllTo(this.user.getNombre(), 100);
			 
			for(Message mm : allMessagesTo)
			{
				allmensajes.add((MessageImpl) mm);
			}
			List <Message> allMessagesFrom = mcf.getAllFrom(this.user.getNombre(), 100);
			 
			for(Message mmf : allMessagesFrom)
			{
				allmensajes.add((MessageImpl)mmf);
			//	mensajes.append(mmf.toString()+System.getProperty("line.separator"));
			}
			  Collections.sort(allmensajes,new UserComparator());
			for(Message mmA : allmensajes)
			{
				 
			   mensajes.append(mmA.toString()+System.getProperty("line.separator"));
			}
			
			
			chatTa.setText(mensajes.toString());
	}
	
	public void onSendMessage()
	{
		String message = inputChat.getText();
		
		MessageImpl newm = new MessageImpl(user.getNombre(),"admin",message,"");
		 chatTa.appendText(newm.toString()); 
 

	}

	public void SetDialogStage (Stage dialogStage){
		this.dialogStage = dialogStage;

	}

	/**
	 * Carga la vista con los chats del usuario que este logeado
	 */
 
	
 

	public void loadChats () {

 

		// FROM, TO , CONTENIDO, CABEZERA
			 Message m = new MessageImpl("paciente","admin","tengo un problexa","hola me pasa esto");
			 Message m2 = new MessageImpl("admin","paciente","ahora no puedo","solo soy un admin");


//		//Todos los mensajes
//		List <Message> allMessages = mcf.getAll();
//		for(Message mm : allMessages)
//		{
//			// System.out.println(mm.toString());
//		}
//		System.out.println(" Mensajes enviados a paciente ----");
//		// Coger los mensajes enviados al usuario "paciente" | limite 100
//		List <Message> allMessagesTo = mcf.getAllTo("paciente", 100);
//		for(Message mm : allMessagesTo)
//		{
//			 System.out.println(mm.toString());
//		}
//		System.out.println(" Mensajes enviados desde paciente ----");
//		// Coger los mensajes enviados DESDE usuario "paciente" | limite 100
//		List <Message> allMessagesfrom = mcf.getAllFrom("paciente", 100);
//		for(Message mm : allMessagesfrom)
//			{
//			System.out.println(mm.toString());
//		}


	}

	/**
	 * called when user press "OK"
	 */
	private void handleEnviar(){
//		MessageContainer mc = new MessageContainer();
//		MessageSystem mcf =   mc.getMessageSystem();
//		 Message mensaEnviar = new MessageImpl()
//		mcf.send(m);
//		Message m2;
//		mcf.send(m2);
//	MessageContainer.getMessageSystem().send(m);
//	MessageContainer.getMessageSystem().send(m);

	}



}
