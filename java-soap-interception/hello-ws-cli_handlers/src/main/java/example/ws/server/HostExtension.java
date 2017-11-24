package example.ws.server;

import example.ws.server.Host;

public class HostExtension {
    public static Host createHost(String ip, String dns)
    {
        Host host = new Host();
        host.ip = ip;
        host.dns = dns;
        return host;
    }

}
