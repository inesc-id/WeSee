package example.ws.cli.listener;

import example.ws.cli.listener.interfacing.Pcap4JNewConnectionInterfaceListener;
import example.ws.cli.listener.pcapHostListener.InterfaceFilter;
import interception.models.Connection;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.*;
import java.util.concurrent.*;

public class Pcap4JHostNewConnectionListener implements INewConnectionInterfaceListener {

    private Map<String, INewConnectionInterfaceListener> interfaceListeners = new HashMap<>();
    private Map<String, Future<Connection>> runningProcesses = new ConcurrentHashMap<>();
    private Set<String> hadInterfacePackets = new HashSet<>();
    private ExecutorService threadPool = Executors.newWorkStealingPool();
    private Semaphore semaphore = new Semaphore(0);
    private long interfacesExploreTimeMs;
    private long lastExploreTimeMs = 0;

    public Pcap4JHostNewConnectionListener(long interfacesExploreTimeMs) {
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
                            new Pcap4JNewConnectionInterfaceListener(pcapInterface));
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
    public Connection waitNextConnection() {
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
        Connection newConnection = null;
        while (newConnection == null && timeLeftMs > 0)
        {
            timeLeftMs = interfacesExploreTimeMs - (System.currentTimeMillis() - lastExploreTimeMs);
            try {
                newConnection = getNewConnection(timeLeftMs);
                if (newConnection != null)
                {
                    return newConnection;
                }
            } catch (InterruptedException e) {
                return null;
            }
        }
        return null;
    }

    private Connection getNewConnection(long timeLeftMs) throws InterruptedException {
        if (!semaphore.tryAcquire(timeLeftMs, TimeUnit.MILLISECONDS))
            return null;
        return findResultAndRunNewProcess();
    }

    private Connection findResultAndRunNewProcess()
    {
        Set<String> runningProcessNames = new HashSet<>(runningProcesses.keySet());
        Future<Connection> task = null;
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
            Connection result = task.get();
            if (doneInterfaceName != null)
            {
                if (result != null && (!hadInterfacePackets.contains(doneInterfaceName)))
                    hadInterfacePackets.add(doneInterfaceName);
                runningProcesses.remove(doneInterfaceName);
                runNewProcess(doneInterfaceName);
            }
            return result;
        } catch (Exception e) {
            if (doneInterfaceName != null)
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

        Callable<Connection> task =  () ->
        {
            if (listener == null)
            {
                removeInterface(interfaceName);
                return null;
            }
            Connection result = listener.waitNextConnection();
            semaphore.release();
            return result;
        };
        Future<Connection> future = threadPool.submit(task);
        runningProcesses.put(interfaceName, future);
    }
}
