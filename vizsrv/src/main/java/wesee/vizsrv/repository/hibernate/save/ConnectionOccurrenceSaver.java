package wesee.vizsrv.repository.hibernate.save;

import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.util.Arrays;

public class ConnectionOccurrenceSaver {
    private ConnectionOccurrenceRepository repo;

    public ConnectionOccurrenceSaver(ConnectionOccurrenceRepository repo) {
        this.repo = repo;
    }

    public EntitySaveResult<ConnectionOccurrence> findOrCreateConnectionOccurrence(
              interception.models.connection_models.ConnectionOccurrence interceptionModel, Connection connection)
    {
        ConnectionOccurrence result = repo.findFirstByConnectionAndTimeMs(connection,
                interceptionModel.callTimeMs);
        RepositorySaveResult.SaveStateEnum saveStateEnum = RepositorySaveResult.SaveStateEnum.UNCHANGED;
        if (result == null)
        {
            result = new ConnectionOccurrence();
            result.connection = connection;
            result.setMessage(interceptionModel.message);
            if (result.message.length > 16777215)
                result.message = Arrays.copyOf(result.message, 16777215);
            result.timeMs = interceptionModel.callTimeMs;
            result = repo.save(result);
            saveStateEnum = RepositorySaveResult.SaveStateEnum.NEW;
        }
        else
        {
            if (result.timeMs != interceptionModel.callTimeMs || result.getMessage() != interceptionModel.message)
            {
                result = repo.save(result);
                saveStateEnum = RepositorySaveResult.SaveStateEnum.CHANGED;
            }
        }
        return new EntitySaveResult(result, saveStateEnum);
    }
}
