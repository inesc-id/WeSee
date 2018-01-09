package example.ws.cli.listener.interfacing;

import example.ws.cli.IConnectionRegistrator;
import example.ws.cli.listener.IInterfaceListener;
import example.ws.cli.listener.Pcap4JHostNewConnectionListener;
import interception.models.Connection;
import interception.models.connection_models.ConnectionOccurrence;
import interception.models.connection_models.Host;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;

import java.io.EOFException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class Pcap4JNewConnectionInterfaceListener implements IInterfaceListener {
    private PcapNetworkInterface networkInterface;
    private PcapHandle handle;
    private IInterfaceHandler interfaceInactivityHandler;
    private IConnectionRegistrator registrator;
    private static final int SNAPLEN = 65536;
    private long timeout;
    private String serverIp;
    private static final int BUFFER_SIZE_BYTE = 1024*1024; //1mb
    private ExecutorService threadPool;
    public Pcap4JNewConnectionInterfaceListener(PcapNetworkInterface networkInterface,
                                                IInterfaceHandler interfaceInactivityHandler,
                                                long timeout,
                                                IConnectionRegistrator connectionRegistrator,
                                                ExecutorService threadPool,
                                                String serverIp)
    {
        this.registrator = connectionRegistrator;
        this.threadPool = threadPool;
        this.interfaceInactivityHandler = interfaceInactivityHandler;
        this.timeout = timeout;
        this.networkInterface = networkInterface;
        this.serverIp = serverIp;
        init();
    }


    public void init() {
        PcapHandle.Builder phb
                = new PcapHandle.Builder(networkInterface.getName())
                .snaplen(SNAPLEN)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis((int)timeout)
                .bufferSize(BUFFER_SIZE_BYTE);
        try {
            handle = phb.build();
            handle.setFilter(
            "ip or ip6 ",
            BpfProgram.BpfCompileMode.OPTIMIZE
            );
        } catch (PcapNativeException e) {
            e.printStackTrace();
            interfaceInactivityHandler.onInterfaceSignal(this);
            return;
        } catch (NotOpenException e) {
            e.printStackTrace();
            interfaceInactivityHandler.onInterfaceSignal(this);
            return;
        }

        run();
    }


    public void waitNextConnection()
    {
        while (true)
        {
            try {
                final Packet packet = handle.getNextPacketEx();
                sendPacket(packet);
//                Runnable sendTask = () -> sendPacket(packet);
//                threadPool.submit(sendTask);
            } catch (NotOpenException e) {
                interfaceInactivityHandler.onInterfaceSignal(this);
                e.printStackTrace();
                return;
            } catch (PcapNativeException e) {
                interfaceInactivityHandler.onInterfaceSignal(this);
                e.printStackTrace();
                return;
            } catch (EOFException e) {
                interfaceInactivityHandler.onInterfaceSignal(this);
                e.printStackTrace();
                return;
            } catch (TimeoutException e) {
                interfaceInactivityHandler.onInterfaceSignal(this);
                e.printStackTrace();
                return;
            }
        }
    }

    private void sendPacket(Packet packet)
    {
        try
        {
            PacketInfo packetInfo = extractData(packet);
            if (!isValidAddress(packetInfo.sourceAddress) || !isValidAddress(packetInfo.targetAddress))
                return;

            String targetAddressStr = packetInfo.targetAddress.toString();
            if (targetAddressStr.equals(serverIp))
                return;
            ConnectionOccurrence connectionOccurrence = new ConnectionOccurrence();
            connectionOccurrence.callTimeMs = System.currentTimeMillis();
            connectionOccurrence.message = packetInfo.data;

            Connection connection = new Connection();
            connection.source = new Host(packetInfo.sourceAddress);
            connection.destination = new Host(packetInfo.targetAddress);
            connection.occurrences = new ConnectionOccurrence[]{ connectionOccurrence};
            registrator.registerConnection(connection);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private PacketInfo extractData(Packet packet)
    {
        PacketInfo packetInfo = new PacketInfo();
        putIpLevelInfo(packet, packetInfo);
        putPortsInfo(packet, packetInfo);
        putContentInfo(packet, packetInfo);
        return packetInfo;
    }

    private void putIpLevelInfo(Packet packet, PacketInfo outPacketInfo)
    {
        IpPacket ipPacket = extractIpPacket(packet);
        outPacketInfo.sourceAddress = ipPacket.getHeader().getSrcAddr();
        outPacketInfo.targetAddress = ipPacket.getHeader().getDstAddr();
    }

    private void putPortsInfo(Packet packet, PacketInfo outPacketInfo)
    {
        IpPacket ipPacket = extractIpPacket(packet);
        Packet insidePacket = ipPacket.getPayload();
        int sourcePort;
        int destinationPort;
        if (insidePacket instanceof TcpPacket)
        {
            TcpPacket tcpPacket = (TcpPacket)insidePacket;
            sourcePort = tcpPacket.getHeader().getSrcPort().valueAsInt();
            destinationPort = tcpPacket.getHeader().getDstPort().valueAsInt();
        }
        else if (insidePacket instanceof UdpPacket)
        {
            UdpPacket udpPacket = (UdpPacket)insidePacket;
            sourcePort = udpPacket.getHeader().getSrcPort().valueAsInt();
            destinationPort = udpPacket.getHeader().getDstPort().valueAsInt();
        }
        else return;

        outPacketInfo.data += formPortString(true, sourcePort) + " " +
                formPortString(false, destinationPort) + " ";
    }

    private void putContentInfo(Packet packet, PacketInfo outPacketInfo) {
        Packet lastPacket = getLastKnownPacket(packet);
        outPacketInfo.data += new String(lastPacket.getRawData(), StandardCharsets.UTF_8);
    }

    private String formPortString(boolean isSource, int port)
    {
        String direction = isSource? "Source port" : "Target port";
        return String.format("%s: %d", direction, port);
    }

    private boolean isValidAddress(InetAddress address)
    {
        if (address == null)
            return false;
        if (address.isLoopbackAddress())
            return false;
//        if (address.isMulticastAddress())
//            return false;
        if (address.isAnyLocalAddress())
            return false;
        return true;
    }

    private IpPacket extractIpPacket(Packet packet)
    {
        if (packet == null)
            return null;
        if (packet instanceof IpPacket)
            return (IpPacket)packet;
        return extractIpPacket(packet.getPayload());
    }

    // get rid of not useful information and get only core, ex. one level above tcp
    private Packet getLastKnownPacket(Packet packet)
    {
        Packet insidePacket = packet.getPayload();
        if (insidePacket == null)
            return packet;
        else return getLastKnownPacket(insidePacket);
    }

    @Override
    public String getInterfaceName() {
        return networkInterface.getName();
    }

    @Override
    public void run() {
        Runnable task = () -> waitNextConnection();
        threadPool.submit(task);
    }
}
