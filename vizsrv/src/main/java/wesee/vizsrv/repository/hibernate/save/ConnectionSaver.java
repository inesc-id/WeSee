package wesee.vizsrv.repository.hibernate.save;

import org.springframework.dao.DataIntegrityViolationException;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;
import wesee.vizsrv.repository.repository.ConnectionRepository;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public class ConnectionSaver {
    private ConnectionRepository repo;

    public ConnectionSaver(ConnectionRepository repo) {
        this.repo = repo;
    }

    public EntitySaveResult<Connection> findOrCreateConnection(interception.models.Connection interceptionModel,
                                                               DataSource dataSource, Host sourceHost,
                                                               Host destinationHost)
    {
        Connection result = repo.findFirstBySourceHostAndDestinationHostAndDataSource(sourceHost, destinationHost,
                dataSource);
        RepositorySaveResult.SaveStateEnum saveStateEnum = RepositorySaveResult.SaveStateEnum.UNCHANGED;
        if (result == null)
        {
            result = new Connection();
            result.dataSource = dataSource;
            result.sourceHost = sourceHost;
            result.destinationHost = destinationHost;
            try
            {
                result = repo.save(result);
                saveStateEnum = RepositorySaveResult.SaveStateEnum.NEW;
            }
            catch (DataIntegrityViolationException e) {
                result = repo.findFirstBySourceHostAndDestinationHostAndDataSource(sourceHost, destinationHost,
                        dataSource);
                e.printStackTrace();
            }
        }

        return new EntitySaveResult(result, saveStateEnum);
    }
}
