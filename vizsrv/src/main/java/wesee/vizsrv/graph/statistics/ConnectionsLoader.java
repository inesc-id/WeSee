package wesee.vizsrv.graph.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wesee.vizsrv.repository.HibernateLoader;
import wesee.vizsrv.repository.IRepositoryLoader;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.Host;
import wesee.vizsrv.graph.models.LinksAndNodes;
import wesee.vizsrv.graph.models.elemental.Link;
import wesee.vizsrv.graph.models.elemental.Node;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;
import wesee.vizsrv.repository.repository.ConnectionRepository;
import wesee.vizsrv.repository.repository.DataSourceRepository;
import wesee.vizsrv.repository.repository.HostRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ConnectionsLoader {
    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private ConnectionOccurrenceRepository connectionOccurrenceRepository;

    public static Link fillLinkData(Connection connection)
    {
        Link link = new Link();
        link.sourceId = connection.sourceHost.id;
        link.destinationId = connection.destinationHost.id;
        link.dataSourceId = connection.dataSource.id;
        link.id = connection.id;
        link.messagesCount = connection.connectionOccurrences.size();
        return link;
    }

    public static Node fillNodeData(Host host)
    {
        Node node = new Node();
        node.id = host.id;
        node.calls = 0;
        node.ip = host.ip;
        node.dns = host.dns;
        node.dataSourceId = host.dataSource.id;
        return node;
    }


    public LinksAndNodes getConnections(long[] sourceIds, long fromDate, long toDate)
    {
        IRepositoryLoader loader = new HibernateLoader(dataSourceRepository, connectionRepository,
                connectionOccurrenceRepository);
        Connection[] dbConnections = loader.getConnections(sourceIds, fromDate, toDate);
        HashMap<Long, Node> nodeMap = new HashMap<>(); // id, entity
        List<Link> linkList = new ArrayList<>();
        for (Connection dbConnection : dbConnections) {
            Link link = createLinkAndUpdateNodes(dbConnection, nodeMap, fromDate, toDate);
            link.lastMessage =
                   ConnectionMessagesLoader.fromConnectionOccurrences(
                       connectionOccurrenceRepository.findFirstByConnectionAndTimeMsBeforeOrderByTimeMsDesc(
                               dbConnection, toDate),
                       dbConnection.id
                   );
            linkList.add(link);
        }
        return new LinksAndNodes(linkList, nodeMap.values());
    }

    private Link createLinkAndUpdateNodes(Connection connection, HashMap<Long, Node> outNodes, long fromDate,
                                          long toDate)
    {
        IRepositoryLoader loader = new HibernateLoader(dataSourceRepository, connectionRepository,
                connectionOccurrenceRepository);
        Link link = fillLinkData(connection);
        link.messagesCount = loader.getMessagesNumber(connection.id, fromDate, toDate);
        addOrIncrementNode(outNodes, connection.sourceHost, link.messagesCount);
        addOrIncrementNode(outNodes, connection.destinationHost, link.messagesCount);
        return link;
    }

    private void addOrIncrementNode(HashMap<Long, Node> outNodes, Host host, int messagesCount)
    {
        if (!outNodes.containsKey(host.id))
        {
            outNodes.put(host.id, fillNodeData(host));
        }
        outNodes.get(host.id).calls+= messagesCount;
    }

}
