package es.alco.HCleaning;

import java.util.ArrayList;
import java.util.List;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ClientAuth;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.MqttPublishMessage;


public class MQTTServerVerticle extends AbstractVerticle {

	public static final String TOPIC_PURIFIER = "purifier";
	public static final String TOPIC_AC = "AC";
	public static final String TOPIC_LED = "led";
	
	
	
	

	
	public void start(Promise<Void> promise) {
		MqttServerOptions options = new MqttServerOptions();
		options.setPort(1884);
		options.setClientAuth(ClientAuth.REQUIRED);
		MqttServer mqttServer = MqttServer.create(vertx, options);
		init(mqttServer);
	}

	private static void init(MqttServer mqttServer) {
		
	
		mqttServer.endpointHandler(endpoint -> {
			System.out.println("Nuevo cliente MQTT [" + endpoint.clientIdentifier()
				+ "] solicitando suscribirse [Nueva sesión: " + endpoint.isCleanSession() + "]");
			if(endpoint.auth().getUsername().contentEquals("admin") && endpoint.auth().getPassword().contentEquals("admin")) {
				
				endpoint.accept();
				handleSubscription(endpoint);
				handleUnsubscription(endpoint);
				publishHandler(endpoint);
				handleClientDisconnect(endpoint);
				
			}else {
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
			}
		}).listen(ar -> {
			if (ar.succeeded()) {
				System.out.println("MQTT server is listening on port " + ar.result().actualPort());
			} else {
				System.out.println("Error on starting the server");
				ar.cause().printStackTrace();
			}
		});
	}

	
	private static void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe -> {
		List<MqttQoS> grantedQosLevels = new ArrayList<>();
		for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
			System.out.println("Suscripción al topic " + s.topicName());
			grantedQosLevels.add(s.qualityOfService());
		}
		endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
		});
	}
	
	private static void handleUnsubscription(MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe -> {
		for (String t : unsubscribe.topics()) {
			System.out.println("Eliminada la suscripción del topic " + t);
		}
		endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}

	private static void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message -> {
			handleMessage(message, endpoint);
		}).publishReleaseHandler(messageId -> {
			endpoint.publishComplete(messageId);
		});
	}

	private static void handleMessage(MqttPublishMessage message, MqttEndpoint endpoint) {
		System.out.println("Mensaje publicado");
		if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			String topicName = message.topicName();
			switch (topicName) {
				// Hacer algo con el mensaje si es necesario
			}
			endpoint.publishAcknowledge(message.messageId());
		}else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
			endpoint.publishRelease(message.messageId());
		}
	}


	private static void handleClientDisconnect(MqttEndpoint endpoint) {
		endpoint.disconnectHandler(h -> {
			System.out.println("The remote client has closed the connection.");
		});
	}
	
}

