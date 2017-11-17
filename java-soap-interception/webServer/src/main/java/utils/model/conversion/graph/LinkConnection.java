package utils.model.conversion.graph;

class LinkConnection
{
    private int node1Id;
    private int node2Id;

    public LinkConnection(int node1Id, int node2Id) {
        if (node1Id < node2Id)
        {
            this.node1Id = node1Id;
            this.node2Id = node2Id;
        }
        else
        {
            this.node1Id = node2Id;
            this.node2Id = node1Id;
        }

    }

    public int getNode1Id() {
        return node1Id;
    }

    public int getNode2Id() {
        return node2Id;
    }

    @Override
    public int hashCode() {
        return node1Id + node2Id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LinkConnection))
            return false;
        LinkConnection objLink = (LinkConnection)obj;
        return ((objLink.node1Id == node1Id) && (objLink.node2Id == node2Id)) ||
                ((objLink.node2Id == node1Id) && (objLink.node1Id == node2Id));
    }
}
