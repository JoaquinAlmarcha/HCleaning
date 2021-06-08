package es.alco.HCleaning;



import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;




public class MQTTClientVerticle extends AbstractVerticle {

	MqttClient mqttClient;
	MqttClientOptions options;

	public void start(Promise<Void> startFuture) {

		mqttClient = MqttClient.create(getVertx(), new MqttClientOptions().setAutoKeepAlive(true).setClientId("mqtt")
				.setUsername("root").setPassword("root"));
		mqttClient.connect(1883, "localhost", connection -> {
			if (connection.succeeded()) {
				System.out.println("Conectado cliente MQTT");
				
			} else {
				System.out.println("Se ha producido un error en la conexión al broker");
			}
		});
		
		Router router = Router.router(vertx);
		
		router.route().handler(BodyHandler.create());
		

		activarPurificador();
		activarAC();
		activarLedRojo();
		activarLedVerde();
		
	}
	

	private void activarPurificador() {
	
		mqttClient.publish("purifier", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false,
				publishHandler -> {
					if (publishHandler.succeeded()) {
						System.out.println("El mensaje ha sido publicado con exito");
					} else {
						System.out.println("Error en la publicacion del mensaje");
					}
				});
	}
	
	private void activarAC() {
		
		mqttClient.publish("AC", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false,
				publishHandler -> {
					if (publishHandler.succeeded()) {
						System.out.println("El mensaje ha sido publicado con exito");
					} else {
						System.out.println("Error en la publicacion del mensaje");
					}
				});
	}
	
	private void activarLedRojo() {
		
		mqttClient.publish("ledRojo", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false,
				publishHandler -> {
					if (publishHandler.succeeded()) {
						System.out.println("El mensaje ha sido publicado con exito");
					} else {
						System.out.println("Error en la publicacion del mensaje");
					}
				});
	}
	
	private void activarLedVerde() {
		
		mqttClient.publish("ledVerde", Buffer.buffer('1'), MqttQoS.AT_LEAST_ONCE, false, false,
				publishHandler -> {
					if (publishHandler.succeeded()) {
						System.out.println("El mensaje ha sido publicado con exito");
					} else {
						System.out.println("Error en la publicacion del mensaje");
					}
				});
	}
}

