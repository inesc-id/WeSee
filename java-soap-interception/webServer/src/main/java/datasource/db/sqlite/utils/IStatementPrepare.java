package datasource.db.sqlite.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IStatementPrepare {
    PreparedStatement setupStatement(PreparedStatement statement) throws SQLException;
}
