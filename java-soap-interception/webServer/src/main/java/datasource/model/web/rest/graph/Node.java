package datasource.model.web.rest.graph;

public class Node
{
    public Node(String id, String text, int calls) {
        this.id = id;
        this.text = text;
        this.calls = calls;
    }

    public String id;
    public String text;
    public int calls;
}
