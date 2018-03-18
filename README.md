# HiveCentral
Web Application & Controller for the **HiveBot** IOT Devices.
![FirstImage](docs/app.droid.image1.jpg)

## Technology Stack
 - Framework & Libraries
	 - Spring Boot & Integration
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

## Addtional Setup for Local Maven for Distro + Deployment
Changes to be made for ***.m2/settings.xml***


```
<server>
  <id>DISTROSERVER</id>
  <username></username>   <password></password>   <filePermissions>664</filePermissions>
  <directoryPermissions>775</directoryPermissions>   <configuration></configuration>
</server>
<server>
  <id>DEPLOYSERVER</id>
  ....
</server>
```

```
<profile>
    <id>windows10.local</id>
    <activation> <!-- activate if Windows Machine -->
    <os>
        <family>Windows</family>
    </os>
    </activation>
    <properties>
        <profile.distroserver.url>scp://192.168.1.xxx//PathToRepository</profile.distroserver.url>
        <profile.deployserver.url>scp://192.168.1.xxx//PathToDeploymentArea</profile.deployserver.url>
    </properties>
</profile>
```

Maven Check Active Profile
----------
    mvn help:active-profiles