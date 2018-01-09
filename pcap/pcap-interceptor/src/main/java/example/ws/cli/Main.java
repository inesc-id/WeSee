package example.ws.cli;

import example.ws.cli.input.CheckRunArgs;
import example.ws.cli.listener.ISenderBuilder;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import org.pcap4j.core.PcapNativeException;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

public class Main {

	private static final long REFRESH_ADDRESS_TIME_MS = 100000; //100 sec
	private static final long REFRESH_INTERFACES_TIME = 10000; //10 sec
	public static void main(String[] args) throws InterruptedException, UnknownHostException, MalformedURLException {
		CheckRunArgs.getUrl(args);
		String serverUrl = args[0];
		URL url = new URL(serverUrl);
		String serverIp = InetAddress.getByName(url.getHost()).getHostAddress();
		String listernerId = args.length > 1? args[1] : getMachineName();
		ISenderBuilder registratorBuilder = () -> new ConnectionRegistrator(listernerId, serverUrl);
		Pcap4JHostNewConnectionListener hostListener =
				new Pcap4JHostNewConnectionListener(REFRESH_ADDRESS_TIME_MS, registratorBuilder, serverIp);
		try{
			hostListener.findNewInterfaces();
		}
		catch (PcapNativeException e) {
			e.printStackTrace();
		}
		while(true)
		{
			try
			{
				hostListener.findNewInterfaces();
			} catch (PcapNativeException e) {
				e.printStackTrace();
			}
			Thread.sleep(REFRESH_INTERFACES_TIME);
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
