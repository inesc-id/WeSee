package datasource.db.sqlite.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetIdStatementDelegate implements IStatementPrepare {
    private int id;

    public SetIdStatementDelegate(int id)
    {
        this.id = id;
    }

    @Override
    public PreparedStatement setupStatement(PreparedStatement statement) throws SQLException {
        statement.setInt(1, id);
        return statement;
    }
}
