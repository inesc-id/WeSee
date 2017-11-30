package interception.models.connection_models;

import java.util.Date;

public class ConnectionOccurrence {
    public String message;
    // Date to seconds standart java conversion
    public long callTimeMs;

    public Date getCallTime()
    {
        return new Date(callTimeMs);
    }
}
