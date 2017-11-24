package example.ws.cli.listener;

public interface IConnectionsStorage {
    /**
     * @param sourceIp
     * @param destinationIp
     * @return null if the old connection
     */
    HostPair putNewConnection(String sourceIp, String destinationIp);
}
