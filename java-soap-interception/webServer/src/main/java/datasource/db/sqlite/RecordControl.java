package datasource.db.sqlite;

import datasource.db.sqlite.record.DbConnectionRecord;
import datasource.db.sqlite.record.ListDbConnectionRecordExtractor;
import datasource.db.sqlite.record.SetDatetimeFiltersPrepare;
import datasource.db.sqlite.record.SetDbConnectionRecordPrepare;
import datasource.db.sqlite.utils.SetIdStatementDelegate;
import datasource.db.sqlite.utils.StatementHelper;
import datasource.model.ConnectionRecord;
import datasource.model.User;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.util.*;

public class RecordControl extends AbstractEntityContainer {

    private UserControl userControl;

    public RecordControl(Connection connection, UserControl userControl)
    {
        super(connection);
        this.userControl = userControl;
    }



    private final String ADD_RECORD_TEMPLATE = "insert into connectionRecord(datetime, fromUserId, toUserId)\n" +
            "   values(?, ? , ?)";
    private final String SELECT_RECORD_TEMPLATE = "select id, datetime, fromUserId, toUserId " +
            "from connectionRecord";
    private final String SELECT_RECORD_DATE_FILTERED_TEMPLATE = SELECT_RECORD_TEMPLATE +
            " where datetime >= ? and datetime <= ?";
    private final String DELETE_RECORD_TEMPLATE = "delete from connectionRecord where id = ?";

    public void addRecord(ConnectionRecord record) {
        record.fromUser.id = createUserIfNotExist(record.fromUser);
        record.toUser.id = createUserIfNotExist(record.toUser);
        StatementHelper.executeWithoutResult(
                new SetDbConnectionRecordPrepare(DbConnectionRecord.fromConnectionRecord(record)),
                connection, ADD_RECORD_TEMPLATE);
    }

    private int createUserIfNotExist(User user)
    {
        User dbUserRecord = userControl.getUser(user.ip);
        if (dbUserRecord != null)
            return dbUserRecord.id;
        userControl.addUser(user);
        return userControl.getUser(user.ip).id;
    }


    public List<ConnectionRecord> getRecords(Date fromTime, Date untilTime) {
        List<DbConnectionRecord> dbRecords = StatementHelper.executeWithResult(
                new SetDatetimeFiltersPrepare(fromTime, untilTime),
                connection, SELECT_RECORD_DATE_FILTERED_TEMPLATE, new ListDbConnectionRecordExtractor());
        return restoreRecords(dbRecords);
    }

    public List<ConnectionRecord> getRecords() {
        List<DbConnectionRecord> dbRecords = StatementHelper.executeWithResult(statement -> { return statement; },
                connection, SELECT_RECORD_TEMPLATE, new ListDbConnectionRecordExtractor());
        return restoreRecords(dbRecords);
    }

    private List<ConnectionRecord> restoreRecords(List<DbConnectionRecord> dbRecords)
    {
        HashSet<Integer> uniqueUserIds = collectUniqueUserIds(dbRecords);
        HashMap<Integer, User> userHashMap = readUsers(uniqueUserIds);
        return matchUsersToIds(dbRecords, userHashMap);
    }

    private List<ConnectionRecord> matchUsersToIds(List<DbConnectionRecord> dbConnectionRecords,
                                                   HashMap<Integer, User> userMap)
    {
        List<ConnectionRecord> connectionRecords = new ArrayList<>();
        for (DbConnectionRecord dbConnectionRecord: dbConnectionRecords) {
            connectionRecords.add(dbConnectionRecord.toConnectionRecord(userMap.get(dbConnectionRecord.fromUserId),
                    userMap.get(dbConnectionRecord.toUserId)));
        }
        return connectionRecords;
    }

    private HashSet<Integer> collectUniqueUserIds(List<DbConnectionRecord> dbRecords)
    {
        HashSet<Integer> uniqueUserIds = new HashSet<>();
        for (DbConnectionRecord dbRecord : dbRecords) {
            uniqueUserIds.add(dbRecord.fromUserId);
            uniqueUserIds.add(dbRecord.toUserId);
        }
        return uniqueUserIds;
    }

    private HashMap<Integer, User> readUsers(HashSet<Integer> userIds)
    {
        HashMap<Integer, User> users = new HashMap<>();
        for (int userId : userIds) {
            users.put(userId, userControl.getUser(userId));
        }
        return users;
    }

    public boolean deleteRecord(int id) {
        return StatementHelper.executeWithoutResult(new SetIdStatementDelegate(id), connection, DELETE_RECORD_TEMPLATE);
    }
}
