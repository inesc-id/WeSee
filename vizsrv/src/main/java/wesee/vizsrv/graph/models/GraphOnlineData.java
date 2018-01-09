package wesee.vizsrv.graph.models;

import wesee.vizsrv.graph.models.elemental.Link;
import wesee.vizsrv.graph.models.elemental.LinkMessage;
import wesee.vizsrv.graph.models.elemental.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GraphOnlineData {
   public Collection<DataSource> dataSources = new ArrayList<>();
   public Map<Long, String> dataSourcesStates = new HashMap<>();

    public Collection<Link> links = new ArrayList<>();
    public Map<Long, String> linkStates = new HashMap<>();

    public Collection<Node> nodes = new ArrayList<>();
    public Map<Long, String> nodeStates = new HashMap<>();

    public Collection<LinkMessage> messages = new ArrayList<>();
    public Map<Long, String> messageStates = new HashMap<>();
}
