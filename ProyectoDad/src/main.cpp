#include "RestClient.h"
#include <ESP8266WiFi.h>
#include "ArduinoJSON.h"
#include "DHT.h"
#include "DHT_U.h"
#include "Adafruit_Sensor.h"
#include <GP2YDustSensor.h>
#include <PubSubClient.h>




/*
  Poner los pines de los leds rojo, verde, blanco...

  Realizar pruebas

  
*/


//- - - - - - - - - - VARIABLES GLOBALES - - - - - - - - - -
int idDevice, DustSensor, HumiditySensor, TemperatureSensor, LedActuator, PurifierActuator, ACActuator;
int idSensor, idActuator, idRoom, idSurgery = 1, modeL, modeP, modeAC, pinLedRojo = 0, pinLedVerde = 4, pinPurificador = 13, pinAC= 15;
int contPrevAC = 0, contPrevD = 0, tiempoLoop = 0, inicio = 0; 
long timestampStart, timestampEnd, tiempo;
float humedad, temperatura, umbralHumedad = 0.55, umbralTemperatura = 23.0, umbralPolvo = 500;
unsigned int valLed = 0, valPur;
uint16_t densidadPolvo = 0, mediaPolvo = 0;
String response, send;
bool activarDesinfeccion, activarAC, blockedOper;





//- - - - - - - - - - VARIABLES DTH22 - - - - - - - - - -
#define DHTPIN 5                          
#define DHTTYPE DHT22
DHT dht(DHTPIN, DHTTYPE);

///- - - - - - - - - - VARIABLES SHARP - - - - - - - - - -
const uint8_t SHARP_LED_PIN = 12;   
const uint8_t SHARP_VO_PIN = A0;    
GP2YDustSensor dustSensor(GP2YDustSensorType::GP2Y1010AU0F, SHARP_LED_PIN, SHARP_VO_PIN);
char logBuffer[255];

//- - - - - - - - - - CLIENTE WIFI - - - - - - - - - -
char responseBuffer[300];
WiFiClient client;                                                       // HTTP Client     
/*
const char ssid[] = "vodafone0E43";
const char pass[] = "MGFMRT7NPKPUJQ";
String SERVER_IP = "192.168.0.155";                                      //CAMBIAR IP SERVIDOR
*/
const char ssid[] = "Mi 10";
const char pass[] = "73e624800a30";
String SERVER_IP = "192.168.215.203";   //IP MOVIL
int SERVER_PORT = 8080;

//- - - - - - - - - - VARIABLES PARA MQTT - - - - - - - - - -
//const char* mqtt_server = "192.168.0.155";                               //CAMBIAR IP SERVIDOR
const char* mqtt_server = "192.168.215.203";  //IP MOVIL
const char* mqttUser = "admin";
const char* mqttPassword = "admin";
WiFiClient client2;                             
PubSubClient MQTTClient(client2);
long lastMsg = 0;
char msg[50];
int value = 0;


//- - - - - - - - - - REST CLIENT - - - - - - - - - -
RestClient client3 = RestClient("192.168.215.203", 8080);


//- - - - - - - - - - WIFI CONFIG - - - - - - - - - -
void setupWifi(){
  Serial.print("Conectando a la red: ");
  Serial.print(ssid);
  WiFi.begin(ssid, pass);
  WiFi.mode(WIFI_STA);
  while (WiFi.status() != WL_CONNECTED) 
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("Direccion MACs = " + String(WiFi.softAPmacAddress().c_str()));
  Serial.println("Wi-Fi conectado satisfactoriamente");
  Serial.println("");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}



/*
                                     SERIALIZE BODY 
                                                                                     */
  
String serializeBodySensorV(int idSensor, float value, String type)
{
  DynamicJsonDocument  doc(200);

  doc["idSensor_Value"] = idSensor;                                             //{
  doc["value"] = float(value);                                                  //"idSensor": "5",
  doc["type"] = type;                                                           //"type": "Dust",
  doc["timestamp"] = long(0);                                                   //"timestamp": "1621520194000",
  doc["idSensor"] = idSensor;                                                   //"name":"PW154",
                                                                                //"idDevice": "0",
  String output;
  
  serializeJsonPretty(doc, output);
  
  Serial.println(output);

  return output;
}

