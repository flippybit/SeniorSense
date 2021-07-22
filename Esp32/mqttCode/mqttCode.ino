 /*********
  Rui Santos
  Complete project details at https://randomnerdtutorials.com  
*********/
//Senior Sense 
// Proyecto Informatica I
// Benjamin Ruiz

//#include <WiFi.h>
#include "WiFi.h"
#include <PubSubClient.h>

#include <NTPClient.h>
#include <WiFiUdp.h>

#include <Wire.h>
//#include <Adafruit_BME280.h>
#include "DHT.h"
#include <Adafruit_Sensor.h>
#define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321
#define DHTPIN 27

// Define NTP Client to get time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

// Variables to save date and time
String formattedDate;
String dayStamp;
String timeStamp;

// Your SSID/Password combination
const char* ssid = "dlink-c3p0";
const char* password = "dungavive2018@gmail.com";


// Add your MQTT Broker IP address, example:
//const char* mqtt_server = "192.168.1.144";
const char* mqtt_server = "192.168.0.157";
DHT dht(DHTPIN, DHTTYPE);
WiFiClient espClient;
PubSubClient client(espClient);
long lastMsg = 0;
char msg[50];
int value = 0;
float temperature = 0;
float humidity = 0;

// LED Pin
const int ledPin = 2;
/** Pin number for DHT11 data pin */
int dhtPin = 27;

void setup() {
  Serial.begin(115200);

  // default settings
  // (you can also pass in a Wire library object like &Wire2)
  //status = bme.begin();  
  if (isnan(temperature) || isnan(humidity)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    while (1);
  }
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  pinMode(ledPin, OUTPUT);
}

void setup_wifi() {
  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

Serial.println("Connecting to NTPC... ");
// Initialize a NTPClient to get time
  timeClient.begin();
  // Set offset time in seconds to adjust for your timezone, for example:
  // GMT +1 = 3600
  // GMT +8 = 28800
  // GMT -1 = -3600
  // GMT 0 = 0
  timeClient.setTimeOffset(3600);
  
}

void callback(char* topic, byte* message, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.print(topic);
  Serial.print(". Message: ");
  String messageTemp;
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)message[i]);
    messageTemp += (char)message[i];
  }
  Serial.println();

  // Feel free to add more if statements to control more GPIOs with MQTT

  // If a message is received on the topic esp32/output, you check if the message is either "on" or "off". 
  // Changes the output state according to the message
  if (String(topic) == "esp32/output") {
    Serial.print("Changing output to ");
    if(messageTemp == "on"){
      Serial.println("on");
      digitalWrite(ledPin, HIGH);
    }
    else if(messageTemp == "off"){
      Serial.println("off");
      digitalWrite(ledPin, LOW);
    }
  }
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect("ESP32Client")) {
      Serial.println("connected");
      // Subscribe
      client.subscribe("esp32/output");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}
void loop() {
  
  if (!client.connected()) {
    reconnect();
  }
  client.loop();

  long now = millis();
  if (now - lastMsg > 5000) {
    lastMsg = now;
    
    // Temperature in Celsius
    temperature = dht.readTemperature();       
    // Convert the value to a char array
    char tempString[8];
    dtostrf(temperature, 1, 2, tempString);
    Serial.print("Temperature: ");
    Serial.println(tempString);
    
   String publishableString(tempString);
   String test;
   String test1;
     formattedDate = timeClient.getFormattedDate();
      char pubCharArray[70];
     formattedDate.toCharArray(pubCharArray,formattedDate.length());
     strcat(pubCharArray,"$");
     strcat(pubCharArray,tempString);
  
  
     
    client.publish("esp32/temperature", pubCharArray);

    humidity = dht.readHumidity();
    
    // Convert the value to a char array
    char humString[8];
    dtostrf(humidity, 1, 2, humString);
    Serial.print("Humidity: ");
    Serial.println(humString);
    client.publish("esp32/humidity", humString);
  }
// update time client (not so often)
  while(!timeClient.update()) {
    timeClient.forceUpdate();
  }

  formattedDate = timeClient.getFormattedDate();
  Serial.println(formattedDate);

  delay(2000);
    formattedDate = timeClient.getFormattedDate();
  
  Serial.print("-- 2 sec later --");
  Serial.println(formattedDate);

  // Extract date
 // int splitT = formattedDate.indexOf("T");
 // dayStamp = formattedDate.substring(0, splitT);
 // Serial.print("DATE: ");
 // Serial.println(dayStamp);
  // Extract time
 // timeStamp = formattedDate.substring(splitT+1, formattedDate.length()-1);
 // Serial.print("HOUR: ");
 // Serial.println(timeStamp);
  Serial.println(xPortGetCoreID());
delay(1000);
}
