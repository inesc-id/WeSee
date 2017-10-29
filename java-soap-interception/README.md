# Java SOAP Web Services interception

This example shows how SOAP messages can be intercepted.

There are 3 projects:
- a library - ws-handlers - that defines the interceptors (called Handlers)
- a server - hello-ws_handlers - that provides a SOAP endpoint
- a client - hello-ws-cli_handlers - that invokes the service.

To build and run:
```
cd ws-handlers
mvn clean compile install

cd ..
cd server
mvn clean compile exec:java 

cd ..
cd client
mvn clean compile exec:java 
```

The server and the client have independent configuration files 
to decide which handlers are to be invoked.

Each project has its own read me file with more details.

--
Tested on JDK 8 u152 running on Windows 10.

--
miguel.pardal@tecnico.ulisboa.pt
