package example.ws.cli.listener;

public interface IConnectionsStorage {
    /**
     * @param sourceIp
     * @param destinationIp
     * @return null if the old connection_models
     */
    HostPair putNewConnection(String sourceIp, String destinationIp);
}
