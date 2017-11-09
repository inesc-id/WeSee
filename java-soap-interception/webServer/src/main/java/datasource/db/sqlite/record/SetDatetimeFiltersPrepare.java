package datasource.db.sqlite.record;

import datasource.db.sqlite.utils.DateConvertor;
import datasource.db.sqlite.utils.IStatementPrepare;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class SetDatetimeFiltersPrepare implements IStatementPrepare {
    private Date fromDate;
    private Date toDate;

    public SetDatetimeFiltersPrepare(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public PreparedStatement setupStatement(PreparedStatement statement) throws SQLException {
        statement.setLong(1, DateConvertor.toLongInt(fromDate));
        statement.setLong(2, DateConvertor.toLongInt(toDate));
        return statement;
    }
}
