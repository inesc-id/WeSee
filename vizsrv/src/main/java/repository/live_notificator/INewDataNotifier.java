package repository.live_notificator;

import repository.saveresult.RepositorySaveResult;

public interface INewDataNotifier {
    void onDataRecieved(RepositorySaveResult saveResult);
    boolean isActive();
}
