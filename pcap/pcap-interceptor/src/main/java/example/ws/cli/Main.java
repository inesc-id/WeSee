package example.ws.cli;

import example.ws.cli.input.CheckRunArgs;
import example.ws.cli.listener.INewConnectionInterfaceListener;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import interception.models.Connection;
import interception.models.ConnectionsEntity;
import interception.restclient.IConnectionRegistrator;
import interception.restclient.RestConnectionRegistrator;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

	private static final long REFRESH_ADDRESS_TIME_MS = 100000; //100 sec
	private static final long REFRESH_INTERFACES_TIME = 10000; //10 sec
	public static void main(String[] args) {
		CheckRunArgs.getUrl(args);
		String serverUrl = args[0];
		String listernerId = args.length > 1? args[1] : getMachineName();

		IConnectionRegistrator connectionRegistrator = new RestConnectionRegistrator(serverUrl);
		INewConnectionInterfaceListener connectionInterfaceListener =
				new Pcap4JHostNewConnectionListener(REFRESH_INTERFACES_TIME);
		while(true)
		{
			try {
				Connection connection = connectionInterfaceListener.waitNextConnection();
				if (connection == null)
					continue;
				ConnectionsEntity entity = new ConnectionsEntity();
				entity.sourceId = listernerId;
				entity.connections = new Connection[]{ connection };
				Runnable task = () -> {
					try {
						connectionRegistrator.registerConnections(entity);
					} catch (ConnectException e) {
						e.printStackTrace();
					}
				};
				task.run();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

		}
	}

	private static String getMachineName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.out.print("The machine name is not found");
			System.exit(1);
			return "";
		}
	}


}
