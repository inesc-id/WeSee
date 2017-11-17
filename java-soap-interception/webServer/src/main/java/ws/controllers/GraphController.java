package ws.controllers;

import datasource.ConnectionDataRepository;
import datasource.model.ConnectionRecord;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.model.conversion.graph.GraphData;

import java.util.List;

@RestController
public class GraphController {

    @GetMapping("/getGraphData")
    public GraphData getGraphData()
    {
        List<ConnectionRecord> connectionRecords = ConnectionDataRepository.getProvider().getRecords();
        ConnectionRecord[] recordsArray = new ConnectionRecord[connectionRecords.size()];
        recordsArray = connectionRecords.toArray(recordsArray);
        return GraphData.fromConnectionRecords(recordsArray);
    }
}
