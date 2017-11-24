package test;

import example.ws.cli.listener.storage.TimedStorage;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TimedStorageTest extends Assert {
    @Test
    public void testSameRecordInput() throws InterruptedException {
        String ip1 = "122.13.15.1";
        String ip2 = "201.233.109.99";
        TimedStorage storage = new TimedStorage(100);
        storage.putNewConnection(ip1, ip2);
        assertNull(storage.putNewConnection(ip1, ip2));
        TimeUnit.MILLISECONDS.sleep(100);
        storage.clearOldConnections();
        assertNotNull(storage.putNewConnection(ip1, ip2));
    }
}
