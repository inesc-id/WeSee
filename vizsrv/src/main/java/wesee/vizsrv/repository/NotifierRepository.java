package wesee.vizsrv.repository;

import interception.models.ConnectionsEntity;
import wesee.vizsrv.repository.live_notificator.INewDataNotifier;
import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

import java.util.ArrayList;
import java.util.List;

public class NotifierRepository implements IRepositoryNotifier {
    private NotifierRepository()
    {
    }

    private static NotifierRepository singleRepository = new NotifierRepository();
//    private IRepositoryNotifier databaseRepository = new HibernateSaver();
    private List<INewDataNotifier> observers = new ArrayList<>();

    public static NotifierRepository getSingleRepository() {
        return singleRepository;
    }

    @Override
    public RepositorySaveResult save(ConnectionsEntity connectionsEntity) {
        RepositorySaveResult saveResult = new RepositorySaveResult();//databaseRepository.save(connectionsEntity);
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
