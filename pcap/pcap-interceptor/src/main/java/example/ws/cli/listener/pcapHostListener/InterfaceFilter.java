package example.ws.cli.listener.pcapHostListener;

import example.ws.cli.listener.INewConnectionInterfaceListener;
import interception.models.Connection;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;

import java.util.*;
import java.util.concurrent.Future;

public class InterfaceFilter {
    public static List<PcapNetworkInterface> getNewValidInterfaces(Set<String> existingInterfaceNames)
            throws PcapNativeException
    {
        List<PcapNetworkInterface> allInterfaces = Pcaps.findAllDevs();
        List<PcapNetworkInterface> newInterfaces = new ArrayList<>();
        for (PcapNetworkInterface pcapNetworkInterface : allInterfaces) {
            if (existingInterfaceNames.contains(pcapNetworkInterface.getName()))
                continue;
            if (pcapNetworkInterface.isLoopBack())
                continue;
            newInterfaces.add(pcapNetworkInterface);
        }
        return newInterfaces;
    }

    public static void clearUnactiveInterfaces(Map<String, INewConnectionInterfaceListener> registeredInterfaces,
                                               Set<String> activeInterfaces,
                                               Map<String, Future<Connection>> runningProcesses)
    {
        Set<String> registeredInterfaceNames = new HashSet<>(registeredInterfaces.keySet());
        for (String registeredInterfaceName: registeredInterfaceNames)
        {
            if (!(activeInterfaces.contains(registeredInterfaceName)))
            {
                removeInterface(registeredInterfaceName, registeredInterfaces, activeInterfaces, runningProcesses);
            }
        }
        activeInterfaces.clear();
    }

    public static void removeInterface(String interfaceName,
                                       Map<String, INewConnectionInterfaceListener> registeredInterfaces,
                                       Set<String> activeInterfaces,
                                       Map<String, Future<Connection>> runningProcesses)
    {
        registeredInterfaces.remove(interfaceName);
        runningProcesses.remove(interfaceName);
        activeInterfaces.remove(interfaceName);
    }
}
