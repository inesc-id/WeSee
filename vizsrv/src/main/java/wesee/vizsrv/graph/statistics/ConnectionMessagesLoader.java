package wesee.vizsrv.graph.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wesee.vizsrv.repository.HibernateLoader;
import wesee.vizsrv.repository.IRepositoryLoader;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.graph.models.elemental.LinkMessage;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class ConnectionMessagesLoader {
    @Autowired
    private ConnectionOccurrenceRepository connectionOccurrenceRepository;

    public static LinkMessage fromConnectionOccurrences(ConnectionOccurrence dbModel, Long optionalConnectionId)
    {
        if (dbModel == null)
            return null;
        LinkMessage linkMessage = new LinkMessage();
        linkMessage.id = dbModel.id;
        linkMessage.dataSourceId = dbModel.connection.dataSource.id;
        linkMessage.connectionId = optionalConnectionId == null? dbModel.connection.id : optionalConnectionId;
        linkMessage.dateMs = dbModel.timeMs;
        linkMessage.message = dbModel.getMessage();
        return linkMessage;
    }

    public LinkMessage[] getConnectionMessages(long connectionId, long fromDateMs, long toDateMs)
    {
        IRepositoryLoader loader = new HibernateLoader(null, null, connectionOccurrenceRepository);
        ConnectionOccurrence[] occurrences =
                loader.getConnectionOccurrences(connectionId, fromDateMs, toDateMs);
        LinkMessage[] linkMessages = new LinkMessage[occurrences.length];
        Arrays.stream(occurrences)
                .map(connectionOccurrence -> fromConnectionOccurrences(connectionOccurrence, connectionId))
                .collect(Collectors.toList())
                .toArray(linkMessages);
        return linkMessages;
    }

}
