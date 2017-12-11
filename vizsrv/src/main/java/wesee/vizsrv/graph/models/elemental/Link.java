package wesee.vizsrv.graph.models.elemental;

public class Link extends AbstractGraphObject {
    public long sourceId;
    public long destinationId;
    public int messagesCount;
    public LinkMessage lastMessage;
}
