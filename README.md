<h1 >HCleaning</h1>
<p>&nbsp;</p>
<h2 ><strong>1.</strong>  <strong>Descripción del proyecto</strong> </h2>
<p>Controlar la contaminación en varias áreas hoy en día puede ser un gran reto, existen multitudes de empresas que se dedican a la desinfección de distintas salas, estudios, hospitales, oficinas etc. </p>
<p> </p>
<p>Las salas blancas, son un espacio cerrado, pensado para mantener unos niveles de contaminación mínimos o casi nulos. Sin embargo, uno de los grandes retos que se enfrenta hoy en día la tecnología, es eliminar cualquier agente infeccioso de todas estas salas, evitando de esta manera, la reinfección de pacientes que se someten a algunas intervenciones quirúrgicas. </p>
<p> </p>
<p>El objetivo del proyecto de la evaluación continua es el control y la prevención de la aparición de hongos (concretamente el hongo aspergillus). Este hongo, se encuentra en suspensión en el ambiente, por ello, mi propuesta es realizar o diseñar un sistema de control aplicado a dichas salas blancas, el cual controla, la aparición de hongos aspergillus.</p>
<p> </p>
<p>Adicionalmente, se controlarán a los usuarios que accedan a las salas blancas y se creará un registro de las operaciones planificadas.</p>
<p> </p>
<p>A continuación, se realiza una descripción de los pasos que se han ido desarrollando con el objetivo de la creación de dicha herramienta.</p>
<p>&nbsp;</p>
<h2 ><strong>2.</strong>  <strong>Hardware empleado en la solución</strong></h2>
<p> </p>
<p>Se adjunta los elementos Hardware que se están empleando en el proyecto</p>
<p> </p>
<p align="center">
<img src="images/hardware.png">
</p>
<p align="center">
Figura 1: Elementos Hardware en la solución
</p>
<p>&nbsp;</p>
<h2 ><strong>3.</strong>  <strong>Diagrama de conexión de los componentes</strong></h2>
<p> </p>
<p> </p>
<p>A continuación, se adjunta el diagrama de conexión de los componentes </p>
<p align="center">
<img src="images/DiagramaConexiones.jpg">
</p>    
<p align="center">
Figura 2: Diagrama de conexión de los componentes
</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<h2 ><strong>4.</strong>  Modelo de la base de datos</h2>
<p>&nbsp;</p>
<p><img src="images/ModeloBaseDatos.png" referrerpolicy="no-referrer"></p>
<p align="center">
    Figura 3: Modelo de la base de datos 
