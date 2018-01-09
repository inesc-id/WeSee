package example.ws.cli;

import interception.models.Connection;
import interception.models.ConnectionsEntity;
import interception.restclient.RestConnectionRegistrator;

import java.net.ConnectException;

public class ConnectionRegistrator implements IConnectionRegistrator {
    private String id;
    private interception.restclient.IConnectionRegistrator connectionRegistrator;

    public ConnectionRegistrator(String id, String serverUrl) {
        this.id = id;
        connectionRegistrator = new RestConnectionRegistrator(serverUrl);
    }

    @Override
    public void registerConnection(Connection connection) {
        ConnectionsEntity entity = new ConnectionsEntity();
        entity.sourceId = id;
        entity.connections = new Connection[]{ connection };
        try {
            connectionRegistrator.registerConnections(entity);
        } catch (ConnectException e) {
            e.printStackTrace();
        }
    }
}
