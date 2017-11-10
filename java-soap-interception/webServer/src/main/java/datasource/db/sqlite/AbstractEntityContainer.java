package datasource.db.sqlite;

import java.sql.Connection;

abstract class AbstractEntityContainer
{
    protected Connection connection;

    public AbstractEntityContainer(Connection connection)
    {
        this.connection = connection;
    }
}
