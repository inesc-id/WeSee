package wesee.vizsrv.repository;


import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.repository.entities.DataSource;

public interface IRepositoryLoader {
    DataSource[] getDataSources();
    Connection[] getConnections(long[] dataSourceIds, long startDateMs, long endDateMs);
    int getMessagesNumber(long connectionId, long startDateMs, long endDateMs);
    ConnectionOccurrence[] getConnectionOccurrences(long connectionId, long startDateMs, long endDateMs);
}
