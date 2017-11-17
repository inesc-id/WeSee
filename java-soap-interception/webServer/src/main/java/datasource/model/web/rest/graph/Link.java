package datasource.model.web.rest.graph;

public class Link {

    public Link(String source, String target, int calls) {
        this.source = source;
        this.target = target;
        this.calls = calls;
    }

    public String source;
    public String target;
    public int calls;
}
