package wesee.vizsrv.repository.entities.randomizer;

import javafx.util.Pair;
import interception.models.Connection;
import interception.models.ConnectionsEntity;
import interception.models.connection_models.ConnectionOccurrence;
import interception.models.connection_models.Host;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class ConnectionsGenerator {
    private static final int CONNECTIONS_NUMBER = 5;
    private static final int HOSTS_NUMBER = 10;
    private static final int MAX_OCCURRENCES_NUMBER = 5;

    private Random rnd = new Random();
    public ConnectionsEntity generate()
    {
        ConnectionsEntity entity = new ConnectionsEntity();
        byte[] hostId = new byte[10];
        rnd.nextBytes(hostId);
        entity.sourceId = new String(hostId);
        entity.connections = generateConnections();
        return entity;
    }

    private Connection[] generateConnections()
    {
        Connection[] connections = new Connection[CONNECTIONS_NUMBER];
        List<Host> hosts = generateHosts();
        for (int i = 0; i < CONNECTIONS_NUMBER; i++) {
            Connection connection = new Connection();
            connection.occurrences = generateOccurances();
            connection.source = hosts.get(rnd.nextInt(hosts.size()));

            Pair<Integer, Integer> hostUniquePairNums = generateUniqueInts(hosts.size());
            connection.source = hosts.get(hostUniquePairNums.getKey());
            connection.destination = hosts.get(hostUniquePairNums.getValue());
            connections[i] = connection;
        }
        return connections;
    }

    private List<Host> generateHosts()
    {
        List<Host> hosts = new ArrayList<>();
        for (int i = 0; i < HOSTS_NUMBER; i++) {
            hosts.add(HostGenerator.generateHost());
        }
        return hosts;
    }

    private Pair<Integer, Integer> generateUniqueInts(int bound)
    {
        int firstValue = rnd.nextInt(bound);
        int secondValue = rnd.nextInt(bound);
        while (firstValue == secondValue)
            secondValue = rnd.nextInt(bound);
        return new Pair<>(firstValue, secondValue);
    }

    private ConnectionOccurrence[] generateOccurances()
    {
        ConnectionOccurrence[] connectionOccurrences = new ConnectionOccurrence[rnd.nextInt(MAX_OCCURRENCES_NUMBER)];
        long nowDateMs = (new Date()).getTime();
        long dayDurationMs = Duration.ofDays(1).getSeconds() * 1000;
        long dayBeforeMs = nowDateMs - dayDurationMs;
        for (int i = 0; i < connectionOccurrences.length; i++) {
            ConnectionOccurrence connectionOccurrence = new ConnectionOccurrence();
            connectionOccurrence.message = Integer.toString(i);
            connectionOccurrence.callTimeMs = dayBeforeMs + (long)(rnd.nextDouble() * dayDurationMs);
            connectionOccurrences[i] = connectionOccurrence;
        }
        return connectionOccurrences;
    }
}
