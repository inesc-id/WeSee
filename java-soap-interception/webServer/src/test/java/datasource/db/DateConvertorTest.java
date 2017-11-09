package datasource.db;

import datasource.db.sqlite.utils.DateConvertor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class DateConvertorTest {
    @Test
    public void TestConversion()
    {
        Date dateBefore = new Date();
        Date dateAfter = new Date(dateBefore.getTime() + 2000);
        long dateIntBefore = DateConvertor.toLongInt(dateBefore);
        long dateIntAfter = DateConvertor.toLongInt(dateAfter);
        Assert.assertTrue(dateIntAfter > dateIntBefore);
        Date dateMiddle = new Date(dateBefore.getTime() + 1000);
        Date reconvertedDate = DateConvertor.ToDate(DateConvertor.toLongInt(dateMiddle));
        Assert.assertTrue(dateBefore.before(reconvertedDate) && dateAfter.after(reconvertedDate));
    }
}
