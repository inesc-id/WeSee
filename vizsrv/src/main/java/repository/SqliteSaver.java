package repository;

import interception.models.ConnectionsEntity;
import org.hibernate.Session;
import repository.entities.DataSource;
import repository.saveresult.RepositorySaveResult;

import java.util.HashMap;

public class SqliteSaver implements IRepositorySaver {

    @Override
    public RepositorySaveResult save(ConnectionsEntity connectionsEntity) {
        RepositorySaveResult saveResult = new RepositorySaveResult();
        saveResult.saveStateMap = new HashMap<>();
        Session session = ConnectionOpener.getOpenedSession();
    }

    private RepositorySaveResult saveDataSource(Session session, RepositorySaveResult saveResult,
                                                ConnectionsEntity connectionsEntity)
    {
        DataSource dataSource = new DataSource();

    }
}
