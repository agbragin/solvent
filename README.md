# Solvent

Solvent is a server-side counterpart of [Concentrate](https://github.com/parseq/concentrate). 
Taken together they form a client-server system for genomic data visualization and filtering.

## Requirements

To compile and run Solvent as back-end component you need the following: 

* Java 8 or higher (we recommend [Oracle Java](https://java.com/en/download/))
* [Maven 3](https://maven.apache.org/)

To build stand-alone Concentrate distribution additional components for front-end assembly should be installed:

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

To build stand-alone Concentrate use 'standalone' Maven profile:

```
mvn clean package -P standalone
```

To build Solvent without GUI run:

```
mvn clean package 
```

Both will produce WAR that can be directly executed by:

```
java -jar solvent-1.N.N-RELEASE.war
```

Or deployed on Tomcat server manually or using a command:

```
mvn tomcat7:deploy
```

Deploying parameters are specified in ``pom.xml`` and should be adopted to your environment.


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

Full API description is available in project [Wiki](https://github.com/parseq/solvent/wiki).

### Concentrate

To use Concentrate for genomic data browsing start Concentrate with:

```
java -jar concentrate-1.N.N-RELEASE.war
```

And go to the following address in your web-browser:

```
localhost:8080
```

Concentrate UI should appear.

## Work with references

This version of solvent grabs reference genome data from reference service. Reference service is a web application that provides access 
to genome sequences via RESTful API accompanied by web interface.

Publicly hosted reference service is available and can be accessed by the following URIs:

* [HTTP API](https://reference-explorer.mss.parseq.pro/refservice/api/0/references)
* [Web interface](https://reference-explorer.mss.parseq.pro/reference-explorer-service-web-client/#/home)

