package repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ConnectionOpener
{
    /**
     * Opens a connection
     * !! don`t forget to close it after usage
     * @return
     */
    public static Session getOpenedSession()
    {
        Configuration configuration = new Configuration().configure();
        SessionFactory sessions = configuration.buildSessionFactory();
        return sessions.openSession();
    }
}
