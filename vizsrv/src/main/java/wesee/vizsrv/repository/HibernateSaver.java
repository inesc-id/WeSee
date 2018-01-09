package wesee.vizsrv.repository;

import interception.models.Connection;
import interception.models.ConnectionsEntity;
import interception.models.connection_models.ConnectionOccurrence;
import interception.models.connection_models.Host;
import org.hibernate.LazyInitializationException;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.hibernate.save.*;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;
import wesee.vizsrv.repository.repository.ConnectionRepository;
import wesee.vizsrv.repository.repository.DataSourceRepository;
import wesee.vizsrv.repository.repository.HostRepository;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.util.HashMap;

public class HibernateSaver{
    public HibernateSaver(DataSourceRepository dataSourceRepository,
                          HostRepository hostRepository,
                          ConnectionRepository connectionRepository,
                          ConnectionOccurrenceRepository connectionOccurrenceRepository) {
        this.dataSourceRepository = dataSourceRepository;
        this.hostRepository = hostRepository;
        this.connectionRepository = connectionRepository;
        this.connectionOccurrenceRepository = connectionOccurrenceRepository;
    }

    private DataSourceRepository dataSourceRepository;
    private HostRepository hostRepository;
    private ConnectionRepository connectionRepository;
    private ConnectionOccurrenceRepository connectionOccurrenceRepository;

    public RepositorySaveResult save(ConnectionsEntity connectionsEntity) {
        RepositorySaveResult saveResult = new RepositorySaveResult();
        saveResult.saveStateMap = new HashMap<>();
        saveDataSource(saveResult, connectionsEntity);
        NotifierRepository.getSingleRepository().notify(saveResult);
        return saveResult;
    }

    private RepositorySaveResult saveDataSource(RepositorySaveResult saveResult,
                                                ConnectionsEntity connectionsEntity)
    {
        DataSourceSaver dataSourceSaver = new DataSourceSaver(dataSourceRepository);
        try
        {
            EntitySaveResult<DataSource> dataSourceSaveResult =
                    dataSourceSaver.findOrCreateDataSaver(connectionsEntity.sourceId);
            saveResult.saveStateMap.put(dataSourceSaveResult.entityResult, dataSourceSaveResult.state);
            saveResult.savedEntities = dataSourceSaveResult.entityResult;
            HashMap<String, wesee.vizsrv.repository.entities.Host> hostHashMap = new HashMap<>();
            saveHosts(saveResult, connectionsEntity, hostHashMap);
            saveConnections(saveResult, connectionsEntity, hostHashMap);
            return saveResult;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    private RepositorySaveResult saveHosts(RepositorySaveResult saveResult,
                                           ConnectionsEntity connectionsEntity,
                                           HashMap<String, wesee.vizsrv.repository.entities.Host> outHostMap)
    {
        HostSaver hostSaver = new HostSaver(hostRepository);
        HashMap<String, Host> hosts = extractAllHosts(connectionsEntity);

        for (Host host : hosts.values()) {
            EntitySaveResult<wesee.vizsrv.repository.entities.Host> dbResult = hostSaver.findOrCreateHost(saveResult.savedEntities, host);
            saveResult.saveStateMap.put(dbResult.entityResult, dbResult.state);
            try{
                saveResult.savedEntities.hosts.add(dbResult.entityResult);
            }
            catch (LazyInitializationException e)
            {
                if (!e.getMessage().contains("wesee.vizsrv.repository.entities.DataSource.hosts"))
                    throw e;
            }

            outHostMap.put(dbResult.entityResult.ip, dbResult.entityResult);
        }
        return saveResult;
    }

    private HashMap<String, Host> extractAllHosts(ConnectionsEntity connectionsEntity)
    {
        HashMap<String, Host> hostHashMap = new HashMap<>();
        for (Connection connection : connectionsEntity.connections) {
            hostHashMap.put(connection.source.ip, connection.source);
            hostHashMap.put(connection.destination.ip, connection.destination);
        }
        return hostHashMap;
    }


    private RepositorySaveResult saveConnections(RepositorySaveResult saveResult, ConnectionsEntity connectionsEntity,
                                                 HashMap<String, wesee.vizsrv.repository.entities.Host> hostsIpMap)  {
        ConnectionSaver connectionSaver = new ConnectionSaver(connectionRepository);
        for (Connection connection : connectionsEntity.connections) {
            EntitySaveResult<wesee.vizsrv.repository.entities.Connection> dbConnectionSaveResult =
                    connectionSaver.findOrCreateConnection(connection, saveResult.savedEntities,
                        hostsIpMap.get(connection.source.ip), hostsIpMap.get(connection.destination.ip));
            saveResult.saveStateMap.put(dbConnectionSaveResult.entityResult, dbConnectionSaveResult.state);
            saveResult.savedEntities.connections.add(dbConnectionSaveResult.entityResult);

            for (ConnectionOccurrence connectionOccurrence : connection.occurrences) {
                saveConnectionOccurrence(saveResult, connectionOccurrence, dbConnectionSaveResult.entityResult);
            }
        }
        return saveResult;
    }

    private RepositorySaveResult saveConnectionOccurrence(RepositorySaveResult saveResult,
        ConnectionOccurrence interceptionModel, wesee.vizsrv.repository.entities.Connection connection)  {
        ConnectionOccurrenceSaver messageSaver = new ConnectionOccurrenceSaver(connectionOccurrenceRepository);
        EntitySaveResult<wesee.vizsrv.repository.entities.ConnectionOccurrence> saveDbResult =
                messageSaver.findOrCreateConnectionOccurrence(interceptionModel, connection);
        saveResult.saveStateMap.put(saveDbResult.entityResult, saveDbResult.state);
        connection.connectionOccurrences.add(saveDbResult.entityResult);
        return saveResult;
    }

}
