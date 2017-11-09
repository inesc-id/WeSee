package datasource.db.sqlite.record;

import datasource.db.sqlite.utils.IDataExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListDbConnectionRecordExtractor implements IDataExtractor<List<DbConnectionRecord>> {
    @Override
    public List<DbConnectionRecord> readDataFromStatement(ResultSet resultSet) throws SQLException {
        List<DbConnectionRecord> records = new ArrayList<>();
        while(resultSet.next())
            records.add(DbConnectionRecordExtractor.readRecord(resultSet));
        return records;
    }
}
