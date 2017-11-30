package interception.models.connection_models;

public class Host {
    public enum IpVersion {
        IPV4, IPV6
    }
    public String ip;
    public IpVersion version;
    public String dns;
}
