package es.alco.HCleaning;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class StartVerticle extends AbstractVerticle{
	
	@Override
	public void start(Future<Void> startFuture) {
		vertx.createHttpServer().requestHandler(
				request ->{
					request.response().end();
		}).listen(8081, result->{
			if(result.succeeded()) {
				System.out.println("Todo correcto");
			}else {
				System.out.println(result.cause());
			}
		});
		
		vertx.deployVerticle(DDBBServerVerticle.class.getName());
		//vertx.deployVerticle(MQTTClientVerticle.class.getName());
	}
}


