package datasource.db.sqlite.record;

import datasource.db.sqlite.utils.DateConvertor;
import datasource.db.sqlite.utils.IStatementPrepare;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetDbConnectionRecordPrepare implements IStatementPrepare {
    private DbConnectionRecord record;

    public SetDbConnectionRecordPrepare(DbConnectionRecord record) {
        this.record = record;
    }

    @Override
    public PreparedStatement setupStatement(PreparedStatement statement) throws SQLException {
        statement.setLong(1, DateConvertor.toLongInt(record.datetime));
        statement.setInt(2, record.fromUserId);
        statement.setInt(3, record.toUserId);
        return statement;
    }
}
