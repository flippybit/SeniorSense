package application.controller;
import application.interfaces.Usuario;
import application.model.Dbase;
import application.model.Paciente;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Ventana para editar datos del Paciente
 *
 */
public class PatientEditDialogController {

	@FXML
	private TextField nombreTf;
	@FXML
	private TextField apellidosTf;
	@FXML
	private TextField calleTf;
	@FXML
	private TextField codigoPostalTf;
	@FXML
	private TextField ciudadTf;
	@FXML
	private TextField cumpleTf;

	@FXML
	private TextField tipoUsuarioTf;

	private Stage dialogStage;
	private Paciente patient;

	// Referencia al controlador de la bbdd
	private Dbase dbase;
	private boolean okClicked = false;

	/**
	 * Inicializa la clase controlador. Este metodo es invocado automaticamente
	 * despues de cargar el fichero fxml.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Sets the stage of this dialog.
	 *
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Rellena los datos de la persona que van a ser editados.
	 *
	 * @param patient
	 */
	public void setPatient(Paciente patient) {
		this.patient = patient;

		nombreTf.setText(patient.getNombre());
		apellidosTf.setText(patient.getApellidos());
		calleTf.setText(patient.getCalle());
		codigoPostalTf.setText(Integer.toString(patient.getCodigoPostal()));
		ciudadTf.setText(patient.getCiudad());
		cumpleTf.setPromptText("dd.mm.yyyy");
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
	 * Invocado cuando se pulsa "OK"
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			patient.setNombre(nombreTf.getText());
			patient.setApellidos(apellidosTf.getText());
			patient.setCalle(calleTf.getText());
			patient.setCodigoPostal(Integer.parseInt(codigoPostalTf.getText()));
			patient.setCiudad(ciudadTf.getText());
			// patient.setBirthday(DateUtil.parse(cumpleTf.getText()));
			patient.setId(nombreTf.getText().hashCode());
			patient.setPassword("mustChangePassword");
			okClicked = true;
			dialogStage.close();
		}
	}

	/**
	 * Invocado cuando se pulsa "CANCEL"
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Valida las entradas del usuario en los campos de texto.
	 *
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (nombreTf.getText() == null || nombreTf.getText().length() == 0) {
			errorMessage += "No valid first name!\n";
		}
		if (apellidosTf.getText() == null || apellidosTf.getText().length() == 0) {
			errorMessage += "No valid last name!\n";
		}
		if (calleTf.getText() == null || calleTf.getText().length() == 0) {
			errorMessage += "No valid street!\n";
		}

		if (codigoPostalTf.getText() == null || codigoPostalTf.getText().length() == 0) {
			errorMessage += "No valid postal code!\n";
		} else {
			// try to parse the postal code into an int.
			try {
				Integer.parseInt(codigoPostalTf.getText());
			} catch (NumberFormatException e) {
				errorMessage += "No valid postal code (must be an integer)!\n";
			}
		}

		if (ciudadTf.getText() == null || ciudadTf.getText().length() == 0) {
			errorMessage += "No valid city!\n";
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

	public void setDataSource(Dbase dbaseCtrl) {
		this.setDbase(dbaseCtrl);

	}

	public Dbase getDbase() {
		return dbase;
	}

	public void setDbase(Dbase dbase) {
		this.dbase = dbase;
	}

}