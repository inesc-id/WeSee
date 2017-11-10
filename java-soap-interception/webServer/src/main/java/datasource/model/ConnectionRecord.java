package datasource.model;

import datasource.db.sqlite.utils.DateConvertor;

import java.util.Date;

public class ConnectionRecord {

    public ConnectionRecord(User fromUser, User toUser)
    {
        setSources(fromUser, toUser);
        this.datetime = new Date();
    }

    public ConnectionRecord(User fromUser, User toUser, Date datetime)
    {
        setSources(fromUser, toUser);
        this.datetime = datetime;
    }

    public int id;
    public Date datetime;
    public User fromUser;
    public User toUser;

    private void setSources(User fromUser, User toUser)
    {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public boolean equals(ConnectionRecord otherRecord)
    {
        if (!(fromUser.equals(otherRecord.fromUser) && toUser.equals(otherRecord.toUser)))
            return false;
        return DateConvertor.isNearlySame(datetime, otherRecord.datetime);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ConnectionRecord)
            return equals((ConnectionRecord) obj);
        return false;
    }


}
