package repository.entities;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static repository.ConnectionOpener.getOpenedSession;

public class RepositoryTest {
    @Test
    public void testConnected()
    {
        Session session = getOpenedSession();
        Assert.assertTrue(session.isOpen());
        session.close();
    }
}
