This is a Java Web Service client 
that uses JAX-WS Handlers to intercept the SOAP messages.

The client uses the wsimport tool (included with the JDK since version 6)
to generate classes that can invoke the web service and 
perform the Java to XML data conversion.

The client needs access to the WSDL file,
either using HTTP or using the local file system.

The handler configuration files are stored by default in: ${basedir}/src/jaxws.


Instructions using Maven:
------------------------

You must start hello-ws first.

The default WSDL file location is ${basedir}/src/wsdl .
The WSDL URL location can be specified in pom.xml
/project/build/plugins/plugin[artifactId="jaxws-maven-plugin"]/configuration/wsdlUrls

The jaxws-maven-plugin is run at the "generate-sources" Maven phase (which is before the compile phase).


To generate stubs using wsimport:
  mvn generate-sources

To compile:
  mvn compile

To run using exec plugin:
  mvn exec:java

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run using appassembler plugin:
  On Windows:
    target\appassembler\bin\hello-ws-cli_handlers.bat http://localhost:8080/hello-ws/endpoint
  On Linux:
    ./target/appassembler/bin/hello-ws-cli_handlers http://localhost:8080/hello-ws/endpoint


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2017-03-10
leic-sod@disciplinas.tecnico.ulisboa.pt
