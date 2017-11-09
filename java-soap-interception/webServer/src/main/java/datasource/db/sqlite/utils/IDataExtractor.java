package datasource.db.sqlite.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDataExtractor<T> {
    T readDataFromStatement(ResultSet resultSet) throws SQLException;
}
