# Server-side component of Genome Browser

# Build

This is Maven project, Maven 3 should be installed. To build run:

```
mvn clean compile package 
```

This will produce WAR that can be directly executed by:

```
java -jar ghop-core-1.0.0-RELEASE.war
```

Or deployed on Tomcat server manually or using a command:

```
mvn tomcat7:redeploy
```

Deploying parameters you can find in ``pom.xml``

# Usage

To test send HTTP request to:

```
http://localhost:8080/references
```

with headers:

```
Accept: application/json
```

API description is available in [Confluence](http://confluence.corp.parseq.pro/confluence/display/VF/API).

