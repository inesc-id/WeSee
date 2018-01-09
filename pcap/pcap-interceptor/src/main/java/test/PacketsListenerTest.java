package test;

import example.ws.cli.IConnectionRegistrator;
import example.ws.cli.listener.ISenderBuilder;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import interception.models.Connection;
import org.junit.Assert;
import org.junit.Test;
import org.pcap4j.core.PcapNativeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketsListenerTest extends Assert {
    private final String dns = "stackoverflow.com";

    @Test
    public void testNewConnection() throws IOException, PcapNativeException, InterruptedException {
        long testTimeMs = 400000;
        List<Connection> connectionList = new CopyOnWriteArrayList<>();
        Pcap4JHostNewConnectionListener connectionsListener =
                new Pcap4JHostNewConnectionListener(testTimeMs, new RegistratorBuilderMock(connectionList), null);
        connectionsListener.findNewInterfaces();
        String ip = InetAddress.getByName(dns).getHostAddress();
        long startTimeMs = System.currentTimeMillis();
        Runnable downloadWebPageTask = () -> getWebPage(dns);
        boolean isPacketFound = false;
        long timesCounter = 1;
        while (!connectionList.stream().anyMatch(connection -> connection.destination.ip.equals(ip)))
        {
            downloadWebPageTask.run();
            Thread.sleep(1000);
            timesCounter++;
            if (System.currentTimeMillis() - startTimeMs > testTimeMs)
            {
                assertTrue(false);
                break;
            }
        }
        Thread.sleep(10000);
        long foundTimes = connectionList.stream().filter(connection -> connection.destination.ip.equals(ip)).count();
        System.out.println(timesCounter);
        System.out.println(foundTimes);
        assertTrue(true);
    }

    private class RegistratorBuilderMock implements ISenderBuilder
    {
        List<Connection> connectionList;

        public RegistratorBuilderMock(List<Connection> connectionList) {
            this.connectionList = connectionList;
        }

        @Override
        public IConnectionRegistrator buildConnectionRegistrator() {
            return connection -> connectionList.add(connection);
        }
    }

    private boolean isConnectionSame(Connection connection, String ip)
    {
        return connection.destination.ip.equals(ip);

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
