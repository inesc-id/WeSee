package datasource.db.sqlite.record;

import datasource.db.sqlite.utils.DateConvertor;
import datasource.db.sqlite.utils.IDataExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbConnectionRecordExtractor implements IDataExtractor<DbConnectionRecord> {
    @Override
    public DbConnectionRecord readDataFromStatement(ResultSet resultSet) throws SQLException {
        if (!resultSet.next())
            return null;

        return readRecord(resultSet);
    }

    public static DbConnectionRecord readRecord(ResultSet resultSet) throws SQLException {
        DbConnectionRecord record = new DbConnectionRecord();
        record.id = resultSet.getInt("id");
        record.datetime = DateConvertor.ToDate(resultSet.getLong("datetime"));
        record.fromUserId = resultSet.getInt("fromUserId");
        record.toUserId = resultSet.getInt("toUserId");
        return record;
    }
}
