package wesee.vizsrv.repository.hibernate.save;

import wesee.vizsrv.repository.saveresult.RepositorySaveResult;

public class EntitySaveResult<T> {
    public T entityResult;
    public RepositorySaveResult.SaveStateEnum state;

    public EntitySaveResult(T saveResult, RepositorySaveResult.SaveStateEnum state) {
        this.entityResult = saveResult;
        this.state = state;
    }
}
