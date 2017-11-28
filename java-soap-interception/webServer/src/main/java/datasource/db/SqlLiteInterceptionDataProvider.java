package datasource.db;

import datasource.IInterceptionDataProvider;
import datasource.db.sqlite.RecordControl;
import datasource.db.sqlite.UserControl;
import datasource.model.ConnectionRecord;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SqlLiteInterceptionDataProvider implements IInterceptionDataProvider
{
    public SqlLiteInterceptionDataProvider() throws SQLException {
        connect();
    }

    private RecordControl recordControl;
    private UserControl userControl;
    private void connect() throws SQLException {
        try {
            String path = new ClassPathResource("/db/interceptionDb").getFile().getPath();
            String connectionString = "jdbc:sqlite:" + path;
            userControl = new UserControl(connectionString);
            recordControl = new RecordControl(connectionString, userControl);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(e.hashCode());
        }
    }


    @Override
    public void addRecord(ConnectionRecord record) {
        recordControl.addRecord(record);
    }

    @Override
    public List<ConnectionRecord> getRecords(Date fromTime, Date untilTime) {
        return recordControl.getRecords(fromTime, untilTime);
    }

    @Override
    public List<ConnectionRecord> getRecords() {
        return recordControl.getRecords();
    }

    @Override
    public boolean deleteRecord(int id) {
        return recordControl.deleteRecord(id);
    }

    @Override
    public boolean deleteUser(int id) {
        return userControl.deleteUser(id);
    }

}
