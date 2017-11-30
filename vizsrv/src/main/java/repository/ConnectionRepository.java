package repository;

import interception.models.ConnectionsEntity;
import repository.live_notificator.INewDataNotifier;
import repository.saveresult.RepositorySaveResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ConnectionRepository implements IRepositorySaver {
    private ConnectionRepository()
    {
    }

    private static ConnectionRepository singleRepository = new ConnectionRepository();
    private IRepositorySaver databaseRepository = new SqliteSaver();
    private Semaphore isBusy = new Semaphore(1);
    private List<INewDataNotifier> observers = new ArrayList<>();

    public static ConnectionRepository getSingleRepository() {
        return singleRepository;
    }

    @Override
    public RepositorySaveResult save(ConnectionsEntity connectionsEntity) {
        RepositorySaveResult saveResult = databaseRepository.save(connectionsEntity);
        notifyObservers(saveResult);
        return saveResult;
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
                observer.onDataRecieved(saveResult);
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
