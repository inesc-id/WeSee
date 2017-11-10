package datasource.db.sqlite.utils;

import java.util.Date;

/** sqlite does not support pure date format,
 * but it`s supported as interger number of seconds since 1970-01-01 00:00:00 UTC
  */
public class DateConvertor {
    public static long toLongInt(Date datetime)
    {
        return datetime.getTime() / 1000;
    }

    public static Date ToDate(long dateAsNumber)
    {
        return new Date(dateAsNumber * 1000);
    }

    // as the time is stored with the accuracy of 1 second, the comparison should be within 1 second
    public static boolean isNearlySame(Date date1, Date date2)
    {
        return Math.abs(date1.getTime() - date2.getTime()) < 1000;
    }
}
