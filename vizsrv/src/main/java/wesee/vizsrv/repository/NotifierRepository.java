package wesee.vizsrv.repository;

import wesee.vizsrv.repository.live_notificator.INewDataNotifier;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotifierRepository implements IRepositoryNotifier {
    private NotifierRepository()
    {
    }

    private static NotifierRepository singleRepository = new NotifierRepository();
    private List<INewDataNotifier> observers = new CopyOnWriteArrayList<>();

    public static NotifierRepository getSingleRepository() {
        return singleRepository;
    }

    @Override
    public RepositorySaveResult notify(RepositorySaveResult repositorySaveResult) {
        notifyObservers(repositorySaveResult);
        return repositorySaveResult;
    }

    private void notifyObservers(RepositorySaveResult saveResult)
    {
        List<INewDataNotifier> allObservers = new ArrayList<>(observers);
        for (INewDataNotifier observer : allObservers) {
            if (!observer.isActive())
            {
                unsubscribe(observer);
                continue;
            }
            try {
                observer.onDataReceived(saveResult);
            } catch (Exception e)
            {
                e.printStackTrace();
                unsubscribe(observer);
            }
        }
    }

    public void subscribe(INewDataNotifier observer)
    {
        observers.add(observer);
    }

    public void unsubscribe(INewDataNotifier observer)
    {
        observers.remove(observer);
    }

}
