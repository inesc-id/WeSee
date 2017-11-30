package interception.restclient;

import java.net.ConnectException;
import interception.models.*;

public interface IConnectionRegistrator {
    void registerConnections(ConnectionsEntity connectionsEntity) throws ConnectException;
}
