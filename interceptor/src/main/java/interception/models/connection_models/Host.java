package interception.models.connection_models;

import java.net.Inet4Address;
import java.net.InetAddress;

public class Host {
    public Host() {
    }

    public Host(String ip, IpVersion version, String dns) {
        this.ip = ip;
        this.dns = dns;
        this.version = version;
    }

    public Host(InetAddress address)
    {
        this.ip = address.getHostAddress();
        this.dns = address.getHostName();
        this.version = address instanceof Inet4Address? IpVersion.IPV4 : IpVersion.IPV6;
    }

    public enum IpVersion {
        IPV4, IPV6
    }
    public String ip;
    public IpVersion version;
    public String dns;
}
