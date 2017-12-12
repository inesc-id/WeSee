package wesee.vizsrv.repository;

import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;
import wesee.vizsrv.repository.repository.ConnectionRepository;
import wesee.vizsrv.repository.repository.DataSourceRepository;
import wesee.vizsrv.repository.repository.HostRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HibernateLoader implements IRepositoryLoader {
    public HibernateLoader(DataSourceRepository dataSourceRepository,
                           ConnectionRepository connectionRepository,
                           ConnectionOccurrenceRepository connectionOccurrenceRepository) {
        this.dataSourceRepository = dataSourceRepository;
        this.connectionRepository = connectionRepository;
        this.connectionOccurrenceRepository = connectionOccurrenceRepository;
    }

    private DataSourceRepository dataSourceRepository;
    private ConnectionRepository connectionRepository;
    private ConnectionOccurrenceRepository connectionOccurrenceRepository;
    @Override
    public DataSource[] getDataSources() {

        List<DataSource> dataSourceList = new ArrayList<>();
        dataSourceRepository.findAll().forEach(dataSourceList::add);
        DataSource[] dataSourceArray = new DataSource[dataSourceList.size()];
        dataSourceList.toArray(dataSourceArray);
        return dataSourceArray;
    }

    @Override
    public Connection[] getConnections(long[] dataSourceIds, long startDateMs, long endDateMs) {
        List<Connection> connectionList = new ArrayList<>();
        List<Long> dataSourceIdList = new ArrayList<>();
        Arrays.stream(dataSourceIds).forEach(dataSourceIdList::add);
        connectionRepository.findByBetweenTimeAndDataSource_IdIn(startDateMs, endDateMs, dataSourceIdList)
                .forEach(connectionList::add);
        Connection[] connectionArray = new Connection[connectionList.size()];
        connectionList.toArray(connectionArray);
        return connectionArray;
    }

    @Override
    public ConnectionOccurrence[] getConnectionOccurrences(long connectionId, long startDateMs, long endDateMs) {
        List<ConnectionOccurrence> messageList = new ArrayList<>();
        connectionOccurrenceRepository.findByTimeMsBetweenAndConnection_Id(startDateMs, endDateMs, connectionId)
                .forEach(messageList::add);
        ConnectionOccurrence[] messageArray = new ConnectionOccurrence[messageList.size()];
        messageList.toArray(messageArray);
        return messageArray;
    }

    @Override
    public int getMessagesNumber(long connectionId, long startDateMs, long endDateMs) {
        return connectionOccurrenceRepository.countByTimeMsBetweenAndConnection_Id(startDateMs, endDateMs, connectionId);
    }


}
