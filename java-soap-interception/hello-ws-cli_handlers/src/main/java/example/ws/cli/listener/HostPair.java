package example.ws.cli.listener;

import example.ws.server.Host;

public class HostPair {
    public Host source;
    public Host destination;

    public HostPair()
    {
    }

    public HostPair(Host source, Host destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return "HostPair{" +
                "source=" + source.getIp() +
                ", destination=" + destination.getIp() +
                '}';
    }
}
