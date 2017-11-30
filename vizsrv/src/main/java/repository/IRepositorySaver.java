package repository;

import interception.models.ConnectionsEntity;
import repository.saveresult.RepositorySaveResult;

public interface IRepositorySaver {
    RepositorySaveResult save(ConnectionsEntity connectionsEntity);
}
