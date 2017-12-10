package example.ws.cli.listener.interfacing;

import example.ws.cli.listener.INewConnectionInterfaceListener;
import interception.models.Connection;
import interception.models.connection_models.ConnectionOccurrence;
import interception.models.connection_models.Host;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Pcap4JNewConnectionInterfaceListener implements INewConnectionInterfaceListener {
    private PcapNetworkInterface networkInterface;
    private PcapHandle handle;

    public Pcap4JNewConnectionInterfaceListener(PcapNetworkInterface networkInterface)
            throws PcapNativeException, NotOpenException
    {
        this.networkInterface = networkInterface;
        init();
    }
    private static final int SNAPLEN = 65536;
    private static final int TIMEOUT_MS = 1000;
    private static final int BUFFER_SIZE_BYTE = 1024*1024; //1mb

    public void init() throws PcapNativeException, NotOpenException {
        PcapHandle.Builder phb
                = new PcapHandle.Builder(networkInterface.getName())
                .snaplen(SNAPLEN)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(TIMEOUT_MS)
                .bufferSize(BUFFER_SIZE_BYTE);
        handle = phb.build();

        handle.setFilter(
                "",
                BpfProgram.BpfCompileMode.OPTIMIZE
        );
    }

    @Override
    public Connection waitNextConnection() throws Exception
    {
        while (true)
        {
            Packet packet = handle.getNextPacket();
            if (packet == null)
            {
                return null;
            }
            else
            {
                IpPacket ipPacket = extractIpPacket(packet);
                if (ipPacket == null)
                    return null;
                if (!checkValidAddress(ipPacket.getHeader().getSrcAddr()) ||
                        !checkValidAddress(ipPacket.getHeader().getDstAddr()))
                    continue;
                ConnectionOccurrence connectionOccurrence = new ConnectionOccurrence();
                connectionOccurrence.callTimeMs = System.currentTimeMillis();
                connectionOccurrence.message = new String(ipPacket.getPayload().getRawData(), StandardCharsets.UTF_8);

                Connection connection = new Connection();
                connection.source = new Host(ipPacket.getHeader().getSrcAddr());
                connection.destination = new Host(ipPacket.getHeader().getDstAddr());
                connection.occurrences = new ConnectionOccurrence[]{ connectionOccurrence};

                return connection;
            }
        }
    }

    private boolean checkValidAddress(InetAddress address)
    {
        if (address == null)
            return false;
        return !address.isLoopbackAddress();
    }

    private IpPacket extractIpPacket(Packet packet)
    {
        if (packet == null)
            return null;
        if (packet instanceof IpPacket)
            return (IpPacket)packet;
        return extractIpPacket(packet.getPayload());
    }
}
