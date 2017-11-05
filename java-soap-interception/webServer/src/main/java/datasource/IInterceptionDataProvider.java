package datasource;

import datasource.model.ConnectionRecord;
import datasource.model.User;

import java.util.Date;
import java.util.List;

public interface IInterceptionDataProvider
{
    void AddRecord(ConnectionRecord record);
    List<ConnectionRecord> GetRecords(Date fromTime, Date untilTime);
}
