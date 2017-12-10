package wesee.vizsrv.repository.repository;

import org.springframework.data.repository.CrudRepository;
import wesee.vizsrv.repository.entities.DataSource;

import java.util.List;

public interface DataSourceRepository extends CrudRepository<DataSource, Long> {
    DataSource findFirstByName(String name);
}
