Picketlink IDM webapp
==========

Basic web GUI for Picketlink IDM API. Picketlink IDM can be found here:
https://github.com/picketlink/picketlink-idm

Requirements
============

Java 6, Maven 3.x, JBoss AS7

Install
=======

Configure the application by setting the correct DB URL and credentials in config file:

src/main/resources/hibernate.cfg.xml

The DB connection parameters should point to existing Picketlink IDM databse (i.e. the database created by Gatein).

Build and deploy with maven (3.x). Minimal required Java version is 1.6. Using the deploy profile, it is able to build and deploy the app to AS7 using the command:

$ mvn clean install -Pdeploy -Dportal.deploy.dir=$GATEIN_DEPLOY_DIR

Usage
=====

Application is accesible on http://localhost:8080/jboss-idm-servlet/idm after deployment.