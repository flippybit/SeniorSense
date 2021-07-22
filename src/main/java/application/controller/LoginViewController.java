package application.controller;
 
import java.io.IOException;

import application.Configuration;
import application.Main;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.model.Dbase;
import application.model.UsuarioImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class LoginViewController implements CustomActionListener{

	@FXML
	private Label lbBienvenida;
	@FXML
	private TextField tfpass;
	@FXML
	private TextField tfuserName;
	private Stage dialogStage;
	// Referencia a la clase raiz mainApp
	private Main mainApp;
	private Dbase dbaseCtrl;

	public LoginViewController() {

	}

	/**
	 * Inicializa la clase controlador. Este metodo se llama automaticamente
	 * despues de cargar el ficher fxml
	 */
	@FXML
	private void initialize() {

		tfuserName.setText(Configuration.getValue("autoLogin.user"));
		tfpass.setText(Configuration.getValue("autoLogin.password"));
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;

	}

	public void setDataSource(Dbase dbaseCtrl) {
		this.dbaseCtrl = dbaseCtrl;

	}

	  /**
	    *  Se llama cuando se pulsa "LOGIN"
	 * @throws IOException 
	    */
	   @FXML
	   private void handleLoginBt() throws IOException {
		   int veriCode;
			Alert alert = new Alert(AlertType.INFORMATION);
		   //  dbaseCtrl.verifyUser(tfuserName.getText());
		 veriCode = dbaseCtrl.verifyUser(tfuserName.getText(), tfpass.getText());

		 UsuarioImpl u = new UsuarioImpl();
		 u.setPassword(tfpass.getText());
		 u.setNombre(tfuserName.getText()); 
		 
				switch (veriCode){
				case -1: // no tiene tipo de usuario
		           alert.initOwner(dialogStage);
		           alert.setTitle("Fallo de autenticación");
		           alert.setHeaderText(" No tiene tipo de usuario");
		           alert.setContentText(tfuserName.getText() + tfpass.getText() );
		           alert.showAndWait();
					break;

				case 0: // no existe el usuario
			           alert.initOwner(dialogStage);
			           alert.setTitle("Fallo de autenticación");
			           alert.setHeaderText("No existe ese usuario");
			           alert.setContentText(tfuserName.getText() + tfpass.getText() );
			           alert.showAndWait();
					break;

				case 1: // Es tipo Admin
					  alert.initOwner(dialogStage);
			           alert.setTitle("Verificacion correcta");
			           alert.setHeaderText("BIENVENIDO ADMIN");
			           alert.setContentText(tfuserName.getText() + tfpass.getText() );
			           alert.showAndWait();
					break;

				case 2: // Es tipo Sanitario
					  alert.initOwner(dialogStage);
			           alert.setTitle("Verificacion correcta");
			           alert.setHeaderText("BIENVENIDO SANITARIO");
			           alert.setContentText(tfuserName.getText() + tfpass.getText() );
			           alert.showAndWait();
			           mainApp.showPatientOverview();
					break;

				case 3: // Es tipo Paciente
					  alert.initOwner(dialogStage);
			           alert.setTitle("Verificacion correcta");
			           alert.setHeaderText("BIENVENIDO PACIENTE");
			           alert.setContentText(tfuserName.getText() + tfpass.getText() );
			           alert.showAndWait();
			           mainApp.showSinglePatientView();
					break;

					default: // Entras en default
					   alert.initOwner(dialogStage);
			           alert.setTitle("ERROR");
			           alert.setHeaderText("Entras en Default");
			           alert.setContentText(tfuserName.getText() + tfpass.getText() );
			           alert.showAndWait();
						break;
				}

	   }

	/**
	 * Se llama cuando se pulsa "Crear Cuenta"
	 */
	@FXML
	private void handleCrearCuentaBt() {

		mainApp.showSignUpView();

	}

	@Override
	public boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
