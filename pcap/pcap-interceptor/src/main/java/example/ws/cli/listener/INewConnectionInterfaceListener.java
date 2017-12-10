package example.ws.cli.listener;

import interception.models.Connection;

public interface INewConnectionInterfaceListener {
    Connection waitNextConnection() throws Exception;
}
