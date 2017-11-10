package datasource;

import datasource.db.SqlLiteInterceptionDataProvider;
import datasource.model.ConnectionRecord;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Component
public class ConnectionDataRepository {

    private static IInterceptionDataProvider provider;

    public static IInterceptionDataProvider getProvider()
    {
        if (provider == null)
            try {
                provider = new SqlLiteInterceptionDataProvider();
            } catch (SQLException e) {
                e.printStackTrace();
                System.exit(e.getErrorCode());
            }
        return provider;
    }
}
