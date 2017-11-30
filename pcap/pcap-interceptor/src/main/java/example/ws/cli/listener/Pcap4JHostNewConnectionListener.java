package example.ws.cli.listener;

import example.ws.cli.listener.interfacing.Pcap4JNewConnectionInterfaceListener;
import example.ws.cli.listener.pcapHostListener.InterfaceFilter;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.*;
import java.util.concurrent.*;

public class Pcap4JHostNewConnectionListener implements INewConnectionInterfaceListener {

    private IConnectionsStorageBuilder storageBuilder;
    private Map<String, INewConnectionInterfaceListener> interfaceListeners = new HashMap<>();
    private Map<String, Future<HostPair>> runningProcesses = new HashMap<>();
    private Set<String> hadInterfacePackets = new HashSet<>();
    private ExecutorService threadPool = Executors.newWorkStealingPool();
    private Semaphore semaphore = new Semaphore(0);
    private long interfacesExploreTimeMs;
    private long lastExploreTimeMs = 0;

    public Pcap4JHostNewConnectionListener(IConnectionsStorageBuilder storageBuilder, long interfacesExploreTimeMs) {
        this.storageBuilder = storageBuilder;
        this.interfacesExploreTimeMs = interfacesExploreTimeMs;
    }

    private void selectNewInterfaces() {
        InterfaceFilter.clearUnactiveInterfaces(interfaceListeners, hadInterfacePackets, runningProcesses);
        lastExploreTimeMs = System.currentTimeMillis();
        try {
            List<PcapNetworkInterface> validInterfaces =
                    InterfaceFilter.getNewValidInterfaces(new HashSet<>(interfaceListeners.keySet()));
            for (PcapNetworkInterface pcapInterface : validInterfaces) {
                try {
                    interfaceListeners.put(pcapInterface.getName(),
                            new Pcap4JNewConnectionInterfaceListener(storageBuilder.buildStorage(), pcapInterface));
                } catch (NotOpenException e) {
                    e.printStackTrace();
                    // interface is not available
                }
            }
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public HostPair waitNextConnection() {
        long timeLeftMs = interfacesExploreTimeMs - (System.currentTimeMillis() - lastExploreTimeMs);
        if (timeLeftMs <= 0)
        {
            selectNewInterfaces();
            timeLeftMs = interfacesExploreTimeMs - (System.currentTimeMillis() - lastExploreTimeMs);
            runNewProcesses();
        }
        if (interfaceListeners.isEmpty())
        {
            try {
                TimeUnit.MILLISECONDS.sleep(interfacesExploreTimeMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        HostPair newConnection = null;
        while (newConnection == null && timeLeftMs > 0)
        {
            timeLeftMs = interfacesExploreTimeMs - (System.currentTimeMillis() - lastExploreTimeMs);
            try {
                newConnection = getNewConnection(timeLeftMs);
                return newConnection;
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    private HostPair getNewConnection(long timeLeftMs) throws InterruptedException {
        if (!semaphore.tryAcquire(timeLeftMs, TimeUnit.MILLISECONDS))
            return null;
        return findResultAndRunNewProcess();
    }

    private HostPair findResultAndRunNewProcess()
    {
        Set<String> runningProcessNames = new HashSet<>(runningProcesses.keySet());
        Future<HostPair> task = null;
        String doneInterfaceName = null;
        for (String runningInterfaceName : runningProcessNames)
        {
            task = runningProcesses.get(runningInterfaceName);
            if (task.isDone())
            {
                doneInterfaceName = runningInterfaceName;
                break;
            }
        }
        if (task == null)
            return null;

        try {
            HostPair result = task.get();
            if (result != null && (!hadInterfacePackets.contains(doneInterfaceName)))
                hadInterfacePackets.add(doneInterfaceName);
            runningProcesses.remove(doneInterfaceName);
            runNewProcess(doneInterfaceName);
            return result;
        } catch (Exception e) {
            removeInterface(doneInterfaceName);
            e.printStackTrace();
            return null;
        }
    }

    private void removeInterface(String interfaceName)
    {
        InterfaceFilter.removeInterface(interfaceName, interfaceListeners, hadInterfacePackets, runningProcesses);
    }

    private void runNewProcesses()
    {
        Set<String> registeredInterfaceNames = new HashSet<>(interfaceListeners.keySet());
        for (String registeredInterfaceName : registeredInterfaceNames) {
            if (!runningProcesses.containsKey(registeredInterfaceName))
            {
                runNewProcess(registeredInterfaceName);
            }
        }
    }

    private void runNewProcess(String interfaceName)
    {
        if (runningProcesses.containsKey(interfaceName))
        {
            return;
        }
        INewConnectionInterfaceListener listener = interfaceListeners.get(interfaceName);

        Callable<HostPair> task =  () ->
        {
            if (listener == null)
            {
                removeInterface(interfaceName);
                return null;
            }
            HostPair result = listener.waitNextConnection();
            semaphore.release();
            return result;
        };
        Future<HostPair> future = threadPool.submit(task);
        runningProcesses.put(interfaceName, future);
    }
}
