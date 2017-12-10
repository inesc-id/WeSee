package wesee.vizsrv.repository.entities.randomizer;

import interception.models.connection_models.Host;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HostGenerator {
    private static Random rnd = new Random();
    public static Host generateHost()
    {
        Host host = new Host();
        boolean isFormed = false;
        while (!isFormed)
        {
            try {
                InetAddress address = rnd.nextBoolean() ? getIpV4Address() : getIpV6Address();
                host.dns = generateDns();
                host.version = address instanceof Inet4Address? Host.IpVersion.IPV4 : Host.IpVersion.IPV6;
                host.ip = address.getHostAddress();
                isFormed = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return host;
    }

    private static String generateDns()
    {
        int dnsLevelsNumber = 3 + rnd.nextInt(3);
        List<String> dnsLevels = new ArrayList<>();
        for (int i = 0; i < dnsLevelsNumber; i++) {
            byte[] someText = new byte[2 + rnd.nextInt(5)];
            rnd.nextBytes(someText);
            dnsLevels.add(new String(someText));
        }
        return String.join(".", dnsLevels);
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
