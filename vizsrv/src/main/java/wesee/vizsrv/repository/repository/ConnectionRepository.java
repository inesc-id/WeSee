package wesee.vizsrv.repository.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;

import java.util.Collection;

public interface ConnectionRepository extends CrudRepository<Connection, Long> {
    Connection findFirstBySourceHostAndDestinationHostAndDataSource(Host sourceHost, Host destinationHost,
                                                                    DataSource dataSource);
    @Query(value="select distinct c from Connection c " +
            "join c.connectionOccurrences as message on message.timeMs between ?1 and ?2 " +
            "where c.dataSource.id in ?3")
    Iterable<Connection> findByBetweenTimeAndDataSource_IdIn(long fromDateMs, long toDateMs,
                                                             Collection<Long> dSourceIds);
}
