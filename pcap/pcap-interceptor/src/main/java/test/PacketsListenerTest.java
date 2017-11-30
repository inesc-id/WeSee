package test;

import example.ws.cli.listener.HostPair;
import example.ws.cli.listener.INewConnectionInterfaceListener;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import example.ws.cli.listener.storage.TimedStorageBuilder;
import javafx.concurrent.Task;
import org.junit.Assert;
import org.junit.Test;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

public class PacketsListenerTest extends Assert {
    private final String dns = "stackoverflow.com";
    @Test
    public void testNewConnection() throws IOException {
        INewConnectionInterfaceListener connectionsListener = new Pcap4JHostNewConnectionListener(
                new TimedStorageBuilder(10000), 100000);
        String ip = InetAddress.getByName("stackoverflow.com").getHostAddress();
        long testTimeMs = 400000;
        long startTimeMs = new Date().getTime();
        Runnable downloadWebPageTask = () -> getWebPage(dns);
        try {
            HostPair newConnection = connectionsListener.waitNextConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isPacketFound = false;
        while ((new Date()).getTime() - startTimeMs < testTimeMs)
        {
            downloadWebPageTask.run();
            try {
                HostPair newConnection = connectionsListener.waitNextConnection();
                if (newConnection == null)
                    continue;
                if (isConnectionSame(newConnection, ip))
                {
                    isPacketFound = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assertTrue(isPacketFound);
    }
    
    private boolean containsHost(HostPair[] connections, String ip, String dns)
    {
        for (HostPair connection : connections) {
            if (connection.destination.getIp().equals(ip) && connection.destination.getDns().contains(dns))
                return true;
        }
        return false;
    }

    private boolean isConnectionSame(HostPair connection, String ip)
    {
        return connection.destination.getIp().equals(ip);

    }

    private void getWebPage(String expectedDns)
    {
        URL url = null;
        try {
            url = new URL("http://" + expectedDns);
            URLConnection connection = url.openConnection();
            InputStream io = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(io, "UTF-8"));
            String content = bufferedReader.readLine();
            while (content != null)
            {
                content = bufferedReader.readLine();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
