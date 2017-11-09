package datasource.db.sqlite.user;

import datasource.db.sqlite.utils.IStatementPrepare;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetIpStatementPrepare implements IStatementPrepare {
    private String ip;

    public SetIpStatementPrepare(String ip) {
        this.ip = ip;
    }


    @Override
    public PreparedStatement setupStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, ip);
        return statement;
    }
}
