package datasource.db.randomizer;

import datasource.model.User;
import ws.soapserver.build.Host;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class HostGenerator {
    private static Random rnd = new Random();
    public static User generateHost()
    {
        User host = new User();
        boolean isFormed = false;
        while (!isFormed)
        {
            try {
                InetAddress address = rnd.nextBoolean() ? getIpV4Address() : getIpV6Address();
                String dns = address.getHostName();
                host.dns = dns;
                host.isIpv4 = address instanceof Inet4Address;
                host.ip = address.getHostAddress();
                isFormed = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return host;
    }

    private static Inet4Address getIpV4Address() throws UnknownHostException {
        byte[] addressBytes = new byte[4];
        rnd.nextBytes(addressBytes);
        return (Inet4Address) Inet4Address.getByAddress(addressBytes);
    }

    private static Inet6Address getIpV6Address() throws UnknownHostException {
        byte[] addressBytes = new byte[16];
        rnd.nextBytes(addressBytes);
        return (Inet6Address) Inet6Address.getByAddress(addressBytes);
    }
}
