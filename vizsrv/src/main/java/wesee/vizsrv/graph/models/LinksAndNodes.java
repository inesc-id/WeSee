package wesee.vizsrv.graph.models;

import wesee.vizsrv.graph.models.elemental.Link;
import wesee.vizsrv.graph.models.elemental.Node;

import java.util.Collection;

public class LinksAndNodes {
    public LinksAndNodes()
    {
    }

    public LinksAndNodes(Collection<Link> links, Collection<Node> nodes) {
        this.links = new Link[links.size()];
        links.toArray(this.links);
        this.nodes = new Node[nodes.size()];
        nodes.toArray(this.nodes);
    }

    public Link[] links;
    public Node[] nodes;
}
