

#Building Maven Project
mvn clean compile spring-boot:run

#Deploying Maven Project
mvn compile deploy

#Skip tests for Maven
-Dmaven.test.skip=true

#maven Check Active Profile
mvn help:active-profiles

#Angular CLI for Development
cd src\main\ngapp
ng serve --host 192.168.1.103

