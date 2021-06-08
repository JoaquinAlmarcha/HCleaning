#include "RestClient.h"
#include <ESP8266WiFi.h>
#include "ArduinoJSON.h"
#include "DHT.h"
#include "DHT_U.h"
#include "Adafruit_Sensor.h"
#include <GP2YDustSensor.h>
#include <PubSubClient.h>
#include <NTPClient.h>
#include <WiFiUdp.h>




/*
  BUSCAR AÑADIR Y COMPLETAR JEJE, BUSCAR UMBRAL DE POLVO -.-
  retocar los tiempos de temporizacion
  mensajes mqtt
  
*/


//- - - - - - - - - - VARIABLES GLOBALES - - - - - - - - - -
int idDevice=1, DustSensorI = 0, HumiditySensor = 0, TemperatureSensor = 0, idLedVActuator = 0, idLedRActuator = 0;
int idSensor =2, idActuator = 2, idRoom, idSurgery = 1, modeRL= 0, modeVL = 0, modeP = 0, modeAC = 0;
int contPrevAC = 0, contPrevD = 0, tiempoLoop = 0, PurifierActuator = 0, ACActuator = 0; 
int pinLedRojo = 0, pinLedVerde = 10, pinPurificador = 13, pinAC= 15;
long timestampStart, timestampEnd, tiempo;
float humedad, temperatura, umbralHumedad = 55.0, umbralTemperatura = 23.0; //añadir temp = 23.0
unsigned int valLed = 0, valPur;
uint16_t densidadPolvo = 0, mediaPolvo = 0, umbralPolvo = 300;
String response, send;
bool activarDesinfeccion, activarAC, blockedOper, inicio, flagAC, flag, flagDesinfeccion;





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

//- - - - - - - - - - NTP CLIENT - - - - - - - - - -
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "192.168.215.203");

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
  
String serializeBodySensorV(int idSensor, float value)
{
  DynamicJsonDocument  doc(200);

  doc["idSensor_Value"] = idSensor;                                             //{
  doc["Value"] = float(value);                                                  //"idSensor": "5",                           
  doc["Timestamp"] = 0;                                                         //"timestamp": "1621520194000",
  doc["idSensor"] = idSensor;                                                   //"name":"PW154",
                                                                                //"idDevice": "0",
  String output;
  
  serializeJsonPretty(doc, output);
  
  

  return output;
}

String serializeBodyActuatorV(int idActuator, int mode)
{
  DynamicJsonDocument  doc(200);
                                                                                //{
  doc["idActuator_Value"] = idActuator;                                         //"idActuator_Value": "5",
  doc["Mode"] = mode;                                                           //"mode": 1,                                       
  doc["Timestamp"] = 0;                                                         //"timestamp": "1621520194000",
  doc["idActuator"] = idActuator;                                               //"idActuator": "5",
                                                                                
  String output;
  
  serializeJsonPretty(doc, output);
  

  return output;
}

/*
                                   DESERIALIZE BODY 
                                                                                     */


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
    
    JsonArray array = doc.as<JsonArray>();
    for(JsonObject v : array) {  
      idRoom = v["idRoom"].as<int>();
    }  
  }
}

void deserializeBodySensor(String responseJson, String type){
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
     
      if(type == "Dust"){
        DustSensorI = v["idSensor"].as<int>();
        
      }else if(type == "Temperature"){
        TemperatureSensor = v["idSensor"].as<int>();
         
      }else if(type == "Humidity"){
        HumiditySensor = v["idSensor"].as<int>();
      }
    }  
    
    
  }
}

void deserializeBodyActuator(String responseJson, String type){
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
     
      if(type == "Purifier"){
        PurifierActuator = v["idActuator"].as<int>();

      }else if(type == "AC"){
        ACActuator = v["idActuator"].as<int>();
          
      }else if(type == "LedRojo"){
        idLedRActuator = v["idActuator"].as<int>();
        
      }else if(type == "LedVerde"){
        idLedVActuator = v["idActuator"].as<int>();
      }
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
      timestampStart = v["TimestampStart"].as<long>();
      timestampEnd = v["TimestampEnd"].as<long>();
      idRoom = v["idRoom"].as<int>();
    }  
    idDevice = idRoom;
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
    /*
    Serial.print("Humedad: " + String(humedad));                         
    Serial.print("%   Temperatura: " + String(temperatura)); 
    Serial.println(" grados Celsius");
    */
}

void getPolvo(){
 
  densidadPolvo =  dustSensor.getDustDensity();
  mediaPolvo = dustSensor.getRunningAverage();
 /* Serial.print("Dust density: "); 
  Serial.print(dustSensor.getDustDensity());
  Serial.println(" ug/m3;");
  Serial.print("Running average: ");
  Serial.print(dustSensor.getRunningAverage()); 
  Serial.println(" ug/m3");      */                                
}
  

/*
                                  FUNCIONES ACTIVACION  
                                                                                     */



