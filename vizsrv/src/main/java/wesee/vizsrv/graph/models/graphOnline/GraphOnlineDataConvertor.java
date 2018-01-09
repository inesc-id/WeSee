package wesee.vizsrv.graph.models.graphOnline;

import wesee.vizsrv.graph.models.GraphOnlineData;
import wesee.vizsrv.graph.models.elemental.Link;
import wesee.vizsrv.graph.models.elemental.LinkMessage;
import wesee.vizsrv.graph.models.elemental.Node;
import wesee.vizsrv.graph.statistics.ConnectionMessagesLoader;
import wesee.vizsrv.graph.statistics.ConnectionsLoader;
import wesee.vizsrv.graph.statistics.DataSourcesLoader;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class GraphOnlineDataConvertor {
    public static GraphOnlineData fromRepositorySaveResult(RepositorySaveResult repositorySaveResult)
    {
        GraphOnlineData graphOnlineData = new GraphOnlineData();
        repositorySaveResult.saveStateMap.forEach((key, value) -> convertDataBaseObject(key, value, graphOnlineData));
        linkMessagesToLinks(graphOnlineData);
        setHostCalls(graphOnlineData);
        return graphOnlineData;
    }

    private static void linkMessagesToLinks(GraphOnlineData graphOnlineData)
    {
        graphOnlineData.links.forEach(link -> {
            link.messagesCount = (int)graphOnlineData.messages.stream()
                    .filter(linkMessage -> linkMessage.connectionId == link.id)
                    .count();
            LinkMessage lastMessage = graphOnlineData.messages.stream()
                    .filter(linkMessage -> linkMessage.connectionId == link.id)
                    .reduce((o1, o2) -> o1.dateMs >= o2.dateMs? o1 : o2).get();
            link.lastMessage = lastMessage;
        });
    }

    private static void setHostCalls(GraphOnlineData graphOnlineData)
    {
        graphOnlineData.nodes.forEach(node -> {
            node.calls =  graphOnlineData.links.stream()
                    .filter(link -> link.destinationId == node.id || link.sourceId == node.id)
                    .map(link -> link.messagesCount)
                    .reduce((messages1, messages2) -> messages1 + messages2).get();
        });
    }

    private static void convertDataBaseObject(Object object, RepositorySaveResult.SaveStateEnum saveStateEnum,
                                              GraphOnlineData graphOnlineData)
    {
        if (object instanceof DataSource)
        {
            wesee.vizsrv.graph.models.DataSource graphDataSource =
                    DataSourcesLoader.fromDbDataSource((DataSource) object);
            graphOnlineData.dataSources.add(graphDataSource);
            graphOnlineData.dataSourcesStates.put(graphDataSource.id, saveStateEnumToString(saveStateEnum));
        }
        else if (object instanceof Connection)
        {
            Link link = ConnectionsLoader.fillLinkData((Connection) object);
            graphOnlineData.links.add(link);
            graphOnlineData.linkStates.put(link.id, saveStateEnumToString(saveStateEnum));
        }
        else if (object instanceof Host)
        {
            Node node = ConnectionsLoader.fillNodeData((Host) object);
            graphOnlineData.nodes.add(node);
            graphOnlineData.nodeStates.put(node.id, saveStateEnumToString(saveStateEnum));
        }
        else if (object instanceof ConnectionOccurrence)
        {
            LinkMessage message =
                    ConnectionMessagesLoader.fromConnectionOccurrences((ConnectionOccurrence) object, null);
            graphOnlineData.messages.add(message);
            graphOnlineData.messageStates.put(message.id, saveStateEnumToString(saveStateEnum));
        }
    }

    public static String saveStateEnumToString(RepositorySaveResult.SaveStateEnum saveStateEnum)
    {
        switch (saveStateEnum)
        {
            case NEW:
                return "new";
            case UNCHANGED:
                return "unchanged";
            case CHANGED:
                return "changed";
            case DELETED:
                return "deleted";
        }
        return null;
    }

}
