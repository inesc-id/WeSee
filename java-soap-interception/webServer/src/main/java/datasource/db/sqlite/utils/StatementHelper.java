package datasource.db.sqlite.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StatementHelper {

    public static boolean executeWithoutResult(IStatementPrepare statementPrepare, Connection connection,
                                               String statementTemplate)
    {
        try
        {
            PreparedStatement statement = prepareStatement(connection, statementTemplate, statementPrepare);
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T executeWithResult(IStatementPrepare statementPrepare, Connection connection,
                                        String statementTemplate, IDataExtractor<T> dataExtractor)
    {
        try
        {
            PreparedStatement statement = prepareStatement(connection, statementTemplate, statementPrepare);
            ResultSet set = statement.executeQuery();
            return dataExtractor.readDataFromStatement(set);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PreparedStatement prepareStatement(Connection connection, String statementTemplate,
                                                      IStatementPrepare statementPrepare) throws SQLException
    {
        PreparedStatement statement = connection.prepareStatement(statementTemplate);
        statement = statementPrepare.setupStatement(statement);
        return statement;
    }

}
