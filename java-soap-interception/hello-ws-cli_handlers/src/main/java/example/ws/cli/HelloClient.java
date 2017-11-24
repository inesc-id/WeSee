package example.ws.cli;

import example.ws.cli.listener.HostPair;
import example.ws.cli.listener.INewConnectionInterfaceListener;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import example.ws.cli.listener.storage.TimedStorageBuilder;
import example.ws.server.ConnectionsPort;
import example.ws.server.ConnectionsPortService;
import example.ws.server.RegisterConnectionRequest;

import javax.xml.ws.BindingProvider;

public class HelloClient {

	public static void main(String[] args) {
		String serverUrl = args[0];
		ConnectionsPortService service = new ConnectionsPortService();
		ConnectionsPort port = setUrl(serverUrl, service.getConnectionsPortSoap11());
		INewConnectionInterfaceListener connectionInterfaceListener =
				new Pcap4JHostNewConnectionListener(new TimedStorageBuilder(REFRESH_ADDRESS_TIME_MS),
						REFRESH_INTERFACES_TIME);
		while(true)
		{
			try {
				HostPair hostPair = connectionInterfaceListener.waitNextConnection();
				RegisterConnectionRequest request = new RegisterConnectionRequest();
				request.setSource(hostPair.source);
				request.setDestination(hostPair.destination);
				Runnable task = () -> port.registerConnection(request);
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

		}
	}

	private static final long REFRESH_ADDRESS_TIME_MS = 100000; //100 sec
	private static final long REFRESH_INTERFACES_TIME = 10000; //10 sec
	private static ConnectionsPort setUrl(String serverAddress, ConnectionsPort port)
	{
		String endpointURL = serverAddress + "/soap";
		BindingProvider bp = (BindingProvider)port;
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointURL);
		return port;
	}

}
