package datasource.db.sqlite;

abstract class AbstractEntityContainer
{
    protected String connectionString;

    public AbstractEntityContainer(String connectionString)
    {
        this.connectionString = connectionString;
    }
}
