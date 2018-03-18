# HiveCentral
Web Application & Controller for the **HiveBot** IOT Devices.

## Technology Stack
 - Framework & Libraries
	 - Spring Boot (1.5.6)
	 - Mongo DB (Persistence NoSQL)
	 - Angular 5.x
	 - [mosquitto](https://mosquitto.org/) MQTT Broker
	 - Quartz Scheduler (2.2.1)
 - IDE & tools
	 - Angular CLI (1.6.x)
	 - Nodejs & npm
	 - Visual Studio Code (Recommended)
	 - IntelliJ IDEA (Recommended)
	 - Maven (Build)
 - Embedded tools and Support Libraries
     - [hivemq-mqtt-web-client](http://hivemq.com/demos/websocket-client/) A websockets based MQTT Client for your browser.
     - [Swagger UI](https://swagger.io/swagger-ui/)

Building Maven Project
----------
    mvn clean compile spring-boot:run


Deploying Maven Project
----------
    mvn compile deploy
    // to skip testing
    -Dmaven.test.skip=true


Angular CLI for Running Development container
----------
    cd src\main\ngapp
    ng serve --host 192.168.1.100

Maven Check Active Profile
----------
    mvn help:active-profiles
