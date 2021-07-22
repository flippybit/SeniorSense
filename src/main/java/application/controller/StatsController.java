package application.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import application.Configuration;
import application.container.SensorContainer;
import application.container.UserDBContainer;
import application.controller.classes.AutoUpdate;
import application.exception.UserException;
import application.implemet.ArduMessageDef;
import application.implemet.CommunicationCenter;
import application.implemet.MyCommunicationMessage;
import application.interfaces.CustomEvent;
import application.interfaces.Usuario;
import application.model.Sensor;
import application.model.SensorData;
import application.model.SensorNotification;
import application.model.context.Context;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import application.interfaces.CustomEvent;

public class StatsController extends AutoUpdate {

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private LineChart<String, Number> lineChart;

	@FXML
	private GridPane pane;

	@FXML
	private GridPane gridpanechart;
	
	@FXML
	private GridPane gripPaneLine;
	
	XYChart.Series<String,Number> dataSeries1;

	Map<String, ObservableList<XYChart.Data<String,Number>>> totalDatasperHour ;
	Map<String, ObservableList<XYChart.Data<String,Number>>> totalNotisperHour ;
	Map<String,XYChart.Series<String,Number>> listSerie;
	Map<String,XYChart.Series<String,Number>>notySerie;
	@FXML
	private void initialize() {

		Context.getInstance().registerListener("doneUpdateValues", this);
		Context.getInstance().registerListener("doneReceiveAlert", this);
		 
		Usuario u;
		try {
			
			
			if(Configuration.getCustomValue("impresionateUser",Usuario.class)!=null)
			{
				u= (Usuario) Configuration.getCustomValue("impresionateUser",Usuario.class);
				
			
			}else {
				u = UserDBContainer.getUser(UserController.getInstance().user.getId(), true);
			}
		 
			System.out.println("STADISTICAS" +u.getNombre());
			
			listSerie = new HashMap<String,XYChart.Series<String,Number>>();
			notySerie = new HashMap<String,XYChart.Series<String,Number>>();
			
			List<Sensor> sensorList = SensorContainer.getInstance().getAllSensorByUserId(u.getId());
		    totalDatasperHour = new HashMap<String, ObservableList<XYChart.Data<String,Number>>>();
		    totalNotisperHour = new HashMap<String, ObservableList<XYChart.Data<String,Number>>>();
			SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd H");
			
			System.out.println("SENSOR LIST 1 "+sensorList.size());
			
			final CategoryAxis xAxis = new CategoryAxis();
			final NumberAxis yAxis = new NumberAxis();

			xAxis.setLabel("Hora");
			yAxis.setLabel("Valores");
			barChart = new BarChart(xAxis, yAxis);
			
			final CategoryAxis xAxis2 = new CategoryAxis();
			final NumberAxis yAxis2 = new NumberAxis();

			xAxis2.setLabel("Hora");
			yAxis2.setLabel("Total");
			lineChart = new LineChart(xAxis2, yAxis2);
			long actualMilist = System.currentTimeMillis();
			Date today = new Date(actualMilist);
			XYChart.Series<String,Number> dataSeries;
			XYChart.Series<String,Number> notiSeries;
			
			for (Sensor s : sensorList) {
				System.out.println(" iterate sensor "+s.getId_sensor()+" "+s.getId_user());
				dataSeries = new XYChart.Series<String,Number>();
				notiSeries =  new XYChart.Series<String,Number>();
				
				notiSeries.setName(String.valueOf(s.getId_sensor())+" "+Configuration.getValue("sensor.types."+s.getType()));
				dataSeries.setName(String.valueOf(s.getId_sensor())+" "+Configuration.getValue("sensor.types."+s.getType()));
				/** BARCHART **/
				String id_sensor = String.valueOf(s.getId_sensor());
				List<SensorData> sensorDatas = SensorContainer.getInstance().getSensorDatas(s.getId_sensor());
				ObservableList<XYChart.Data<String,Number>> dataList = FXCollections.observableArrayList();
				
				System.out.println(" sdata = "+sensorDatas.size());
				for (SensorData sensorData : sensorDatas) {
					if ((actualMilist - sensorData.getTimestamp() <= 86400000)) {

						String hour = ft.format(new Date(sensorData.getTimestamp())) ;

						int actualValue = 0;
						int index = 0;
						for(XYChart.Data<String,Number> data : dataList)
						{
							if(data.getXValue().equals(hour))
							{
								actualValue = (int) data.getYValue();
							}
							index++;
						}
						
						dataList.add(new Data<String,Number>(hour,++actualValue));
						 
					 
					}
				}
				/** BARCHART **/
				listSerie.put(id_sensor, dataSeries);
				totalDatasperHour.put(id_sensor, dataList);
				dataSeries.setData(totalDatasperHour.get(id_sensor)); 
				barChart.getData().add(dataSeries);
				/** LINE **/
				List<SensorNotification> sensorNotifys = SensorContainer.getInstance().getSensorNotifications(s.getId_sensor());
				ObservableList<XYChart.Data<String,Number>> notifyList = FXCollections.observableArrayList();

				for (SensorNotification sensorNot : sensorNotifys) {
					if ((actualMilist - sensorNot.getTimestamp() <= 86400000)) {

						String hour = ft.format(new Date(sensorNot.getTimestamp())) ;

						int actualValue = 0;
						int index = 0;
						for(XYChart.Data<String,Number> data : dataList)
						{
							if(data.getXValue().equals(hour))
							{
								actualValue = (int) data.getYValue();
							}
							index++;
						}
						
						notifyList.add(new Data<String,Number>(hour,++actualValue));
						 
					 
					}
				}
				notySerie.put(id_sensor, notiSeries);
				totalNotisperHour.put(id_sensor, notifyList);
				notiSeries.setData(totalNotisperHour.get(id_sensor)); 
				lineChart.getData().add(notiSeries);
			}
				 
			 
				 

				
				// barChart.getData().add(dataSeries1);
				
		
		 
				
			
				VBox vbox = new VBox(barChart);
				gridpanechart.getChildren().clear();
				gridpanechart.add(vbox, 0, 0);
				
				VBox vboxLine = new VBox(lineChart);
				gripPaneLine.getChildren().clear();
				gripPaneLine.add(vboxLine, 0, 0);
				pane.autosize();
			
		} catch (UserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Init stats");

	}
	 
	@Override
	public synchronized boolean onAction(CustomEvent event) {
		// TODO Auto-generated method stub
		 
		
		System.out.println("update Stats");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				switch (event.name) {
				case "doneUpdateValues":
					ArduMessageDef amdf = (ArduMessageDef) event.object;
					DecimalFormat df = new DecimalFormat("#.####");
					System.out.println("refresh stats");
					SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd H");
					String hour = ft.format(new Date(amdf.getTimestamp()));

					ObservableList<XYChart.Data<String,Number>> dataList = totalDatasperHour.get(String.valueOf(amdf.getId()));
					Number actualValue = 0;
					int index = 0;
					if(dataList!=null && !dataList.isEmpty())
					{
						for(XYChart.Data<String,Number> data : dataList)
						{
							if(data.getXValue().equals(hour))
							{
								actualValue = (Number) data.getYValue();
							}
							index++;
						}
						int total = actualValue.intValue();
						System.out.println(total);
						total=total+1;
						System.out.println(amdf.getId());
						if(totalDatasperHour.containsKey(String.valueOf(amdf.getId())));
						totalDatasperHour.get(String.valueOf(amdf.getId())).add(new Data<String,Number>(hour,total));
					}
					
					break;
				case "doneReceiveAlert":
				 
				    SensorNotification amdf1 =  (SensorNotification) event.object;
					 
					DecimalFormat df1 = new DecimalFormat("#.####");
					System.out.println("refresh stats");
					SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd H");
					String hour1 = ft1.format(new Date(amdf1.getTimestamp()));

					ObservableList<XYChart.Data<String,Number>> dataList1 = totalNotisperHour.get(String.valueOf(amdf1.getId_sensor()));
					Number actualValue1 = 0;
					int index1 = 0;
					for(XYChart.Data<String,Number> data : dataList1)
					{
						if(data.getXValue().equals(hour1))
						{
							actualValue1 = (Number) data.getYValue();
						}
						index1++;
					}
					int total1 = actualValue1.intValue();
					System.out.println("NOTIS PER HOUR");
					total1=total1+1;
					
					totalNotisperHour.get(String.valueOf(amdf1.getId_sensor())).add(new Data<String,Number>(hour1,total1));
					
					break;	
				}
			}
		});
		return true;
	}
}
