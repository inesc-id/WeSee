package datasource.model;

import utils.StringComparison;

public class User {

    public User()
    {

    }

    public User(String ip, boolean isIpv4)
    {
        this.ip = ip;
        this.isIpv4 = isIpv4;
    }

    public User(String ip, boolean isIpv4, String dns)
    {
        this(ip, isIpv4);
        this.dns = dns;
    }

    public int id;
    public boolean isIpv4;
    public String ip;
    public String dns;

    public boolean equals(User user)
    {
        return StringComparison.NotEmptyOrNullEquals(ip, user.ip);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof  User)
            return equals((User) obj);
        return false;
    }
}
