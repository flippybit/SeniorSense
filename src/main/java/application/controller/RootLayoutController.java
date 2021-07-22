package application.controller;

 


import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import application.Configuration;
import application.Main;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomActionListener;
import application.interfaces.CustomEvent;
import application.model.context.Context;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle; 

import javafx.stage.PopupWindow;
public class RootLayoutController implements CustomActionListener{

	
	 private static final String SQUARE_BUBBLE =
	            "M24 1h-24v16.981h4v5.019l7-5.019h13z";
    // Reference to the main application
    private Main mainApp;
    
    @FXML
    private Circle circle;
    
    
    @FXML
    private GridPane mainpane;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
    	
    	
        this.mainApp = mainApp;
    }
    
    public void setInPane(Pane p)
    {
    	mainpane.getChildren().clear();
    	mainpane.add(p, 0, 0);
     
    	p.autosize();
    	mainpane.autosize();
    }
    
    private Tooltip makeBubble(Tooltip tooltip) {
        tooltip.setStyle("-fx-font-size: 16px; -fx-shape: \"" + SQUARE_BUBBLE + "\";");
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);

        return tooltip;
    }
    @FXML
	private void initialize()
	{
    	Context.getInstance().registerListener("LoginDoneView", this);
    	Context.getInstance().registerListener("doneSocketUserConnected", this);
    	Context.getInstance().registerListener("doneSocketUserDisconnected", this);
    	Context.getInstance().registerListener("dologout", this);
    	
    	(new Thread() {
    		  public void run() {
    			  try {
    					 
    					UserController.getInstance().ConnectToServer(Configuration.getValue("client.bind"),Integer.valueOf(Configuration.getValue("client.port")));
    				} catch (UnknownHostException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    		  }
    		 }).start();
    	
    	Tooltip.install(circle, makeBubble(new Tooltip("SIN CONEXION"))); 
	}
    /**
     * Creates an empty address book.
     */
    @FXML
    private void handleNew() {
      //  mainApp.getuserData().clear();
      //  mainApp.setUserFilePath(null);
    }
    @FXML
    public void openChat()
    {
    	 
    }

    
    @FXML
    public void logout()
    {
    	Context.getInstance().doAction(new CustomEvent("dologout",null));
    	Main.getInstance().showLoginView();
    	UserController.getInstance().user=null;
    	
    }
    
    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show open file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
           // mainApp.loadUserDataFromFile(file);
        }
    }
    /**
     * Saves the file to the person file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML
    private void handleSave() {
//        File personFile = mainApp.getUserFilePath();
//        if (personFile != null) {
//            mainApp.saveUserDataToFile(personFile);
//        } else {
//            handleSaveAs();
//        }
    }

    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            // mainApp.saveUserDataToFile(file);
        }
    }

    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("ArduApp: Una aplicacion para Ambient Assited Living");
        alert.setHeaderText("About: ");
        alert.setContentText("Author: Grupo 6 ");

        alert.showAndWait();
    }
    
    @FXML
    private void handleVerChat(){
    	
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

	@Override
	public boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		switch(event.name)
		{
		case "LoginDoneView":
			
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	Color color = Color.GREEN; // or any other color
			    	String rgb = String.format("%d, %d, %d",
			    	        (int) (color.getRed() * 255),
			    	        (int) (color.getGreen() * 255),
			    	        (int) (color.getBlue() * 255));
			    	circle.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
			    	
			    	Tooltip.install(circle, makeBubble(new Tooltip("Socket conectado \n con login: \n"+UserController.getInstance().user.getNombre()))); 
			    }
			});
			break;
		case "dologout":
			

			MyCommunicationMessage ms = new MyCommunicationMessage("",0,
					"doneLogout");
		 
			try {
				UserController.getInstance().sendMessage(ms);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	Color color = Color.YELLOW; // or any other color
			    	String rgb = String.format("%d, %d, %d",
			    	        (int) (color.getRed() * 255),
			    	        (int) (color.getGreen() * 255),
			    	        (int) (color.getBlue() * 255));
			    	circle.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
			    	
			    	Tooltip.install(circle, makeBubble(new Tooltip("Socket conectado \n sin login"))); 
			    }
			});
			break;
		case "doneSocketUserConnected":
			
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	Color color = Color.YELLOW; // or any other color
			    	String rgb = String.format("%d, %d, %d",
			    	        (int) (color.getRed() * 255),
			    	        (int) (color.getGreen() * 255),
			    	        (int) (color.getBlue() * 255));
			    	circle.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
			    	
			    	Tooltip.install(circle, makeBubble(new Tooltip("Socket conectado \n sin login"))); 
			    }
			});
			break;
		case "doneSocketUserDisconnected":
			UserController.getInstance().user=null;
			Main.getInstance().showLoginView();
			Platform.runLater(new Runnable() {
			    @Override
			    public void run() {
			    	Color color = Color.RED; // or any other color
			    	String rgb = String.format("%d, %d, %d",
			    	        (int) (color.getRed() * 255),
			    	        (int) (color.getGreen() * 255),
			    	        (int) (color.getBlue() * 255));
			    	circle.setStyle("-fx-fill: rgba(" + rgb + ", 0.15);");
			    	
			    	Tooltip.install(circle, makeBubble(new Tooltip("Sin conexion"))); 
			    }
			});
			break;
		}
		
		return false;
	}
}