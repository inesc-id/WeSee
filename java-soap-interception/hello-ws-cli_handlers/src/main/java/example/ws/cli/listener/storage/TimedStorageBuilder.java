package example.ws.cli.listener.storage;

import example.ws.cli.listener.IConnectionsStorage;
import example.ws.cli.listener.IConnectionsStorageBuilder;

public class TimedStorageBuilder implements IConnectionsStorageBuilder {
    private long refreshMs;

    public TimedStorageBuilder(long refreshMs) {
        this.refreshMs = refreshMs;
    }

    @Override
    public IConnectionsStorage buildStorage() {
        return new TimedStorage(refreshMs);
    }
}
