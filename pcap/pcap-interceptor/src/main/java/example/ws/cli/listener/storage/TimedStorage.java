package example.ws.cli.listener.storage;

import example.ws.cli.listener.HostPair;
import example.ws.cli.listener.IConnectionsStorage;
import example.ws.server.HostExtension;
import javafx.util.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class TimedStorage implements IConnectionsStorage {
    /**
     * from-to ips/ call times
     */
    private Map<Pair<String, String>, ConnectionStatistics> sourceToDestinationAddresses = new HashMap<>();
    private Map<String, DnsContainer> ipDnsStorage = new HashMap<>();
    /**
     * Describes the first time the record was registered. Necessary for registering long connections as new ones.
     * time in ms, connection_models pairs
     */
    private SortedMap<Long, HashSet<Pair<String, String>>> occurrenceTable = new TreeMap<>();
    private long refreshMSec;
    public TimedStorage(long refreshMilliSeconds)
    {
        this.refreshMSec = refreshMilliSeconds;
    }

    /**
     * @param sourceIp
     * @param destinationIp
     * @return null if the old connection_models
     */
    public HostPair putNewConnection(String sourceIp, String destinationIp)
    {
        clearOldConnections();

        Pair<String, String> connectionsPair = new Pair<>(sourceIp, destinationIp);
        ConnectionStatistics connectionStatistics = null;
        if (sourceToDestinationAddresses.containsKey(connectionsPair))
        {
            connectionStatistics = sourceToDestinationAddresses.get(connectionsPair);
            connectionStatistics.callTimes++;
            return null;
        }
        connectionStatistics = new ConnectionStatistics();
        sourceToDestinationAddresses.put(connectionsPair, connectionStatistics);
        Pair<String,String> dnsPair = addDns(connectionsPair);
        addConnectionTimeOccurrence(connectionStatistics.occuranceTime, connectionsPair);

        return new HostPair(HostExtension.createHost(sourceIp, dnsPair.getKey()),
                HostExtension.createHost(destinationIp, dnsPair.getValue()));
    }

    public void clearOldConnections()
    {
        long currentTime = (new Date()).getTime();
        while(occurrenceTable.size() > 0 && occurrenceTable.firstKey() < currentTime - refreshMSec)
        {
            long occurrenceTime = occurrenceTable.firstKey();
            Set<Pair<String, String>> outdatedConnections = occurrenceTable.get(occurrenceTime);
            outdatedConnections.remove(occurrenceTable.firstKey());
            for (Pair<String, String> connection: outdatedConnections) {
                deleteConnection(connection);
            }
            occurrenceTable.remove(occurrenceTime);
        }
    }

    private void deleteConnection(Pair<String, String> connection)
    {
        sourceToDestinationAddresses.remove(connection);
        deleteDnsForConnection(connection.getKey(), connection);
        deleteDnsForConnection(connection.getValue(), connection);
    }

    private void deleteDnsForConnection(String ip, Pair<String, String> connection)
    {
        if (ipDnsStorage.containsKey(ip))
            if (ipDnsStorage.get(ip).lastUsedConnection == connection)
                ipDnsStorage.remove(ip);
    }


    private void addConnectionTimeOccurrence(long connectionTimeMs, Pair<String, String> connectionPair)
    {
        HashSet<Pair<String, String>> connectionsInTime;
        if (!(occurrenceTable.containsKey(connectionTimeMs)))
        {
            connectionsInTime = new HashSet<>();
            occurrenceTable.put(connectionTimeMs, connectionsInTime);
        }
        else
            connectionsInTime = occurrenceTable.get(connectionTimeMs);
        if (!connectionsInTime.contains(connectionPair))
            connectionsInTime.add(connectionPair);
    }

    private Pair<String, String> addDns(Pair<String, String> connectionsPair)
    {
        return new Pair<>(refreshOneDns(connectionsPair, connectionsPair.getKey()),
                refreshOneDns(connectionsPair, connectionsPair.getKey()));
    }

    private String refreshOneDns(Pair<String, String> connectionPair, String ip)
    {
        DnsContainer container  = null;
        if (ipDnsStorage.containsKey(ip))
        {
            container = ipDnsStorage.get(ip);
        } else
        {
            container = new DnsContainer();
            try {
                container.dns = InetAddress.getByName(ip).getHostName();
            } catch (UnknownHostException e) {
                // then there is no dns for selected connection_models
            }
            ipDnsStorage.put(ip, container);
        }
        container.lastUsedConnection = connectionPair;
        return container.dns;
    }

    private class ConnectionStatistics
    {
        int callTimes = 0;
        long occuranceTime = (new Date()).getTime();

        public int getCallTimes() {
            return callTimes;
        }
        public long getOccuranceTime() { return occuranceTime; }

        public void increase()
        {
            callTimes++;
        }
    }

    private class DnsContainer
    {
        public Pair<String, String> lastUsedConnection;
        public String dns;

    }
}
