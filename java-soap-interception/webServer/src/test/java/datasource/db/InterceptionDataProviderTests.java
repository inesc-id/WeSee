package datasource.db;

import datasource.IInterceptionDataProvider;
import datasource.db.sqlite.utils.DateConvertor;
import datasource.model.ConnectionRecord;
import datasource.model.User;
import net.minidev.asm.ConvertDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.*;


public class InterceptionDataProviderTests {


    // test multiple records for same sources
    @Test
    public void testAddSqlLite() throws SQLException {
        IInterceptionDataProvider dataProvider = new SqlLiteInterceptionDataProvider();
        Date startTime = new Date();
        List<ConnectionRecord> records = putData(dataProvider);
        Date endTime = new Date();
        List<ConnectionRecord> foundData = findData(dataProvider, records, startTime, endTime);
        Assert.assertNotNull(foundData);
        deleteData(dataProvider, foundData);
        dataProvider.close();
    }

    /** @returns list in case of the same data with filled date and ids or null */
    private List<ConnectionRecord> findData(IInterceptionDataProvider dataProvider,
                                            List<ConnectionRecord> generatedRecords,
                                            Date fromTime, Date endTime)
    {
        List<ConnectionRecord> records = dataProvider.getRecords(fromTime, endTime);
        if (records.size() != generatedRecords.size())
            return null;
        if  (records.containsAll(generatedRecords))
            return records;
        return null;
    }

    private List<ConnectionRecord> putData(IInterceptionDataProvider dataProvider)
    {
        List<ConnectionRecord> records = new ArrayList<>();
        User fromUser = new User("192.168.10.1", true);
        User toUser = new User("::1:1", false, "mega server");
        ConnectionRecord record = new ConnectionRecord(fromUser, toUser);

        dataProvider.addRecord(record);
        records.add(record);
        dataProvider.addRecord(record);
        records.add(record);
        return records;
    }

    private void deleteData(IInterceptionDataProvider dataProvider, List<ConnectionRecord> records)
    {
        Set<Integer> userIds = new HashSet<>();
        for (ConnectionRecord record: records)
        {
            userIds.add(record.fromUser.id);
            userIds.add(record.toUser.id);
            dataProvider.deleteRecord(record.id);
        }
        for (int userId : userIds) {
            dataProvider.deleteUser(userId);
        }

    }

}