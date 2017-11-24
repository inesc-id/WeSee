package example.ws.cli.listener.interfacing;

import example.ws.cli.listener.HostPair;
import example.ws.cli.listener.IConnectionsStorage;
import example.ws.cli.listener.INewConnectionInterfaceListener;

public abstract class AbstractInterfaceNewConnectionListener implements INewConnectionInterfaceListener {
    protected IConnectionsStorage connectionsStorage;

    public AbstractInterfaceNewConnectionListener(IConnectionsStorage connectionsStorage) {
        this.connectionsStorage = connectionsStorage;
    }
}
