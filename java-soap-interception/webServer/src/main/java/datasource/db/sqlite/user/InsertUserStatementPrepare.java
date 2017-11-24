package datasource.db.sqlite.user;

import datasource.db.sqlite.utils.IStatementPrepare;
import datasource.model.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUserStatementPrepare implements IStatementPrepare {
    private User user;

    public InsertUserStatementPrepare(User user) {
        this.user = user;
    }

    @Override
    public PreparedStatement setupStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, user.dns);
        statement.setString(2, user.ip);
        statement.setInt(3, user.isIpv4? 1 : 0);
        return statement;
    }
}
