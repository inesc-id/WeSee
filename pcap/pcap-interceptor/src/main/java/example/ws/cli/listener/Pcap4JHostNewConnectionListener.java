package example.ws.cli.listener;

import example.ws.cli.listener.interfacing.IInterfaceHandler;
import example.ws.cli.listener.interfacing.Pcap4JNewConnectionInterfaceListener;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Pcap4JHostNewConnectionListener implements IInterfaceHandler {

    private Map<String, IInterfaceListener> interfaceListeners = new ConcurrentHashMap<>();
    private ISenderBuilder senderBuilder;
    private long timeout;
    private String serverIp;
    private ExecutorService threadPool = Executors.newWorkStealingPool();

    public Pcap4JHostNewConnectionListener(long timeout, ISenderBuilder registratorBuilder, String serverIp) {
        this.timeout = timeout;
        this.senderBuilder = registratorBuilder;
        this.serverIp = serverIp;

    }

    public void findNewInterfaces() throws PcapNativeException {
        List<PcapNetworkInterface> newInterfaces = Pcaps.findAllDevs().stream()
                .filter(i -> !i.isLoopBack() && !interfaceListeners.containsKey(i))
                .collect(Collectors.toList());
        for (PcapNetworkInterface networkInterface : newInterfaces) {

            IInterfaceListener listener = new Pcap4JNewConnectionInterfaceListener(networkInterface,
                    this, timeout, senderBuilder.buildConnectionRegistrator(), threadPool,
                    serverIp);
            interfaceListeners.put(listener.getInterfaceName(), listener);
        }
    }

    @Override
    public void onInterfaceSignal(IInterfaceListener interfaceListener) {
        interfaceListeners.remove(interfaceListener.getInterfaceName());
    }
}