String serializeBodyActuatorV(int idActuator, int mode, String type)
{
  DynamicJsonDocument  doc(200);
                                                                                //{
  doc["idActuator_Value"] = idActuator;                                         //"idActuator_Value": "5",
  doc["mode"] = mode;                                                           //"mode": 1,  
  doc["type"] = type;                                                           //"type": AC,                                           
  doc["timestamp"] = 0;                                                         //"timestamp": "1621520194000",
  doc["idActuator"] = idActuator;                                               //"idActuator": "5",
                                                                                
  String output;
  
  serializeJsonPretty(doc, output);
  
  Serial.println(output);

  return output;
}

/*
                                   DESERIALIZE BODY 
                                                                                     */

//ELIMINAR
void deserializeBodyDevice(String responseJson){
  if (responseJson != "")
  { 
    const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;

    DynamicJsonDocument doc(capacity);
    
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }
    Serial.print("El dispositivo "+ idDevice );
    Serial.print(" se encuentra en la sala: ");
    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {  
      idRoom = doc["idRoom"].as<int>();
      Serial.println(idRoom); 
    }  
  }
}

void deserializeBodySensor(String responseJson){
  if (responseJson != "")
  { 
    const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;

    DynamicJsonDocument doc(capacity);
    
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {
      idSensor = v["idSensor"].as<int>();
      
    }  
    
  }
}

void deserializeBodySensorValues(String responseJson){
  if (responseJson != "")
  { 
    const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;

    DynamicJsonDocument doc(capacity);
    
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {
      String type = v["Type"].as<String>();
      if(type == "Dust"){
        DustSensor = v["idSensor"].as<int>();
        Serial.println(String(DustSensor, DEC) + ",");
      }else if(type == "Temperature"){
        TemperatureSensor = v["idSensor"].as<int>();
          Serial.println(String(TemperatureSensor, DEC) + ",");
      }
    }  
    
  }
}

void deserializeBodyActuator(String responseJson){
  if (responseJson != "")
  { 
    const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;

    DynamicJsonDocument doc(capacity);
    
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }
    Serial.print("IDs de los Actuadores: ");
    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {
      idActuator = v["idActuator"].as<int>();
    }  
  }
}

void deserializeBodySurgery(String responseJson){
  if (responseJson != "")
  { 
    const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;

    DynamicJsonDocument doc(capacity);
    
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }
    
    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {
      idSurgery = v["idSurgery"].as<int>();
      timestampStart = v["timestampStart"].as<long>();
      timestampEnd = v["timestampEnd"].as<long>();
      idRoom = v["idRoom"].as<int>();
      idDevice = idRoom;
    } 
    Serial.println("Informacion de la operacion: " + idSurgery);
    Serial.println("Dia/hora de inicio: " + timestampStart);
    Serial.println("Dia/hora de finalizacion: " + timestampEnd);
    Serial.println("Sala: " + idRoom); 
  }
}

                                                                                    
/*
                                  FUNCIONES LECTURA  
                                                                                     */

void getTemperaturaHumedad(){
    humedad = dht.readHumidity();                                   //Lectura de humedad              
    temperatura = dht.readTemperature();                      //Lectura de temperatura Celsius

    if (isnan(humedad) || isnan(temperatura)) {
      Serial.println("¡Fallo al leer los datos del sensor DHT22!");
      return;
    }
    /*Serial.print("Humedad: " + String(humedad));                         
    Serial.print("%   Temperatura: " + String(temperatura)); 
    Serial.println(" grados Celsius"); */
      
}

void getPolvo(){
 
  densidadPolvo =  dustSensor.getDustDensity();
  mediaPolvo = dustSensor.getRunningAverage();
  /*Serial.print("Dust density: "); 
  Serial.print(dustSensor.getDustDensity());
  Serial.println(" ug/m3;");
  Serial.print("Running average: ");
  Serial.print(dustSensor.getRunningAverage()); 
  Serial.println(" ug/m3");       */                                
}
  

