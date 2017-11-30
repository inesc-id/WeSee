package example.ws;

import javax.jws.WebService;

@WebService
public interface Hello {

	String sayHello(String name);

}
