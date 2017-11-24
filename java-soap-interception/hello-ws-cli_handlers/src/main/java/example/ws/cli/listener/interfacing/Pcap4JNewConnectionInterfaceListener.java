package example.ws.cli.listener.interfacing;

import example.ws.cli.listener.HostPair;
import example.ws.cli.listener.IConnectionsStorage;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpPacket;
import org.pcap4j.packet.Packet;
import test.PacketsListenerTest;

import java.net.InetAddress;

public class Pcap4JNewConnectionInterfaceListener extends AbstractInterfaceNewConnectionListener {
    private PcapNetworkInterface networkInterface;
    private PcapHandle handle;

    public Pcap4JNewConnectionInterfaceListener(IConnectionsStorage connectionsStorage,
                                                PcapNetworkInterface networkInterface)
            throws PcapNativeException, NotOpenException
    {
        super(connectionsStorage);
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
    public HostPair waitNextConnection() throws Exception
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
                return putIfValidConnection(ipPacket.getHeader().getSrcAddr(), ipPacket.getHeader().getDstAddr());
            }
        }
    }

    private HostPair putIfValidConnection(InetAddress fromAddress, InetAddress toAddress)
    {
        if (!(checkValidAddress(fromAddress) && checkValidAddress(toAddress)))
            return null;
        HostPair newConnection = connectionsStorage.putNewConnection(fromAddress.getHostAddress(),
                toAddress.getHostAddress());
        return newConnection;
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
