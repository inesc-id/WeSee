package datasource.db.sqlite;

import datasource.IInterceptionDataProvider;
import datasource.model.ConnectionRecord;

import java.util.Date;
import java.util.List;

public class SqlLiteInterceptionDataProvider implements IInterceptionDataProvider
{
    @Override
    public void AddRecord(ConnectionRecord record) {

    }

    @Override
    public List<ConnectionRecord> GetRecords(Date fromTime, Date untilTime) {
        return null;
    }
}
