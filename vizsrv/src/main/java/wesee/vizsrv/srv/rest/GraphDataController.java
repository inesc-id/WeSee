package wesee.vizsrv.srv.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import wesee.vizsrv.graph.models.DataSource;
import wesee.vizsrv.graph.models.LinksAndNodes;
import wesee.vizsrv.graph.models.elemental.LinkMessage;
import wesee.vizsrv.graph.statistics.ConnectionMessagesLoader;
import wesee.vizsrv.graph.statistics.ConnectionsLoader;
import wesee.vizsrv.graph.statistics.DataSourcesLoader;

import java.net.ConnectException;

@RestController()
@RequestMapping("/graph")
public class GraphDataController {
    @Autowired
    private ConnectionsLoader connectionsLoader;
    @Autowired
    private DataSourcesLoader dataSourcesLoader;
    @Autowired
    private ConnectionMessagesLoader messagesLoader;

    @RequestMapping(value = "/connections", method = RequestMethod.GET)
    public LinksAndNodes getGraphData(long[] dataSourceIds, long fromDate, long toDate) throws ConnectException {
        return connectionsLoader.getConnections(dataSourceIds, fromDate, toDate);
    }

    @RequestMapping(value = "/dataSources", method = RequestMethod.GET)
    public DataSource[] getDataSources()
    {
        return dataSourcesLoader.loadDataSources();
    }

    @RequestMapping(value = "/connectionOccurrences", method = RequestMethod.GET)
    public LinkMessage[] getConnectionOccurrences(long connectionId, long fromDate, long toDate)
    {
        return messagesLoader.getConnectionMessages(connectionId, fromDate, toDate);
    }

}
