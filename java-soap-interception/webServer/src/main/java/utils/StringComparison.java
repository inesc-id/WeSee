package utils;

public class StringComparison {

    public static boolean NotEmptyOrNullEquals(String val1, String val2)
    {
        if (val1 == null || val2 == null)
            return false;
        if (val1.isEmpty() || val2.isEmpty())
            return false;
        return val1.equals(val2);
    }
}
