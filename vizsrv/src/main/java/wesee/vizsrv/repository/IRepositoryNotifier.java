package wesee.vizsrv.repository;

import interception.models.ConnectionsEntity;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public interface IRepositoryNotifier {
    RepositorySaveResult save(ConnectionsEntity connectionsEntity);
}
