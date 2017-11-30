package interception.models;

import interception.models.connection_models.ConnectionOccurrence;
import interception.models.connection_models.Host;

public class Connection {
    public Host source;
    public Host destination;

    public ConnectionOccurrence[] occurrences;
}