void desinfeccion(){
  Serial.println("Desinfeccion activada");
  digitalWrite(pinLedRojo, HIGH);
  digitalWrite(pinLedVerde, LOW);
  digitalWrite(pinPurificador, HIGH);
  
}

void AC(){
  digitalWrite(pinAC, HIGH);
  Serial.println("Aire acondicionado activado");

  //AÑADIR MENSAJE MQTT , EL AIRE SE HA CONECTADO , IGUAL PARA DESINFECCION
}



/*
                                    FUNCION AUXILIAR 
                                                                                     */


void test_status(int statusCode)
{
  delay(1000);
  if (statusCode == 200 || statusCode == 201)
  {
    /*
    Serial.print("Completado (");
    Serial.print(statusCode);
    Serial.println(")");
    */
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
  Serial.println("RESULTADO: (Cuerpo de la respuesta = " + response + ")");
  response = "";
}

/*
                                     FUNCIONES GET 
                                                                                     */

void getSensorsOfDevice(String t){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      
      String query = "/api/sensorOf/";
      query += String(idDevice, DEC);
      query += String("/'");
      query += String(t);
      query += String("'");
      
      test_status(client3.get(query.c_str(), &response));
      //test_response();
      
      deserializeBodySensor(response, t);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void getActuatorsOfDevice(String t){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String query = "/api/actuatorOf/";
      query += String(idDevice, DEC);
      query += String("/'");
      query += String(t);
      query += String("'");
      
      test_status(client3.get(query.c_str(), &response));
      //test_response();
      deserializeBodyActuator(response, t);
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void getSurgeryInfo(){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String query = "/api/surgery/";
      query += String(idSurgery, DEC);
      test_status(client3.get(query.c_str(), &response));
      //test_response();
      deserializeBodySurgery(response);

     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}


                                                                                    /*
                                     FUNCIONES POST 
                                                                                     */

void postSensorValues(int id, float v){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String post_body = serializeBodySensorV(id, v);
      String query = "/api/sensor/values/update/";
      query += String(id, DEC);
      test_status(client3.post(query.c_str(), post_body.c_str(), &response));
      //test_response();
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

void postActuatorValues(int id, int m){
  response = "";
  if (WiFi.status() == WL_CONNECTED){
      String post_body = serializeBodyActuatorV(id, m);
      
      String query = "/api/actuator/values/update/";
      query += String(id, DEC);
      test_status(client3.post(query.c_str(), post_body.c_str(), &response));
      //test_response();
     
  }else{
    Serial.println("Error al conectar mediante WIFI");
  }
}

/*
                                 INICIO FUNCIONES MQTT 
                                                                                     */

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  
  if(String(topic) == "purifier"){
    modeP = !modeP;
    contPrevD = 0;
    activarDesinfeccion = 1; 
    flagDesinfeccion = 1;
    Serial.println("Mensaje MQTT PURIFICADOR recibido");
    if(modeP == 1){
      digitalWrite(pinPurificador, HIGH);
      Serial.println("Se ha encendido el PURIFICADOR mediante MQTT");
    }else{
      digitalWrite(pinPurificador, LOW);
      Serial.println("Se ha encendido el PURIFICADOR mediante MQTT");
    }
    postActuatorValues(PurifierActuator, modeP);

  }else if(String(topic) == "AC"){
    modeAC = !modeAC;
    contPrevAC = 0;
    activarAC = 1;
    flagAC = 1;
    Serial.println("Mensaje MQTT AC recibido");
    if(modeAC == 1){
      digitalWrite(pinAC, HIGH);
      Serial.println("Se ha encendido el AC mediante MQTT");
    }else{
      digitalWrite(pinAC, LOW);
      Serial.println("Se ha apagado el AC mediante MQTT");
    }
    postActuatorValues(ACActuator, modeAC);
    
  }else if(String(topic) == "ledRojo"){
    modeRL = !modeRL;    
    Serial.println("Mensaje MQTT LED ROJO recibido");
    if(modeRL == 1){
      digitalWrite(pinLedRojo, HIGH);
      Serial.println("Se ha encendido el LED ROJO mediante MQTT");
    }else{
      digitalWrite(pinLedRojo, LOW);
      Serial.println("Se ha apagado el LED ROJO mediante MQTT");
    }
    postActuatorValues(idLedRActuator, modeRL);

  }else if(String(topic) == "ledVerde"){
    modeVL = !modeVL;
    Serial.println("Mensaje MQTT LED VERDE recibido");
    if(modeVL == 1){
      digitalWrite(pinLedVerde, HIGH);
      Serial.println("Se ha encendido el LED VERDE mediante MQTT");
    }else{
      digitalWrite(pinLedVerde, LOW);
      Serial.println("Se ha apagado el LED VERDE mediante MQTT");
    }
    
    postActuatorValues(idLedVActuator, modeVL);
  }
  
}
 
void MQTTReconnect() {
  while (!MQTTClient.connected()) {
    Serial.println("Iniciando conexion MQTT...");
    
    if (MQTTClient.connect("ESP8266Client")) {
      Serial.println("MQTT conectado satisfactoriamente");
      MQTTClient.subscribe("purifier");
      MQTTClient.subscribe("AC");
      MQTTClient.subscribe("ledRojo");
      MQTTClient.subscribe("ledVerde");
    } else {
      Serial.print("Fallo, Codigo de error = ");
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
  MQTTClient.setServer(mqtt_server, 1883);
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

  //- - - - - - - - - - INICIALIZACION NTP - - - - - - - - - -
  timeClient.begin();

  modeRL = 0;
  modeVL = 1;
  modeAC = 0;
  modeP = 0;

  inicio = 0;
  blockedOper = 0;
  activarAC = 0;
  activarDesinfeccion = 0;

  flagDesinfeccion = 0;
  flagAC = 0;
  timeClient.setTimeOffset(0);
}

/*
                                     FUNCION LOOP 
                                                                                     */
 
void loop() {
  if (!MQTTClient.connected()) {
    MQTTReconnect();    // CONECTAMOS EL CLIENTE EN CASO DE NO ESTARLO
  }
    MQTTClient.loop();  //CONSTANTEMENTE COMPRUEBA MENSAJES NUEVOS 
  
    timeClient.update();
    unsigned long contActualAC = millis();
    unsigned long contActualD = millis();
    unsigned long epochTime = timeClient.getEpochTime();
    
    if(inicio == LOW){
      inicio = 1;
      
      Serial.println("Iniciando valores..");
      getSurgeryInfo();
      getSensorsOfDevice("Dust");
      getSensorsOfDevice("Temperature");
      getSensorsOfDevice("Humidity");
  
      getActuatorsOfDevice("Purifier");
      getActuatorsOfDevice("AC");
      getActuatorsOfDevice("LedVerde");
      getActuatorsOfDevice("LedRojo");
      Serial.println("Valores iniciados correctamente");
      
      Serial.println();
     //pruebaLeds();
    }

    if(millis() > tiempoLoop && inicio == HIGH){
      getTemperaturaHumedad();
      getPolvo();
      //POST VALORES SENSORES
      postSensorValues(DustSensorI, float(densidadPolvo));
      delay(4000);
      postSensorValues(TemperatureSensor, temperatura);
      delay(4000);
      postSensorValues(HumiditySensor, humedad);
      delay(4000);
      postActuatorValues(idLedRActuator, modeRL);
      delay(4000);
      postActuatorValues(idLedVActuator, modeVL);
      delay(4000);
      tiempoLoop = millis() + 60000;

    }else if(timestampStart >= epochTime && epochTime <= timestampEnd){
      blockedOper = 1;
      
    }else if((densidadPolvo > umbralPolvo) && inicio == HIGH && flagDesinfeccion == LOW && blockedOper == LOW){
        flagDesinfeccion = 1;
        

    }else if(flagDesinfeccion == HIGH && inicio == HIGH && activarDesinfeccion == LOW){
        activarDesinfeccion = 1;
        modeP = 1;
        modeRL = 1;
        modeVL = 0; 
        desinfeccion();
        postActuatorValues(idLedVActuator, modeVL);
        delay(4000);
        postActuatorValues(idLedRActuator, modeRL);
        delay(4000);
        postActuatorValues(PurifierActuator, modeP);

    // Iniciamos la desifenccion tras la operacion (duracion = 30 min = 1800 * 1000 segundos)
    }else if(((unsigned long)(contActualD - contPrevD) >= (1800000) && activarDesinfeccion == HIGH) && inicio == HIGH){   
      activarDesinfeccion = 0; 
      flagDesinfeccion = 0;
      modeP = 0;
      blockedOper = 0;
      digitalWrite(pinPurificador, LOW);
      postActuatorValues(PurifierActuator, modeP);
      Serial.println("Desinfeccion finalizada");
      contPrevD = contActualD;


    }else if(((temperatura > umbralTemperatura) || (humedad > umbralHumedad)) && inicio == HIGH && flagAC == LOW ){
      flagAC = 1;
      

    }else if(flagAC == HIGH && inicio == HIGH && activarAC == LOW){
        activarAC = 1;
        modeAC = 1;
        AC();
        postActuatorValues(ACActuator, modeAC);

    //
    }else if(((unsigned long)(contActualAC - contPrevAC) >= (900000) && activarAC == HIGH) && inicio == HIGH){
        activarAC = 0;
        flagAC = 0;
        modeAC = 0;
        digitalWrite(pinAC, LOW);
        postActuatorValues(ACActuator, modeAC);
        contPrevAC = contActualAC;
        Serial.println("Aire acondicionado desconectado");
        //AÑADIR MENSAJE MQTT , EL AIRE SE HA DESCONECTADO
    }
        
    
  } 