</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>La base de datos almacenará información sobre los usuarios de la API Rest, las operaciones programadas. Las salas se controlarán con sensores y actuadores y la información de cada uno de estos dispositivos. </p>
<p> </p>
<p>Existe una clase DEVICE que controla los dispositivos (actuadores y sensores). Estos dispositivos serán colocados en salas blancas. </p>
<p>Se mantiene un registro de los usuarios de la API. Estos pueden activar manualmente la acción de los actuadores y llevar un control sobre las operaciones “SURGERY” programadas para tener las salas totalmente listas.</p>
<p> </p>
<p> </p>
<p>&nbsp;</p>
<p> </p>
<h2 ><strong>5.</strong>  <strong>Descripción de los métodos de la API Rest</strong></h2>
<p> </p>
<p>El diseño de una API REST, se trata de una creación de una interfaz empleando unas reglas muy bien definidas con el objetivo de interactuar con un sistema y obtener su información.</p>
<p> </p>
<p>El método REST se basa en la separación de su API en recursos lógicos, donde estos se manipulan mediante peticiones HTTP con <strong>métodos GET, POST, PUT y DELETE.</strong></p>
<p> </p>
<h3 ><strong>Métodos GET</strong></h3>
<p>Para realizar las consultas a las tablas se ha realizado un método GET para cada tabla que filtra por “id”. </p>
<p>Casos específicos:</p>
<p>·    SURGERY: Se dispone de métodos GET que filtran por “fecha” y “idRoom” para obtener la información de todas las operaciones programadas para una determinada sala. </p>
<p>·    DEVICE, SENSOR, ACTUATOR: Los dispositivos disponen métodos GET que filtran por “idSensor” obteniendo de esta manera la información acerca del sensor/actuador a consultar</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<h3 ><strong>Métodos POST</strong></h3>
<p> </p>
<p>Se han implementado métodos POST para la inserción de datos. Esta inserción de datos se realiza para unos casos determinados:</p>
<p>·    Añadir un nuevo dispositivo
·    Añadir valores de un sensor
·    Añadir valores de un actuador
·    Añadir una nueva operación</p>
<p> </p>
<h3 ><strong>Métodos DELETE</strong></h3>
<p>Se ha implementado un método DELETE para la gestión de las operaciones.</p>
<p> </p>
<p>&nbsp;</p>
<h3 ><strong>IMPLEMENTACIÓN</strong></h3>
<p>&nbsp;</p>
<p> <strong>DEVICE</strong></p>
<p> Descripción peticiones GET: Devuelve la información de un dispositivo filtrado por su idDevice o su idRoom.</p>
<p> Descripcion peticiones POST: Inserta la informción de un nuevo dispositivo</p>
<p> Url &quot;getDevice&quot;: &quot;/api/device/:idDevice”</p>
<p> Url &quot;getDeviceRoom&quot;: &quot;/api/deviceOf/:idRoom”</p>
<p> Url &quot;postDevice&quot;: &quot;/api/device/new&quot;</p>
<p><img src="APIRESTImages/getDevice.png" referrerpolicy="no-referrer"></p>
<p><img src="APIRESTImages/postDevice.png" referrerpolicy="no-referrer"></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><strong>SENSOR</strong></p>
<p>Descripción peticiones GET: Devuelve la información de un sensor filtrado por su idSensor o su idDevice.</p>
<p>Descripción peticiones POST: Inserta los valores de un sensor</p>
<p>Url &quot;getSensorVales&quot;: &quot;/api/sensor/values/:idSensor&quot;</p>
<p>Url &quot;getSensorDevice&quot;: &quot;/api/sensorOf/:idDevice”</p>
<p>Url &quot;postSensorValues&quot;: &quot;/api/sensor/values/:idSensor&quot;</p>
<p><img src="APIRESTImages/getSensor.png" referrerpolicy="no-referrer"></p>
<p><img src="APIRESTImages/updateSensorValues.png" referrerpolicy="no-referrer"></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><strong>ACTUATOR</strong></p>
<p>Descripción peticiones GET: Devuelve la información de un actuador filtrado por su idActuator o su idDevice.</p>
<p>Descripción peticiones POST: Inserta los valores de un actuador</p>
<p>Url &quot;getActuatorVales&quot;: &quot;/api/actuator/values/:idActuator&quot;</p>
<p>Url &quot;getActuatorDevice&quot;: &quot;/api/actuatorOf/:idDevice”</p>
<p>Url &quot;postActuatorValues&quot;: &quot;/api/actuator/values/:idActuator&quot;</p>
<p><img src="APIRESTImages/getActuator.png" referrerpolicy="no-referrer"></p>
<p><img src="APIRESTImages/updateActuatorValues.png" referrerpolicy="no-referrer"></p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p><strong>SURGERY</strong></p>
<p>Descripción peticiones GET: Devuelve la información de las operaciones filtrado por su idSurgery</p>
<p>Descripción peticiones POST: Inserta una nueva operación</p>
<p>Descripción peticiones DELETE: Elimina una operacion filtrada por su idSurgery</p>
<p>Url &quot;getSurgery&quot;: &quot;/api/surgery/:idSurgery&quot;</p>
<p>Url &quot;postSurgery&quot;: &quot;/api/surgery/new&quot;</p>
<p>Url &quot;deleteSurgery&quot;: &quot;/api/surgery/:idSurgery&quot;</p>
<p><img src="APIRESTImages/getSurgery.png" referrerpolicy="no-referrer"></p>
<p><img src="APIRESTImages/postSurgery.png" referrerpolicy="no-referrer"></p>
<p><img src="APIRESTImages/deleteSurgery.png" referrerpolicy="no-referrer"></p>
<p>&nbsp;</p>
<h2 ><strong>6.</strong>  <strong>Descripción de los mensajes MQTT</strong></h2>
<p>Se trata de un protocolo basado en TCP/IP como base de la comunicación. En el caso de la MQTT, hay que tener en cuenta que cada conexión se mantiene abierta siendo reutilizada en cada comunicación.</p>
<p>Su funcionamiento se basa en un servicio de mensajería push con patrón publicador/suscriptor (pub-sub). Este tipo de infraestructura utiliza una conexión cliente con un servidor central conocido como broker, que en nuestro caso es Mosquitto.</p>
<p align="center">
<img src="images/MQTT1.PNG">
</p>
<p align="center">
    Figura 4: Diagrama descripción mensajes MQTT 
</p> 
<p>En el presente proyecto, nuestra estructura de los mensajes MQTT, se representa mediante el siguiente diagrama.</p>
<p align="center">
 <img src="images/mqtt.jpg">
 Figura 5: Diagrama descripción mensajes MQTT (proyecto)
</p>
<p>&nbsp;</p>
<p>En resumen, el cliente desde la aplicación web, elige el actuador que desea activar manualmente y envía un mensaje MQTT el cual llegará al servidor y el cliente “ESP8266” previamente suscrito al topic, recibirá el mensaje alterando el estado del actuador.</p>
