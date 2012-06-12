JBoss Picketlink IDM servlet

Install

Build and deploy with maven. Minimal required java version is 1.6. Using the deploy profile, it is able to build and deploy the portlet using the command:
mvn clean install -Pdeploy -Dportal.deploy.dir=$GATEIN_DEPLOY_DIR

Usage

Application is accesible on http://localhost:8080/jboss-idm-servlet/ after deployment.
