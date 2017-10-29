package example.ws;

import javax.jws.HandlerChain;
import javax.jws.WebService;

@WebService(endpointInterface = "example.ws.Hello")
@HandlerChain(file = "/hello_handler-chain.xml")
public class HelloImpl implements Hello {

	public String sayHello(String name) {
		return "Hello " + name + "!";
	}

}
