package es.alco.HCleaning;

import java.util.Calendar;




import es.alco.HCleaning.types.Actuator;
import es.alco.HCleaning.types.Actuator_Value;
import es.alco.HCleaning.types.Device;
import es.alco.HCleaning.types.Sensor;
import es.alco.HCleaning.types.Sensor_Value;
import es.alco.HCleaning.types.Surgery;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;


public class DDBBServerVerticle extends AbstractVerticle {
	
	private MySQLPool mySQLPool;
	private MqttClient mqttClient;
	
	
	@Override
	public void start(Promise<Void> startPromise) {

		/*
		 * 
		 *  						CONEXION MYSQL
		 *  
		 *  																	*/ 
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions()
				.setPort(3306)
				.setHost("localhost")
				.setDatabase("daddb")
				.setUser("root")
				.setPassword("rootroot");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);

		//Definicion del router
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		
		
		//Handler del servidor HTTP para la conexion con la base de datos
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startPromise.complete();
				System.out.println("Conexion con la BBDD satisfactoria");
			} else {
				startPromise.fail(result.cause());
				System.out.println("Conexion con la BBDD fallida");
			}
		});
		
		
		/*
		 * 					  ROUTER METODOS REST
		 * 
		 * 
		 *  						  GET
		 *  
		 *  																	*/ 
		
		
		router.get("/api/device/:idDevice").handler(this::getDevice);
		router.get("/api/deviceOf/:idRoom").handler(this::getDeviceRoom);
		router.get("/api/sensorOf/:idDevice/:Type").handler(this::getSensorDevice);
		router.get("/api/actuatorOf/:idDevice/:Type").handler(this::getActuatorDevice);
		router.get("/api/actuator/values/activate/:idActuator").handler(this::activateActuator);
		
		router.get("/api/sensor/values/:idSensor").handler(this::getSensorValues);
		router.get("/api/actuator/values/:idActuator").handler(this::getActuatorValues);
		
		router.get("/api/surgery/:idSurgery").handler(this::getSurgery);
		
		
		/*
		 * 
		 *  						  POST
		 *  
		 *  																	*/ 
	
		router.post("/api/device/new").handler(this::postDevice);
		
		router.post("/api/sensor/values/update/:idSensor").handler(this::updateSensorValues);
		router.post("/api/actuator/values/update/:idActuator").handler(this::updateActuatorValues);
		
		router.post("/api/surgery/values").handler(this::postSurgery);
			
		
		/*
		 * 
		 *  						DELETE
		 *  
		 *  																	*/ 
		router.delete("/api/surgery/:idSurgery").handler(this::deleteSurgery);
		
		
		/*
		 * 
		 *  					 CLIENTE MQTT
		 *  
		 *  																	*/ 
		
		
		
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true).setClientId("MQTT")
				.setUsername("admin").setPassword("admin"));
		mqttClient.connect(1883, "localhost", connection -> {

			if(connection.succeeded()) {
				System.out.println("Nombre del cliente: " + connection.result().code().name()); 
				
				//PURIFIER TOPIC
				mqttClient.subscribe("purifier", MqttQoS.AT_LEAST_ONCE.value(), handlerSubscribe -> {
					if(handlerSubscribe.succeeded()) {
						System.out.println("El cliente se ha suscrito al topic Purificador"); 
					}	
				});
				
				//AC TOPIC
				mqttClient.subscribe("AC", MqttQoS.AT_LEAST_ONCE.value(), handlerSubscribe -> {
					if(handlerSubscribe.succeeded()) {
						System.out.println("El cliente se ha suscrito al topic AC"); 
					}	
				});
				
				//LED ROJO TOPIC
				mqttClient.subscribe("ledRojo", MqttQoS.AT_LEAST_ONCE.value(), handlerSubscribe -> {
					if(handlerSubscribe.succeeded()) {
						System.out.println("El cliente se ha suscrito al topic ledRojo"); 
					}	
				});
				
				//LED VERDE TOPIC
				mqttClient.subscribe("ledVerde", MqttQoS.AT_LEAST_ONCE.value(), handlerSubscribe -> {
					if(handlerSubscribe.succeeded()) {
						System.out.println("El cliente se ha suscrito al topic ledVerde"); 
					}	
				});
			
				
			} else {
				System.out.println("Se ha producido un error en la conexion con el broker");
			}
		});
	}
	
	
	
	/*
	 * 
	 *  					 METODOS REST
	 *  
	 *  																	*/ 
	
	
	/*
	 * 
	 *  					 	  GET
	 *  
	 *  																	*/ 
	
	
	//GET-DEVICE : Devuelve la información de un dispositivo filtrado por su ID. (Parametro = idDevice)
	private void getDevice(RoutingContext routingContext) {		
		mySQLPool.query("SELECT * FROM daddb.device WHERE idDevice = " + routingContext.request().getParam("idDevice"),
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						System.out.println("Consulta satisfactoria (Device: " + routingContext.request().getParam("idDevice") + ")");
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Device(row.getInteger("idDevice"),
									row.getString("IP"),
									row.getString("Name"),
									row.getInteger("idRoom"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
								.end(result.encodePrettily());
						
					} else {
						
						System.out.println("Consulta fallida (Device: " + routingContext.request().getParam("idDevice") + ")");
						
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	//GET-DEVICE :  Devuelve la información de un dispositivo filtrado por salas.  (Parametro = idRoom)
	private void getDeviceRoom(RoutingContext routingContext) {  
		mySQLPool.query("SELECT * FROM device WHERE idRoom = " + routingContext.request().getParam("idRoom"), 
				res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						System.out.println("Consulta satisfactoria (Dispositivo: " + routingContext.request().getParam("idDevice")
								+ "perteneciente a la sala"+ routingContext.request().getParam("idRoom") + ")");
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Device(row.getInteger("idDevice"),
									row.getString("IP"),
									row.getString("Name"),
									row.getInteger("idRoom"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(result.encodePrettily());
						
					}else {
							
						System.out.println("Consulta fallida (Dispositivo: " + routingContext.request().getParam("idDevice") 
								+ "perteneciente a la sala"+ routingContext.request().getParam("idRoom") + ")");
						
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	//GET-SENSOR-DEVICE : Devuelve la información de todos los sensores asociados a un dispositivo. (Parametro = idDevice)
	private void getSensorDevice(RoutingContext routingContext) {  
		mySQLPool.query("SELECT * FROM daddb.sensor WHERE idDevice = " + routingContext.request().getParam("idDevice") +
				" AND Type = " + routingContext.request().getParam("Type"), 
				
				res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						System.out.println("Consulta satisfactoria (Sensor: " + routingContext.request().getParam("idDevice") + ")");
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Sensor(row.getInteger("idSensor"),
									row.getString("Name"),
									row.getString("Type"),
									row.getInteger("idDevice"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
						
						
						
					}else {
							
						System.out.println("Consulta fallida (Sensor: " + routingContext.request().getParam("idDevice") + ")");
						
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	//GET-ACTUATOR-DEVICE : Devuelve la información de todos los actuadores asociados a un dispositivo. (Parametro = idDevice)
	private void getActuatorDevice(RoutingContext routingContext) {  
		mySQLPool.query("SELECT * FROM actuator WHERE idDevice = " + routingContext.request().getParam("idDevice") +
				" AND Type = " + routingContext.request().getParam("Type"), 
				res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						System.out.println("Consulta satisfactoria (Actuator: " + routingContext.request().getParam("idDevice") + ")");
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Actuator(row.getInteger("idActuator"),
									row.getString("Name"),
									row.getString("Type"),
									row.getInteger("idDevice"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(result.encodePrettily());
						
					}else {
							
						System.out.println("Consulta fallida (Actuator: " + routingContext.request().getParam("idDevice") + ")");
						
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	
	//GET-SENSOR_VALUES : Devuelve la informacion de un sensor. (Parametro = idSensor)
	private void getSensorValues(RoutingContext routingContext) { 
		mySQLPool.query("SELECT * FROM daddb.sensor_value WHERE idSensor = " + routingContext.request().getParam("idSensor"), 
				res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						for (Row row : resultSet) {
							
							result.add(JsonObject.mapFrom(new Sensor_Value(row.getInteger("idSensor"),
									row.getFloat("Value"),
									row.getLong("Timestamp"),
									row.getInteger("idSensor"))));
						}
						
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
							.end(result.encodePrettily());
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}

	//GET-ACTUATOR_VALUES : Devuelve la informacion de un actuador. (Parametro = idActuador)
	private void getActuatorValues(RoutingContext routingContext) { 
		mySQLPool.query("SELECT * FROM daddb.actuator_value WHERE idActuator = " + routingContext.request().getParam("idActuator"), 
				res -> {
					if (res.succeeded()) {	
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						for (Row row : resultSet) {
							
							result.add(JsonObject.mapFrom(new Actuator_Value(row.getInteger("idActuator"),
									row.getInteger("Mode"),
									row.getLong("Timestamp"),
									row.getInteger("idActuator"))));
							
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
					}else{
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	//GET-SURGERY : Devuelve la información de las operaciones con sus respectivos filtros. (Parametros = idSurgery, TimestampStart, idRoom)
	private void getSurgery(RoutingContext routingContext) {
		String query = "SELECT * FROM surgery WHERE idSurgery = " + routingContext.request().getParam("idSurgery");
			
		mySQLPool.query(query, res -> {
				
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						JsonArray result = new JsonArray();
						
						System.out.println("Consulta satisfactoria (Surgery: " + routingContext.request().getParam("idSurgery") + ")");
						
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Surgery(row.getInteger("idSurgery"),
									row.getLong("TimestampStart"),
									row.getLong("TimestampEnd"),
									row.getInteger("idRoom"))));
							
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(result.encodePrettily());
						
					} else {
						
						System.out.println("Consulta fallida (Surgery: " + routingContext.request().getParam("idSurgery") + ")");
						
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}

	
	
	//ACTIVATE-ACTUATOR: Envia un mensaje MQTT para activar un actuador manualmente
		private void activateActuator(RoutingContext routingContext) {
			
			mySQLPool.preparedQuery("SELECT * FROM actuator WHERE idActuator = " + routingContext.request().getParam("idActuator"), 
					res -> {
						if (res.succeeded()) {	
							RowSet<Row> resultSet = res.result();
							JsonArray result = new JsonArray();
							
							System.out.println("Consulta satisfactoria (Actuator: " + routingContext.request().getParam("idActuator") + ")");
							
							for (Row row : resultSet) {
								result.add(JsonObject.mapFrom(new Actuator(row.getInteger("idActuator"),
										row.getString("Name"),
										row.getString("Type"),
										row.getInteger("idDevice"))));
								
								if(row.getString("Type") == "Purifier") {
									mqttClient.publish("purifier", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false, publishHandler -> {
										if(publishHandler.succeeded()) {
											System.out.println("El mensaje se ha publicado correctamente");
										}else {
											System.out.println("Error en el mensaje");
										}
									});
								}else if(row.getString("Type") == "AC") {
									mqttClient.publish("AC", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false, publishHandler -> {
										if(publishHandler.succeeded()) {
											System.out.println("El mensaje se ha publicado correctamente");
										}else {
											System.out.println("Error en el mensaje");
										}
									});
								}else if(row.getString("Type") == "Led Rojo") {
									mqttClient.publish("ledRojo", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false, publishHandler -> {
										if(publishHandler.succeeded()) {
											System.out.println("El mensaje se ha publicado correctamente");
										}else {
											System.out.println("Error en el mensaje");
										}
									});
								}
								else if(row.getString("Type") == "Led Verde") {
									mqttClient.publish("ledVerde", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false, publishHandler -> {
										if(publishHandler.succeeded()) {
											System.out.println("El mensaje se ha publicado correctamente");
										}else {
											System.out.println("Error en el mensaje");
										}
									});
								}
							}
							
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(result.encodePrettily());
							
							
						}else {
								
							System.out.println("Consulta fallida (Actuator: " + routingContext.request().getParam("idActuator") + ")");
							
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
									.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
						}
					});
							
			}
	
	/*
	 * 
	 *  					 	  POST
	 *  
	 *  																	*/ 
	
	

	//POST-DEVICE : Inserta un nuevo dispositivo.
	private void postDevice(RoutingContext routingContext) {
		
		Device device = Json.decodeValue(routingContext.getBodyAsString(), Device.class);	
		mySQLPool.preparedQuery("INSERT INTO Device (idDevice, "
						+ "IP, "
						+ "Name, "
						+ "idRoom) "
						+ "VALUES (?,?,?,?)",
						
					Tuple.of(device.getIdDevice(),
							device.getIp(),
							device.getName(), 
							device.getIdRoom()),

				handler -> {
					
					if (handler.succeeded()) {
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(device).encodePrettily());
						
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	
	//UPDATE-SENSOR-VALUES : Inserta los valores de un sensor
	private void updateSensorValues(RoutingContext routingContext) { 

		Sensor_Value sensor_value = Json.decodeValue(routingContext.getBodyAsString(), Sensor_Value.class);	
		mySQLPool.preparedQuery("UPDATE Sensor_Value SET Value = ?, "
							+ "Timestamp = ?, "
							+ "idSensor = ? "
							+ "WHERE idSensor_Value = ?",
						
				 
				Tuple.of(sensor_value.getValue(), 
						Calendar.getInstance().getTimeInMillis(),
						sensor_value.getIdSensor(),
						routingContext.request().getParam("idSensor")),
				handler -> {
					
					if (handler.succeeded()) {
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(sensor_value).encodePrettily());
						
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	
	//UPDATE-ACTUATOR-VALUES : Inserta los valores de un actuador
	private void updateActuatorValues(RoutingContext routingContext) {
		Actuator_Value actuator_value = Json.decodeValue(routingContext.getBodyAsString(), Actuator_Value.class);	
		mySQLPool.preparedQuery("UPDATE Actuator_Value SET Mode = ?, "
								+ "Timestamp = ?, "
								+ "idActuator = ? "
								+ "WHERE idActuator_Value = ?",
				
								
				Tuple.of(actuator_value.getMode(), 
						Calendar.getInstance().getTimeInMillis(),
						actuator_value.getIdActuator_Value(),
						routingContext.request().getParam("idActuator")),
				
				handler -> {
					
					if (handler.succeeded()) {
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(actuator_value).encodePrettily());
						
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	
	
	

	//POST-SURGERY : Inserta una nueva operación.
	private void postSurgery(RoutingContext routingContext) {
		
		Surgery surgery = Json.decodeValue(routingContext.getBodyAsString(), Surgery.class);	
		mySQLPool.preparedQuery("INSERT INTO Surgery (idSurgery, "
									+ "TimestampStart, "
									+ "TimestampEnd, "
									+ "idRoom) "
									+ "VALUES (?,?,?,?)",
									
					Tuple.of(surgery.getIdSurgery(),
							surgery.getTimestampStart(),
							surgery.getTimestampEnd(),
							surgery.getIdRoom()),

				handler -> {
					
					if (handler.succeeded()) {
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(surgery).encodePrettily());
						
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	
	
	/*
	 * 
	 *  					 	  DELETE
	 *  
	 *  																	*/ 
	
	private void deleteSurgery(RoutingContext routingContext) {
		
		mySQLPool.query("DELETE FROM Surgery WHERE idSurgery =  " + routingContext.request().getParam("idSurgery"),
				handler -> {
					
					if (handler.succeeded()) {						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end("Operacion borrada correctamente");
						
						}else {
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
}
