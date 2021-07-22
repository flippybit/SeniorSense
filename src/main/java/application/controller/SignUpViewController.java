package application.controller;

 
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import application.Main;
import application.interfaces.Usuario;
import application.model.Dbase;
import javafx.fxml.FXML;

public class SignUpViewController {

	@FXML
	private TextField tfnombre;

	@FXML
	private TextField tfpassword;

	@FXML
	private TextField tfapellidos;

	@FXML
	private TextField tfemail;

	@FXML
	private TextField tfciudad;

	@FXML
	private TextField tfcalle;

	@FXML
	private TextField tfcodigopostal;

	// Referencia a la clase raiz mainApp
	private Main mainApp;
	private Dbase dbaseCtrl;
	private boolean okClicked = false;
	private Stage dialogStage;
	private Usuario nuevoUsuario;

	public SignUpViewController() {

	}

	/**
	 * Inicializa la clase controlador. Este metodo se llama automaticamente
	 * despues de cargar el ficher fxml.
	 */
	@FXML
	private void initialize() {

	}

	/**
	 * Devuelve true si se pulsa "OK". sino falso.
	 *
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Invocado cuando se pulsa "CANCEL"
	 */
	@FXML
	private void handleCancel() {
		mainApp.showLoginView();
	}

	/**
	 * Invocado cuando se pulsa "OK"
	 */
	@FXML
	private void handleOk() {
	//	if (isInputValid()) {
		//creo nuevo usuario
//			nuevoUsuario = new Usuario();
//
//			nuevoUsuario.setNombre(tfnombre.getText());
//			nuevoUsuario.setApellidos(tfapellidos.getText());
//			nuevoUsuario.setCalle(tfcalle.getText());
//		//	nuevoUsuario.setPostalCode(Integer.parseInt(tfcodigopostal.getText()));
//			nuevoUsuario.setCiudad(tfciudad.getText());
//			nuevoUsuario.setID(tfnombre.getText().hashCode());
//			nuevoUsuario.setUserType("cliente");
//			nuevoUsuario.setPassword(tfpassword.getText());
//
//			dbaseCtrl.saveUserToAccountFile(nuevoUsuario);
//			okClicked = true;
//			// Mostrar el mensaje de Usuario nuevo creado.
//						Alert alert = new Alert(AlertType.WARNING);
//						alert.initOwner(dialogStage);
//						alert.setTitle("Usuario Nuevo Creado ");
//						alert.setHeaderText("		¡¡Nuevo perfil creado!!");
//						alert.setContentText(nuevoUsuario.getNombre()+"Recuerda que tu email y contraseña"
//								+ " son necesarios para Acceder a tu perfil");
//
//
//						alert.showAndWait();
//
//
//						mainApp.showLoginView();

	//	}
	}

	/**
	 * Valida las entradas del usuario en los campos de texto.
	 *
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (tfnombre.getText() == null || tfnombre.getText().length() == 0) {
			errorMessage += "Nombre incorrecto!\n";
		}
		if (tfapellidos.getText() == null || tfapellidos.getText().length() == 0) {
			errorMessage += "Apellidos incorrectos!\n";
		}
		if (tfcalle.getText() == null || tfcalle.getText().length() == 0) {
			errorMessage += "Calle incorrecta!\n";
		}

		if (tfcodigopostal.getText() == null || tfcodigopostal.getText().length() == 0) {
			errorMessage += "Codigo postal incorrecto!\n";
		} else {

			try {
				Integer.parseInt(tfcodigopostal.getText());
			} catch (NumberFormatException e) {
				errorMessage += "Codigo postal incorrecto (debe ser un numero)!\n";
			}
		}

		if (tfciudad.getText() == null || tfciudad.getText().length() == 0) {
			errorMessage += "cuidad incorrecta!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Mostrar el mensaje de error.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Datos Incorrectos");
			alert.setHeaderText("Por favor corrige los datos introducidos");
			alert.setContentText(errorMessage);

			alert.showAndWait();

			return false;
		}
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	public void setDataSource(Dbase dbaseCtrl) {
		this.dbaseCtrl = dbaseCtrl;

	}

}
