package test;

import example.ws.cli.listener.INewConnectionInterfaceListener;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import interception.models.Connection;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Date;

public class PacketsListenerTest extends Assert {
    private final String dns = "stackoverflow.com";
    @Test
    public void testNewConnection() throws IOException {
        INewConnectionInterfaceListener connectionsListener =
                new Pcap4JHostNewConnectionListener(40000);
        String ip = InetAddress.getByName(dns).getHostAddress();
        long testTimeMs = 400000;
        long startTimeMs = new Date().getTime();
        Runnable downloadWebPageTask = () -> getWebPage(dns);
        boolean isPacketFound = false;

        while (System.currentTimeMillis() - startTimeMs < testTimeMs)
        {
            downloadWebPageTask.run();
            try {
                Connection newConnection = connectionsListener.waitNextConnection();
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
