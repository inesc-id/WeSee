package example.ws.cli.listener;

import example.ws.cli.listener.HostPair;

public interface INewConnectionInterfaceListener {
    HostPair waitNextConnection() throws Exception;
}
