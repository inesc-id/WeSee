package datasource;

import datasource.model.ConnectionRecord;
import datasource.model.User;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public interface IInterceptionDataProvider
{
    void addRecord(ConnectionRecord record);
    List<ConnectionRecord> getRecords(Date fromTime, Date untilTime);
    List<ConnectionRecord> getRecords();

    boolean deleteRecord(int id);
    boolean deleteUser(int id);
}
