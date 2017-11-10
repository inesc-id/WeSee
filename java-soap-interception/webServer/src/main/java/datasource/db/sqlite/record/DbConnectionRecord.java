package datasource.db.sqlite.record;

import datasource.model.ConnectionRecord;
import datasource.model.User;

import java.util.Date;

public class DbConnectionRecord {

    public DbConnectionRecord(int id, Date datetime, int fromUser, int toUser) {
        this.id = id;
        this.datetime = datetime;
        this.fromUserId = fromUser;
        this.toUserId = toUser;
    }

    public int id;
    public Date datetime;
    public int fromUserId;
    public int toUserId;

    public DbConnectionRecord() {

    }

    public ConnectionRecord toConnectionRecord(User fromUser, User toUser)
    {
        ConnectionRecord record = new ConnectionRecord(fromUser, toUser, datetime);
        record.id = id;
        return record;
    }

    public static DbConnectionRecord fromConnectionRecord(ConnectionRecord connectionRecord)
    {
        return new DbConnectionRecord(connectionRecord.id, connectionRecord.datetime, connectionRecord.fromUser.id,
                connectionRecord.toUser.id);
    }
}
