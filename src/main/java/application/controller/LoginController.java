package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.gson.internal.LinkedTreeMap;

import application.Configuration;
import application.Main;
import application.container.UserDBContainer;
import application.container.UserMemoryContainer;
import application.exception.UserException;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.Administrador;
import application.model.Paciente;
import application.model.Supervisor;
import application.model.UsuarioImpl;
import application.model.context.Context;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;


public class LoginController implements CustomActionListener{
	@FXML
	// The reference of inputText will be injected by the FXML loader
	public TextField userInput;

	// The reference of outputText will be injected by the FXML loader
	@FXML
	public PasswordField passwordInput;

	// location and resources will be automatically injected by the FXML loader
	@FXML
	public URL location;

	@FXML
	public Button loginButton;

	@FXML
	public Button registerButton;

	@FXML
	private Label messageInfo;

	@FXML
	private ResourceBundle resources;
	private Stage dialogStage;
	// Add a public no-args constructor
	public LoginController()
	{
		
	 	Context.getInstance().registerListener("onSucessfullLogin", this);
	}

	@FXML
	public void onRegister()
	{
		Main.getInstance().showSignUpView();

	}

	@FXML
	public void onLoginClick()
	{
		
		String name= userInput.getText();
		String password = passwordInput.getText();
		UserDBContainer container = UserDBContainer.getInstance();
		
		
		 UsuarioImpl u = new UsuarioImpl();
		 u.setPassword(password);
		 u.setNombre(name);
		 u.setUserAccount(name);
		 try {
			UserController.getInstance().sendMessage(new MyCommunicationMessage("","onActionLogin","ok",u));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void makeLogin(Usuario u)
	{
	
		
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    
		    	Alert alert = new Alert(AlertType.INFORMATION);
				System.out.println("clickme,");
				messageInfo.setText("Logeado! con usuario "+u.getNombre()+" de tipo "+u.getType());
		 
		switch(u.getType())
		{
		case Usuario.USUARIO_ADMIN:
			  alert.initOwner(dialogStage);
			   alert.setTitle("Verificacion correcta");
		       alert.setHeaderText("BIENVENIDO ADMIN");
		       alert.setContentText(u.getNombre() );
		       alert.showAndWait();
		       Main.getInstance().showAdminView();
			break;
		case Usuario.USUARIO_SUPERVISOR:
			  alert.initOwner(dialogStage);
		       alert.setTitle("Verificacion correcta");
		       alert.setHeaderText("BIENVENIDO SUPERVISOR");
		       alert.setContentText(u.getNombre() );
		       alert.showAndWait();
		       Main.getInstance().showPatientOverview();
			break;
		case Usuario.USUARIO_PACIENTE:
			  alert.initOwner(dialogStage);
		       alert.setTitle("Verificacion correcta");
		       alert.setHeaderText("BIENVENIDO PACIENTE");
		       alert.setContentText(u.getNombre() );
		       alert.showAndWait();
		       Main.getInstance().showSinglePatientView();
			break;
		}
		    }
				});
	}

	@FXML
	private void initialize()
	{
		
		userInput.setText(Configuration.getValue("autoLogin.user"));
		passwordInput.setText(Configuration.getValue("autoLogin.password"));
	}

	@FXML
	private void printOutput()
	{

	}

	@Override
	public boolean onAction(CustomEvent event) {


	MyCommunicationMessage mimsj=null;
		
		if(event.object instanceof MyCommunicationMessage)
		{
			  mimsj = (MyCommunicationMessage) event.object;
		} 
	
		switch(event.name)
		{
	   
		case "onSucessfullLogin":
	 
			LinkedTreeMap lintree = (LinkedTreeMap) mimsj.objectmessage;
			
			String jsonmap = CommunicationCenter.getGson().toJson(lintree);
		  
			Usuario usimpl = (UsuarioImpl) CommunicationCenter.deserializeMessageTO(jsonmap,UsuarioImpl.class);
			
			UserController.getInstance().user=usimpl;
			if(usimpl.getType()==Usuario.USUARIO_ADMIN)
			{ 
				makeLogin( usimpl);
			}else if(usimpl.getType()==Usuario.USUARIO_SUPERVISOR)
			{
				makeLogin( usimpl);

			}else {

				makeLogin( usimpl);
			}
			Context.getInstance().doAction(new CustomEvent("LoginDoneView",usimpl));
			break;
		}
		
		return true;
	}
	 
	
	
}
