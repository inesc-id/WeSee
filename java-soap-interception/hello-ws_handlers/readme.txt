This is a Java Web Service that uses JAX-WS Handlers
to intercept the SOAP messages.

The service is defined by the Java code with annotations
(code-first approach, also called bottom-up approach).

The service runs in a standalone HTTP server.

The SOAP Handlers are activated in the
handler chain configuration file (handler-chain.xml).


Instructions using Maven:
------------------------

To compile:
    mvn compile

To run using exec plugin:
    mvn exec:java 

To generate launch scripts for Windows and Linux:
  (appassembler:assemble is attached to install phase)
  mvn install

To run using appassembler plugin:
  On Windows:
    target\appassembler\bin\hello-ws_handlers http://localhost:8080/hello-ws/endpoint
  On Linux:
    ./target/appassembler/bin/hello-ws_handlers http://localhost:8080/hello-ws/endpoint


When running, the web service awaits connections from clients.
You can check if the service is running using your web browser 
to see the generated WSDL file:

    http://localhost:8080/hello-ws/endpoint?WSDL

This address is defined in HelloMain when the publish() method is called.

To call the service you will need a web service client,
including code generated from the WSDL.


To configure the Maven project in Eclipse:
-----------------------------------------

'File', 'Import...', 'Maven'-'Existing Maven Projects'
'Select root directory' and 'Browse' to the project base folder.
Check that the desired POM is selected and 'Finish'.


--
Revision date: 2017-03-10
leic-sod@disciplinas.tecnico.ulisboa.pt