/*
                                  FUNCIONES ACTIVACION  
                                                                                     */

void desinfeccion(){
  digitalWrite(pinLedRojo, HIGH);
  digitalWrite(pinLedVerde, LOW);
  digitalWrite(pinPurificador, HIGH);
  Serial.print("Purificador activado | Densidad de polvo: " + densidadPolvo);
  Serial.print(" ug/m3 | Media de polvo: " + mediaPolvo);
  Serial.println(" ug/m3");
}

void AC(){
  digitalWrite(pinAC, HIGH);
  Serial.print("Aire acondicionado activado, temperatura actual: " + String(temperatura));
  Serial.println("Humedad actual: " + String(humedad)); 
}

/*
                                    FUNCION AUXILIAR 
                                                                                     */


void test_status(int statusCode)
{
  delay(1000);
  if (statusCode == 200 || statusCode == 201)
  {
    Serial.print("RESULTADO TEST: OKAY (");
    Serial.print(statusCode);
    Serial.println(")");
  }
  else
  {
    Serial.print("RESULTADO TEST: FALLO (");
    Serial.print(statusCode);
    Serial.println(")");
  }
}

void test_response()
{
  Serial.println("RESULTADO POST: (Cuerpo de la respuesta = " + response + ")");
  response = "";
}

/*
                                     FUNCIONES GET 
                                                                                     */

