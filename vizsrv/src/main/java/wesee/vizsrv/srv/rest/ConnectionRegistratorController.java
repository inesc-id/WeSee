package wesee.vizsrv.srv.rest;

import interception.models.ConnectionsEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import interception.restclient.IConnectionRegistrator;

import java.net.ConnectException;

@RestController
public class ConnectionRegistratorController implements IConnectionRegistrator {
    @Override
    @RequestMapping(value = "/registerConnections", method = RequestMethod.POST)
    public void registerConnections(@RequestBody ConnectionsEntity connectionsEntity) throws ConnectException {
        System.out.print(String.format("packet received %s", connectionsEntity.sourceId));
    }

}
