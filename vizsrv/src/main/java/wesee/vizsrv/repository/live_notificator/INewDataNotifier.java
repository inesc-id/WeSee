package wesee.vizsrv.repository.live_notificator;

import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public interface INewDataNotifier {
    void onDataRecieved(RepositorySaveResult saveResult);
    boolean isActive();
}