void getSensorsOfDevice(){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      Serial.println("getSensorsOfDevice");
      test_status(client3.get("/api/sensorOf/" + char(idDevice), &response));
      deserializeBodySensor(response);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void getSensorValue(){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      Serial.println("getSensorsValue");
      test_status(client3.get("/api/sensor/values/" + char(idSensor), &response));
      deserializeBodySensorValues(response);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void getActuatorsOfDevice(){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      Serial.println("getActuatorsOfDevice");
      test_status(client3.get("/api/actuatorOf/" + char(idDevice), &response));
      deserializeBodyActuator(response);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void getSurgeryInfo(){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      Serial.println("getSurgeryInfo");
      test_status(client3.get("/api/surgery/" + char(idSurgery), &response));
      deserializeBodySurgery(response);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}


                                                                                    /*
                                     FUNCIONES POST 
                                                                                     */

void postSensorValues(int id, float v, String typeSensor){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String post_body = serializeBodySensorV(id, v, typeSensor);
      Serial.println(post_body.c_str());
      Serial.println("sensor_values post");
      test_status(client3.post("/api/sensor/values/" + char(idSensor), post_body.c_str(), &response));
      test_response();
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void postActuatorValues(int id, int m, String typeActuator){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String post_body = serializeBodyActuatorV(id, m, typeActuator);
      Serial.println("actuator_values post");
      test_status(client3.post("/api/actuator/values/" + char(idActuator), post_body.c_str(), &response));
      test_response();
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

/*
                                 INICIO FUNCIONES MQTT 
                                                                                     */

void callback(char* topic, byte* payload, unsigned int length) {
  String messageTemp;
  const size_t capacity = JSON_OBJECT_SIZE(8) + JSON_ARRAY_SIZE(8) + 60;
  DynamicJsonDocument doc(capacity);
  
  deserializeJson(doc, messageTemp);
  JsonObject object = doc.as<JsonObject>();
  
  if(String(topic) == "purifier"){
    modeP = object["mode"].as<int>();

  }else if(String(topic) == "AC"){
    modeAC = object["mode"].as<int>();
    
  }else if(String(topic) == "led"){
    modeL = object["mode"].as<int>();

  }
}
 
void MQTTReconnect() {
  while (!MQTTClient.connected()) {
    Serial.println("Iniciando conexion MQTT...");
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    if (MQTTClient.connect(clientId.c_str(), mqttUser, mqttPassword)) {
      Serial.println("MQTT conectado satisfactoriamente");
      MQTTClient.subscribe("actuator_value");
      MQTTClient.subscribe("sensor_value");
      MQTTClient.subscribe("surgery");
    } else {
      Serial.print("Fallo, Codigo de error=");
      Serial.print(MQTTClient.state());
      Serial.println(" probando otra vez en 5 segundos");
      delay(5000);
    }
  }
}



/*
                                     FUNCION SETUP 
                                                                                     */

void setup() {
  Serial.begin(9600);
  Serial.println("Dispositivo iniciado");
  
  
  //- - - - - - - - - - CONEXION WIFI - - - - - - - - - -
  setupWifi();

  //- - - - - - - - - - INICIALIZACION CLIENTE MQTT - - - - - - - - - -
  MQTTClient.setServer(mqtt_server, 1884);
  MQTTClient.setCallback(callback);
  
  //- - - - - - - - - - - - - -PINES - - - - - - - - - - - - - - -
  pinMode(pinLedRojo, OUTPUT);
  pinMode(pinLedVerde, OUTPUT);
  pinMode(pinPurificador, OUTPUT);
  pinMode(pinAC, OUTPUT);
  

  digitalWrite(pinLedRojo, LOW);
  digitalWrite(pinLedVerde, HIGH);
  digitalWrite(pinPurificador, LOW);
  digitalWrite(pinAC, LOW);
  //- - - - - - - - - - INICIALIZACION DHT - - - - - - - - - -
  dht.begin();

  //- - - - - - - - - - INICIALIZACION SHARP - - - - - - - - - -
  dustSensor.begin();
}

/*
                                     FUNCION LOOP 
                                                                                     */
 
void loop() {
  if (!MQTTClient.connected()) {
    MQTTReconnect();    // CONECTAMOS EL CLIENTE EN CASO DE NO ESTARLO
  }
    MQTTClient.loop();  //CONSTANTEMENTE COMPRUEBA MENSAJES NUEVOS 
    
    if(millis() > tiempoLoop && inicio == HIGH){
      getTemperaturaHumedad();
      getPolvo();
      //POST VALORES SENSORES
      postSensorValues(DustSensor, float(densidadPolvo), "Dust");
      delay(2000);
      postSensorValues(TemperatureSensor, temperatura, "Temperature");
      delay(2000);
      postSensorValues(HumiditySensor, humedad, "Humidity");
      delay(2000);
      postActuatorValues(LedActuator, modeL, "Led");
      delay(2000);
      tiempoLoop = millis() + 60000;
      
    }

    if(inicio == LOW){
      inicio = 1;
      getSurgeryInfo();
      getSensorsOfDevice();
      getSensorValue();
      getActuatorsOfDevice();
    }
    unsigned long contActualAC = millis();
    unsigned long contActualD = millis();

    if((timestampStart <= (long)gettimeofday) && (timestampEnd >= (long)gettimeofday) && inicio == HIGH){
      blockedOper = 1;

    }else if(densidadPolvo > umbralPolvo && blockedOper == LOW ){
      activarDesinfeccion = 1;
      

    }else if((temperatura > umbralTemperatura || humedad > umbralHumedad) && activarAC == LOW && inicio == HIGH){
      activarAC = 1;
     

    }else if(activarAC == HIGH && inicio == HIGH){
      postActuatorValues(PurifierActuator, modeP, "Purifier");
      desinfeccion();

    }else if(activarDesinfeccion == HIGH && inicio == HIGH){
      postActuatorValues(ACActuator, modeAC, "AC");
      AC();
    
    // El Aire acondicionado se activa durante 10 minutos 
    }else if(((unsigned long)(contActualAC - contPrevAC) >= (600*1000) && activarAC == HIGH) && inicio == HIGH){
      activarAC = 0;
      postActuatorValues(ACActuator, modeAC, "AC");
      contPrevAC = contActualAC;
    
    // Iniciamos la desifenccion tras la operacion (duracion = 30 min = 1800 segundos)
    }else if(((unsigned long)(contActualD - contPrevD) >= (1800 * 1000) && activarDesinfeccion == HIGH) && inicio == HIGH){   
      activarDesinfeccion = 0; 
      postActuatorValues(PurifierActuator, modeP, "Purifier");
      contPrevD = contActualD;
    }

} 










