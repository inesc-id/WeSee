package check;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpCheck {
    public static IpVersion recogniseIp(String ipString)
    {
        try {
            InetAddress address = InetAddress.getByName(ipString);
            if (address instanceof Inet4Address)
                return IpVersion.ipv4;
            if (address instanceof Inet6Address)
                return IpVersion.ipv6;
        } catch (UnknownHostException e) {
            return IpVersion.unknown;
        }
        return IpVersion.unknown;
    }

    public enum IpVersion { ipv4, ipv6, unknown }
}
