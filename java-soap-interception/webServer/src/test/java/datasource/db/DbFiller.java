package datasource.db;

import datasource.IInterceptionDataProvider;
import datasource.db.randomizer.HostGenerator;
import datasource.model.ConnectionRecord;
import datasource.model.User;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DbFiller {
    public List<ConnectionRecord> connectionRecords = new ArrayList<>();
    private static final int RECORDS_COUNT = 100;
    @Before
    public void generateInsertionData()
    {
        for (int i = 0; i < RECORDS_COUNT; i++) {
            User userSource = HostGenerator.generateHost();
            User userDestination = HostGenerator.generateHost();
            ConnectionRecord record = new ConnectionRecord(userSource, userDestination);
            connectionRecords.add(record);
        }

    }
    @Test
    public void generateRandomData() throws SQLException {
        IInterceptionDataProvider dataProvider = new SqlLiteInterceptionDataProvider();
        for (ConnectionRecord record : connectionRecords) {
            dataProvider.addRecord(record);
        }
    }
    @Test
    public void clearDatabase() throws SQLException {
        IInterceptionDataProvider dataProvider = new SqlLiteInterceptionDataProvider();
        List<ConnectionRecord> records = dataProvider.getRecords();
        for (ConnectionRecord record :
                records) {
            dataProvider.deleteRecord(record.id);
        }
        for (ConnectionRecord record : records) {
            dataProvider.deleteUser(record.fromUser.id);
            dataProvider.deleteUser(record.toUser.id);
        }
    }
}
