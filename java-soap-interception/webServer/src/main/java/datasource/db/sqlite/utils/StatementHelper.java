package datasource.db.sqlite.utils;

import datasource.model.ConnectionRecord;

import java.sql.*;
import java.util.List;

public class StatementHelper {

    public static boolean executeWithoutResult(IStatementPrepare statementPrepare, String connectionString,
                                               String statementTemplate)
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(connectionString);
            PreparedStatement statement = prepareStatement(connection, statementTemplate, statementPrepare);
            statement.execute();
            connection.close();
            return true;
        } catch (SQLException e) {
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            e.printStackTrace();
            return false;
        }
    }

    public static <T> T executeWithResult(IStatementPrepare statementPrepare, String connectionString,
                                        String statementTemplate, IDataExtractor<T> dataExtractor)
    {
        Connection connection = null;
        try
        {
            connection = DriverManager.getConnection(connectionString);
            PreparedStatement statement = prepareStatement(connection, statementTemplate, statementPrepare);
            ResultSet set = statement.executeQuery();
            T result = dataExtractor.readDataFromStatement(set);
            connection.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null)
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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
