package wesee.vizsrv.repository.repository;

import org.springframework.data.repository.CrudRepository;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;

public interface HostRepository extends CrudRepository<Host, Long> {
    Host findFirstByIpAndDataSource(String ip, DataSource dataSource);
}
