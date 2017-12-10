package wesee.vizsrv.repository.saveresult;

import wesee.vizsrv.repository.entities.DataSource;

import java.util.Map;

public class RepositorySaveResult {
    public enum SaveStateEnum {NEW, UNCHANGED, CHANGED, DELETED}
    public DataSource savedEntities;
    public Map<Object, SaveStateEnum> saveStateMap;
}
