package utils.model.conversion.graph;

import datasource.model.ConnectionRecord;
import datasource.model.User;
import datasource.model.web.rest.graph.Link;
import datasource.model.web.rest.graph.Node;

import java.util.HashMap;

public class GraphData {
    public Link[] links;
    public Node[] nodes;

    public static GraphData fromConnectionRecords(ConnectionRecord[] connectionRecords) {
        GraphData graphData = new GraphData();
        HashMap<Integer, Node> hosts = new HashMap<>();
        HashMap<LinkConnection, Link> links = new HashMap<>();
        for (ConnectionRecord record : connectionRecords) {
            extractDataFromConnectionRecord(record, hosts, links);
        }

        graphData.links = links.values().toArray(new Link[links.size()]);
        graphData.nodes = hosts.values().toArray(new Node[hosts.size()]);
        return graphData;
    }

    private static void extractDataFromConnectionRecord(ConnectionRecord record, HashMap<Integer, Node> hosts,
                                                 HashMap<LinkConnection, Link> links)

    {
        if (record.fromUser == null || record.toUser == null)
            return;
        editHostList(record, hosts);
        LinkConnection linkId = new LinkConnection(record.fromUser.id, record.toUser.id);
        if (links.containsKey(linkId))
            links.get(linkId).calls++;
        else
            links.put(linkId,
                    new Link(Integer.toString(record.fromUser.id), Integer.toString(record.toUser.id),
                            INITIAL_CALL_TIMES));
    }

    /**
     * Processes the record and adds a new host if detects or just increases the counter in host struct
     * @param record record to extract data
     * @param hosts current hosts collection
     */
    private static void editHostList(ConnectionRecord record, HashMap<Integer, Node> hosts)
    {
        addOrIncrementNode(record.fromUser, hosts);
        addOrIncrementNode(record.toUser, hosts);
    }

    private static void addOrIncrementNode(User user, HashMap<Integer, Node> hosts)
    {
        if (hosts.containsKey(user.id))
            hosts.get(user.id).calls++;
        else hosts.put(user.id, fromUser(user));
    }

    private static final int INITIAL_CALL_TIMES = 1;
    private static Node fromUser(User user)
    {
        String userInfo = user.ip;
        if (user.dns != null)
            if (!user.dns.isEmpty())
                userInfo += "\n" + user.dns;
        Node node = new Node(Integer.toString(user.id), userInfo, INITIAL_CALL_TIMES);
        return node;
    }


}
