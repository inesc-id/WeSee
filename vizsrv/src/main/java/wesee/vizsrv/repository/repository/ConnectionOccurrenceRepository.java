package wesee.vizsrv.repository.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;

import java.util.Collection;

public interface ConnectionOccurrenceRepository extends CrudRepository<ConnectionOccurrence, Long> {
    ConnectionOccurrence findFirstByConnectionAndTimeMs(Connection connection, long timeMs);
    ConnectionOccurrence findFirstByConnectionAndTimeMsBeforeOrderByTimeMsDesc(Connection connection, long timeMs);
    Iterable<ConnectionOccurrence> findByTimeMsBetweenAndConnection_Id(long afterTimeMs, long beforeTimeMs,
                                                                       long connectionId);
    int countByTimeMsBetweenAndConnection_Id(long afterTimeMs, long beforeTimeMs, long connectionId);
}
