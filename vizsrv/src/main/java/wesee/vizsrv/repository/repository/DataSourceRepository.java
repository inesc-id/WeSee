package wesee.vizsrv.repository.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import wesee.vizsrv.repository.entities.DataSource;

import java.util.List;

public interface DataSourceRepository extends CrudRepository<DataSource, Long> {
    DataSource findFirstByName(String name);

    @Query(value="select min(m.timeMs) from DataSource ds " +
            "join ds.connections c " +
            "join c.connectionOccurrences m " +
            "where ds.id = ?1")
    long getMinTimeMsByDataSourceId(long datasourceId);
    @Query(value="select max(m.timeMs) from DataSource ds " +
            "join ds.connections c " +
            "join c.connectionOccurrences m " +
            "where ds.id = ?1")
    long getMaxTimeMsByDataSourceId(long datasourceId);
}
