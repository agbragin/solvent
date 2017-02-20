# Solvent

Solvent is a server-side counterpart of Concentrate. 
Taken together they form a client-server system for genomic data visualization and filtering.

## Requirements

To compile and run Solvent as back-end component you need the following: 

* Java 8 or higher (we recommend [Oracle Java](https://java.com/en/download/))
* [Maven 3](https://maven.apache.org/)

To build stand-alone Concentrate distribution additional components for front-end assembly should be intalled:

* [npm](https://www.npmjs.com/)
* [grunt](http://gruntjs.com/)
* [bower](https://bower.io/)
* Any modern web-browser for application access

Note that on some Linux distributions you should add ``node`` to path, e.g. by:

```
ln -s /usr/bin/nodejs /usr/bin/node
```

to prevent ``/usr/bin/env: node: No such file or directory`` error. 

## Build

To build Solvent only run:

```
mvn clean package 
```

This will produce WAR that can be directly executed by:

```
java -jar ghop-core-1.0.0-RELEASE.war
```

Or deployed on Tomcat server manually or using a command:

```
mvn tomcat7:deploy
```

Deploying parameters are specified in ``pom.xml``

For stand-alone Concentrate building separate Maven profile exists. To build stand-alone version run:

```
mvn clean package -P standalone
```

## Access

### Solvent

To test back-end Solvent send GET HTTP request to:

```
http://localhost:8080/references
```

It is necessary to specify Accept header:

```
Accept: application/json
```

Full API description is available in [Confluence](http://confluence.corp.parseq.pro/confluence/display/VF/API).

### Concentrate

To use Concentrate for genomic data exploration start Concentrate with:

```
java -jar ghop-core-1.0.0-RELEASE.war
```

And go to the following address in your web-browser:

```
localhost:8080
```

Concentrate UI should appear.
