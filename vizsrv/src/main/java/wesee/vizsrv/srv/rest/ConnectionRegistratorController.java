package wesee.vizsrv.srv.rest;

import interception.models.ConnectionsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wesee.vizsrv.repository.HibernateSaver;
import wesee.vizsrv.repository.entities.Connection;
import wesee.vizsrv.repository.entities.ConnectionOccurrence;
import wesee.vizsrv.repository.entities.DataSource;
import wesee.vizsrv.repository.entities.Host;
import wesee.vizsrv.repository.repository.ConnectionOccurrenceRepository;
import wesee.vizsrv.repository.repository.ConnectionRepository;
import wesee.vizsrv.repository.repository.DataSourceRepository;
import wesee.vizsrv.repository.repository.HostRepository;

import java.net.ConnectException;
import java.util.List;

@RestController
public class ConnectionRegistratorController{
    @Autowired
    private DataSourceRepository dataSourceRepository;
    @Autowired
    private HostRepository hostRepository;
    @Autowired
    private ConnectionRepository connectionRepository;
    @Autowired
    private ConnectionOccurrenceRepository connectionOccurrenceRepository;
    @RequestMapping(value = "/registerConnections", method = RequestMethod.POST)
    public void registerConnections(@RequestBody ConnectionsEntity connectionsEntity) throws ConnectException {
        HibernateSaver hibernateSaver = new HibernateSaver(dataSourceRepository, hostRepository, connectionRepository,
                connectionOccurrenceRepository);
        hibernateSaver.save(connectionsEntity);
    }

}
